package com.tpps.technicalServices.network.matchmaking.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import com.tpps.technicalServices.network.login.SQLHandling.SQLStatisticsHandler;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingRequest;

public class MPlayer {

	private final String playerName;
	private final UUID playerUID;
	private final HashMap<String, StatisticUnit> statistics;
	private final int connectionPort;

	private int mmScore = 0;

	private static final long MILLIS_24_HOURS = 86400000;

	/** a score representing the player's performance over the last 10 games */
	private int getLastWinsCount() {
		boolean[] lastGamesWins = statistics.get("LAST_GAMES_WINS").asBooleanArray();
		int wins = 0;
		for (boolean b : lastGamesWins) {
			if (b)
				wins++;
		}
		return wins;
	}

	/**
	 * calculates a lower score, if the player is new to the game
	 */
	private int getGamesCountModifier() {

		long[] lastGames = statistics.get("LAST_GAMES_TIMES").asLongArray();

		/* not played 5 games yet */
		if (lastGames.length < 5) {
			this.mmScore = 0;
			return 1;
		}
		/* not played 10 games yet */
		else if (lastGames.length < 10) {
			this.mmScore = 1;
			return 2;
		} else {
			// played more then 10 games
			return 10;
		}
	}

	/** calculates a score representing the last time the player played */
	private int getLastTimePlayedModifier() {

		long[] lastGames = statistics.get("LAST_GAMES_TIMES").asLongArray();
		/* score for the time the last 10 games were played in */

		/* last played */
		if (System.currentTimeMillis() - lastGames[0] < MILLIS_24_HOURS) {
			// played already in the last 24 hours
			return 10;
		} else if (System.currentTimeMillis() - lastGames[0] < MILLIS_24_HOURS * 2) {
			// played already in the last 48 hours
			return 5;
		} else if (System.currentTimeMillis() - lastGames[0] < MILLIS_24_HOURS * 7) {
			// played already in the last week
			return 3;
		} else if (System.currentTimeMillis() - lastGames[0] < MILLIS_24_HOURS * 30) {
			// played already in the last month
			return 2;
		} else {
			// not played since at least 30 days
			return 1;
		}

	}

	/**
	 * calculates a score representing the time-intervals the player played the
	 * last 10 games in
	 */
	private int getTimeIntervalModifier() {

		long[] lastGames = statistics.get("LAST_GAMES_TIMES").asLongArray();

		long totalTimeForTheLastTenGames = System.currentTimeMillis() - lastGames[9];

		long maxTimeBetweenTwoGames = System.currentTimeMillis() - lastGames[0], minDeviation = maxTimeBetweenTwoGames,
				deviation;
		for (int i = 1; i < lastGames.length; i++) {
			deviation = lastGames[i - 1] - lastGames[i];
			if (maxTimeBetweenTwoGames < deviation) {
				maxTimeBetweenTwoGames = deviation;
			} else if (minDeviation > deviation) {
				minDeviation = deviation;
			}
		}

		totalTimeForTheLastTenGames /= MILLIS_24_HOURS;
		maxTimeBetweenTwoGames /= MILLIS_24_HOURS;

		if (totalTimeForTheLastTenGames == 0) {
			// 10 or more games in the last 24 hours -> >=10 games/day
			return 10;
		} else if (totalTimeForTheLastTenGames <= 1) {
			// 10 or more games in the last 2 days -> ~5 games/day
			return 8;
		} else if (totalTimeForTheLastTenGames <= 2) {
			// 10 games in the last 3 days -> ~ 3-4 games/day
			return 6;
		} else if (totalTimeForTheLastTenGames <= 5) {
			// 10 games in the last 6 days
			if (maxTimeBetweenTwoGames >= 2) {
				// two day pause -> ~2-3 games/day
				return 5;
			} else {
				// ~2 games/day
				return 4;
			}
		} else if (totalTimeForTheLastTenGames <= 14) {
			// 10 games in the last 14 days
			if (maxTimeBetweenTwoGames > 2) {
				// most likely 2 games/day or less
				return 2;
			} else if (maxTimeBetweenTwoGames > 5) {
				// maybe usually more games per day, but 5 days pause
				return 4;
			} else if (maxTimeBetweenTwoGames > 10) {
				// long pause, now back
				return 7;
			}
		}
		// usually <1game/day
		return 1;
	}

	/** calculates how many times a user played today */
	private int getTimesPlayedToday() {
		long[] lastGames = statistics.get("LAST_GAMES_TIMES").asLongArray();
		int count = 0;
		for (int i = 0; i < lastGames.length; i++) {
			if (System.currentTimeMillis() - lastGames[i] < MILLIS_24_HOURS) {
				count++;
			} else
				break;
		}
		return count;
	}

	/** calculates a modifier based on the times the player played already */
	private int getTotalTimesPlayedModifier() {
		int timesPlayedModifier = statistics.get("TIMES_PLAYED").asInteger() / 50;
		return timesPlayedModifier > 10 ? 10 : timesPlayedModifier;
	}

	/**
	 * calculates the fraction of the games that were wins and creates a
	 * modifier based on that
	 */
	private int getWinLossModifier() {
		double winFraction = (double) statistics.get("WINS").asInteger()
				/ (statistics.get("WINS").asInteger() + statistics.get("LOSSES").asInteger());

		return (int) Math.round(winFraction * 10);

	}

	public int calculateMatchmakingScore() {
		/* did the player play <5 or less <10 games */
		this.mmScore = 100 * this.getGamesCountModifier();

		/* how many games did the player play today */
		this.mmScore += 5 * this.getTimesPlayedToday();

		/* how many games of the last ten were won */
		this.mmScore += 7 * this.getLastWinsCount();

		/* how many games had the player played yet */
		this.mmScore += 15 * this.getTotalTimesPlayedModifier();

		/* how many games does the player really win */
		this.mmScore += 10 * this.getWinLossModifier();

		/* when did the player played his last game */
		this.mmScore += 12 * this.getLastTimePlayedModifier();

		/* how many times does the player usually play per day */
		this.mmScore += 15 * this.getTimeIntervalModifier();

		return this.mmScore;
	}

	public StatisticUnit getStat(String statName) {
		return this.statistics.get(statName);
	}

	private MPlayer(String name, UUID uid, HashMap<String, StatisticUnit> stats, int port) {
		this.playerName = name;
		this.playerUID = uid;
		this.statistics = stats;
		this.connectionPort = port;
	}

	public UUID getPlayerUID() {
		return this.playerUID;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public HashMap<String, StatisticUnit> getStatistics() {
		return this.statistics;
	}

	public int getConnectionPort() {
		return connectionPort;
	}

	private static class StatisticUnit {
		private Object obj;

		public StatisticUnit(Object stat) {
			this.obj = stat;
		}

		public int asInteger() {
			return (int) obj;
		}

		public long[] asLongArray() {
			return (long[]) obj;
		}

		public boolean[] asBooleanArray() {
			return (boolean[]) obj;
		}
	}

	public static MPlayer initialize(PacketMatchmakingRequest request, int port) {
		try {
			ResultSet res = SQLStatisticsHandler.getStatisticsForPlayer(request.getPlayerName());

			if (res.next()) {
				HashMap<String, StatisticUnit> stats = new HashMap<>();
				stats.put("WINS", new StatisticUnit(res.getInt("wins")));
				stats.put("LOSSES", new StatisticUnit(res.getInt("losses")));
				stats.put("WIN_LOSS_RATIO", new StatisticUnit(res.getDouble("win_loss")));
				stats.put("TIMES_PLAYED", new StatisticUnit(res.getInt("games_played")));
				stats.put("RANK", new StatisticUnit(res.getInt("rank")));
				stats.put("TIME_PLAYED", new StatisticUnit(res.getInt("playtime")));
				stats.put("LAST_GAMES_TIMES", new StatisticUnit(res.getArray("last_played_times").getArray()));
				stats.put("LAST_GAMES_WINS", new StatisticUnit(res.getArray("last_played_wins").getArray()));
				return new MPlayer(request.getPlayerName(), request.getPlayerID(), stats, port);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
