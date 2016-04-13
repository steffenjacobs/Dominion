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
	private final int connectionPort;
	
	private PlayerScore matchmakingScore;


	public StatisticUnit getStat(String statName) {
		return this.matchmakingScore.getStatistics().get(statName);
	}

	private MPlayer(String name, UUID uid, HashMap<String, StatisticUnit> stats, int port) {
		this.playerName = name;
		this.playerUID = uid;
		this.connectionPort = port;
		this.matchmakingScore = new PlayerScore(stats);
		this.matchmakingScore.calculateMatchmakingScore();
	}

	public UUID getPlayerUID() {
		return this.playerUID;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public int getConnectionPort() {
		return connectionPort;
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
				stats.put("LAST_GAMES_TIMES", new StatisticUnit(SQLStatisticsHandler.getPlaytimeDatesAsLongArray(res.getString("LAST_TIME_PLAYED"))));
				stats.put("LAST_GAMES_WINS", new StatisticUnit(SQLStatisticsHandler.getLastTimeWinsParsed(res.getString("LAST_TIME_WINS"))));
				return new MPlayer(request.getPlayerName(), request.getPlayerID(), stats, port);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getScore() {
		return this.matchmakingScore.getScore();
	}
}