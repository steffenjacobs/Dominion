package com.tpps.technicalServices.network.matchmaking.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.network.login.SQLHandling.SQLStatisticsHandler;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingRequest;

/**
 * this represents a Matchmaking-Player (a player searching for a match)
 * 
 * @author Steffen Jacobs
 */
public class MPlayer {

	private final String playerName;
	private final UUID playerUID;
	private final int connectionPort;
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "MPlayer_" + System.identityHashCode(this) + " - " + playerName + " [" + playerUID.toString() + "] @" + connectionPort + " AI:" + this.isAI();  
	}

	private PlayerMatchmakingScore matchmakingScore;

	/**
	 * constructor for the Matchmaking-Player
	 * 
	 * @param name
	 *            the name of the player
	 * @param uid
	 *            the uuid of the player
	 * @param stats
	 *            a HashMap of the player's stats retrieved from the databse
	 * @param port
	 *            the port the player is connected with
	 * @param ai
	 *            determines, whether the linked Player is an AI or not
	 */
	private MPlayer(String name, UUID uid, HashMap<String, StatisticUnit> stats, int port) {
		this.playerName = name;
		this.playerUID = uid;
		this.connectionPort = port;
		this.matchmakingScore = new PlayerMatchmakingScore(stats);
		if (!this.isAI())
			this.matchmakingScore.calculateMatchmakingScore();
	}

	/** @return the uuid of the player */
	public UUID getPlayerUID() {
		return this.playerUID;
	}

	/** @return the name of the player */
	public String getPlayerName() {
		return this.playerName;
	}

	/** @return the port of the player */
	public int getConnectionPort() {
		return connectionPort;
	}

	/** @return the matchmaking-score of the player */
	public int getScore() {
		if (this.isAI()) {
			return 1;
		} else {
			return this.matchmakingScore.getScore();
		}
	}

	/**
	 * @return the requested stat
	 * @param statName
	 *            name of the requested stat
	 */
	public StatisticUnit getStat(String statName) {
		return this.matchmakingScore.getStatistics().get(statName);
	}

	/**
	 * static method, called to get an instance of the MPlayer, automatically
	 * retrieves all relevant information from the database
	 * 
	 * @param request
	 *            the request-packet received by the server containing
	 *            player-information
	 * @param port
	 *            the port the player is connected with
	 * @return a new instance created from the request and filled with data from
	 *         the data-base
	 */
	public static MPlayer initialize(PacketMatchmakingRequest request, int port) {
		if (!DominionController.isOffline()) {
			try {
				ResultSet res = SQLStatisticsHandler.getStatisticsForPlayer(request.getPlayerName());
				if (res.next()) {
					HashMap<String, StatisticUnit> stats = new HashMap<>();
					stats.put("WINS", new StatisticUnit(res.getInt("wins")));
					stats.put("LOSSES", new StatisticUnit(res.getInt("losses")));
					stats.put("WIN_LOSS_RATIO", new StatisticUnit(res.getDouble("win_loss")));
					stats.put("TIMES_PLAYED", new StatisticUnit(res.getInt("games_played")));
					stats.put("RANK", new StatisticUnit(res.getInt("rank")));
					stats.put("TIME_PLAYED", new StatisticUnit(res.getLong("playtime")));
					stats.put("LAST_GAMES_TIMES", new StatisticUnit(
							SQLStatisticsHandler.getPlaytimeDatesParsed(res.getString("LAST_TIME_PLAYED"))));
					stats.put("LAST_GAMES_WINS", new StatisticUnit(
							SQLStatisticsHandler.getLastTimeWinsParsed(res.getString("LAST_TIME_WINS"))));
					return new MPlayer(request.getPlayerName(), request.getPlayerID(), stats, port);
				} else {
					return new MPlayer(request.getPlayerName(), request.getPlayerID(), null, 0);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		} else {
			HashMap<String, StatisticUnit> stats = new HashMap<>();
			stats.put("WINS", new StatisticUnit(0));
			stats.put("LOSSES", new StatisticUnit(0));
			stats.put("WIN_LOSS_RATIO", new StatisticUnit(0d));
			stats.put("TIMES_PLAYED", new StatisticUnit(0));
			stats.put("RANK", new StatisticUnit(0));
			stats.put("TIME_PLAYED", new StatisticUnit(0));
			stats.put("LAST_GAMES_TIMES", new StatisticUnit(new long[0]));
			stats.put("LAST_GAMES_WINS", new StatisticUnit(new boolean[0]));
			return new MPlayer(request.getPlayerName(), request.getPlayerID(), stats, port);
		}
	}

	/** @return true: if this is an AI-Player, false: otherwise */
	public boolean isAI() {
		return UUID.fromString("00000000-0000-0000-0000-000000000000").equals(this.playerUID);
	}
}
