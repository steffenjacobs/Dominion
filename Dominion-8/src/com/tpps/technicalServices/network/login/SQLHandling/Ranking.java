package com.tpps.technicalServices.network.login.SQLHandling;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.tpps.technicalServices.network.matchmaking.server.PlayerMatchmakingScore;
import com.tpps.technicalServices.network.matchmaking.server.StatisticUnit;

import sun.misc.BASE64Encoder;

/** @author Steffen Jacobs, Johannes Huhn */
public final class Ranking {

	private static final ArrayList<String> special;

	static {
		special = new ArrayList<>();
		special.add("bmlzaGl0");
		special.add("cmFscGg=");
		special.add("c3RlZmFu");
		special.add("Z2VyYmln");
	}

	private Ranking() {
		throw new AssertionError();
	}

	/**
	 * updates the player-rank in the database
	 * 
	 * @param playerName
	 *            the name of the player to update the rank of
	 * @throws SQLException
	 */
	public static void udpatePlayerRank(String playerName) throws SQLException {
		ResultSet res = SQLStatisticsHandler.getStatisticsForPlayer(playerName);
		if (res.next()) {
			HashMap<String, StatisticUnit> stats = new HashMap<>();
			stats.put("WINS", new StatisticUnit(res.getInt("wins")));
			stats.put("LOSSES", new StatisticUnit(res.getInt("losses")));
			stats.put("WIN_LOSS_RATIO", new StatisticUnit(res.getDouble("win_loss")));
			stats.put("TIMES_PLAYED", new StatisticUnit(res.getInt("games_played")));
			stats.put("RANK", new StatisticUnit(res.getInt("rank")));
			stats.put("TIME_PLAYED", new StatisticUnit(res.getLong("playtime")));
			stats.put("LAST_GAMES_TIMES",
					new StatisticUnit(SQLStatisticsHandler.getPlaytimeDatesParsed(res.getString("LAST_TIME_PLAYED"))));
			stats.put("LAST_GAMES_WINS",
					new StatisticUnit(SQLStatisticsHandler.getLastTimeWinsParsed(res.getString("LAST_TIME_WINS"))));

			PlayerMatchmakingScore pms = new PlayerMatchmakingScore(stats);
			int score = pms.calculateMatchmakingScore();
			SQLStatisticsHandler.setRank(playerName, score);
		}
	}

	/**
	 * @param playerName
	 *            the name of the player to check
	 * @return true if player is special
	 */
	private static boolean isSpecial(String playerName) {
		return special.contains(new String(new BASE64Encoder().encode(playerName.toLowerCase().getBytes())));
	}

	/**
	 * @param playerName
	 *            the name of the player
	 * @param score
	 *            the matchmaking-score to calculate the rank-name from
	 * @return the rank-string from a player
	 */
	public static String getRankByScore(String playerName, int score) {
		if (isSpecial(playerName)) {
			return "forever Lurch";
		}
		if (score < 190)
			return "Noob";
		if (score < 210)
			return "Silver-Noob";
		if (score < 240)
			return "Gold Nova";
		if(score < 260)
			return "Master Guardian";
		if(score < 280)
			return "Legendary";
		if(score < 300)
			return "Supreme Master";
		if (score < 350) {
			return "Global Elite";
		}
		return "Blood Diamond";
	}

}
