package com.tpps.technicalServices.network.login.SQLHandling;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.tpps.technicalServices.network.login.server.LoginServer;

/**
 * This class delivers all functionalities that is needed to handle all player
 * statistics in mysql database
 * 
 * @author jhuhn - Johannes Huhn
 *
 */
public class SQLStatisticsHandler {

	/**
	 * @author jhuhn - Johannes Huhn
	 * @param statistics
	 *            An ArrayList of Statistic, which contains all columns(included
	 *            types e.g. VARCHAR) that should implement the statistics table
	 *            in the database (PRIMARY KEY is nickname, hardcoded)
	 */
	public static void createStatisticsTable(ArrayList<Statistic> statistics) {
		StringBuffer buf = new StringBuffer();
		buf.append("CREATE TABLE statistics ( \n");
		Statistic temp;
		buf.append("nickname NVARCHAR(24) PRIMARY KEY NOT NULL, \n");
		for (Iterator<Statistic> iterator = statistics.iterator(); iterator.hasNext();) {
			temp = (Statistic) iterator.next();
			// buf.append(temp.getColumnname() + " " + temp.getTypeAsString() +
			// " NOT NULL,");
			buf.append(temp.getColumnname() + " " + temp.getTypeAsString() + " ,");
		}
		buf.deleteCharAt(buf.length() - 1);
		buf.append(");");
		System.out.println(buf.toString());
		try {
			Statement stmt = SQLHandler.getConnection().createStatement();
			stmt.executeUpdate(buf.toString());
			System.out.println("Table statistics created");
		} catch (SQLException e) {
			System.err.println("Table couldn't get created, Maybe it already exists");
			e.printStackTrace();
		}
	}

	/**
	 * This method inserts the initial row in the statistics table. Needed for
	 * creating an account.
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param nickname
	 *            String representation of the account name, that is used to
	 *            initial the row
	 */
	public static void insertRowForFirstLogin(String nickname) {
		try {
			PreparedStatement stmt = SQLHandler.getConnection().prepareStatement(
					"INSERT INTO statistics (nickname, wins, losses, win_loss, games_played, rank, playtime) VALUES (?, 0, 0, 0, 0,'silver', 0)");
			stmt.setString(1, nickname);
			stmt.executeUpdate();
			System.out.println("Added nickname Row for statistics");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static long getPlaytime(String nickname) {
		PreparedStatement stmt;
		try {
			stmt = SQLHandler.getConnection().prepareStatement("SELECT playtime FROM statistics WHERE nickname = ?");
			stmt.setString(1, nickname);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getInt("playtime");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void addPlaytime(String nickname, long time) {
		long oldPlaytime = getPlaytime(nickname);
		oldPlaytime += time;

		try {
			PreparedStatement stmt = SQLHandler.getConnection()
					.prepareStatement("UPDATE statistics SET playtime = ?  WHERE nickname = ?");
			stmt.setLong(1, oldPlaytime);
			stmt.setString(2, nickname);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getGamesPlayed(String nickname) {
		PreparedStatement stmt;
		try {
			stmt = SQLHandler.getConnection()
					.prepareStatement("SELECT games_played FROM statistics WHERE nickname = ?");
			stmt.setString(1, nickname);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getInt("games_played");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static void incrementGamesPlayed(String nickname) {
		try {
			PreparedStatement stmt = SQLHandler.getConnection()
					.prepareStatement("UPDATE statistics SET games_played = games_played + 1 WHERE nickname = ?");
			stmt.setString(1, nickname);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method updates the wins and losses for the player statistics in the
	 * mysql database (including the ratio)
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param nickname
	 *            String representation of the account name
	 * @param win
	 *            boolean value, true for win, false for loss
	 */
	public static void addWinOrLoss(String nickname, boolean win) {
		PreparedStatement stmt = null;
		try {
			if (win) {
				stmt = SQLHandler.getConnection()
						.prepareStatement("UPDATE statistics SET wins = wins +1 WHERE nickname = ?");
			} else {
				stmt = SQLHandler.getConnection()
						.prepareStatement("UPDATE statistics SET losses = losses +1 WHERE nickname = ?");
			}
			stmt.setString(1, nickname);
			stmt.executeUpdate();
			updateWinLoss(nickname);
			incrementGamesPlayed(nickname);
			System.out.println("Updated Wins and Losses");
		} catch (SQLException e) {
			System.err.println("Error while updating win/loss db");
			e.printStackTrace();
		}
	}

	public static String getRank(String nickname) {
		try {
			PreparedStatement stmt = SQLHandler.getConnection()
					.prepareStatement("SELECT rank FROM statistics WHERE nickname = ?");
			stmt.setString(1, nickname);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getString("rank");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setRank(String nickname, String rank) {
		try {
			PreparedStatement stmt = SQLHandler.getConnection()
					.prepareStatement("UPDATE statistics SET rank = ? WHERE nickname = ?");
			stmt.setString(1, rank);
			stmt.setString(2, nickname);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method updates the win - loss ratio in the mysql database
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param nickname
	 *            String representation of the account name
	 */
	private static void updateWinLoss(String nickname) {
		try {
			PreparedStatement stmtgetwinquery = SQLHandler.getConnection()
					.prepareStatement("SELECT wins FROM statistics WHERE nickname = ?");
			stmtgetwinquery.setString(1, nickname);
			PreparedStatement stmtgetlosses = SQLHandler.getConnection()
					.prepareStatement("SELECT losses FROM statistics WHERE nickname = ?");
			stmtgetlosses.setString(1, nickname);
			ResultSet rswin = stmtgetwinquery.executeQuery();
			rswin.next();
			ResultSet rsloss = stmtgetlosses.executeQuery();
			rsloss.next();
			int wins = rswin.getInt(1);
			int losses = rsloss.getInt(1);
			if (losses != 0) {
				double ratio = (double) wins / (double) losses;
				// System.out.println(ratio);
				PreparedStatement setwinloss = SQLHandler.getConnection()
						.prepareStatement("UPDATE statistics SET win_loss = ? WHERE nickname = ?;");
				setwinloss.setDouble(1, ratio);
				setwinloss.setString(2, nickname);
				setwinloss.executeUpdate();
			} else {
				PreparedStatement setwinloss = SQLHandler.getConnection()
						.prepareStatement("UPDATE statistics SET win_loss = 0 WHERE nickname = ?;");
				setwinloss.setString(1, nickname);
				setwinloss.executeUpdate();
			}
			System.out.println("Updated Win/loss successful for " + nickname);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getWins(String nickname) {
		try {
			PreparedStatement stmt = SQLHandler.getConnection()
					.prepareStatement("SELECT wins FROM statistics WHERE nickname = ?");
			stmt.setString(1, nickname);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getInt("wins");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int getLosses(String nickname) {
		try {
			PreparedStatement stmt = SQLHandler.getConnection()
					.prepareStatement("SELECT losses FROM statistics WHERE nickname = ?");
			stmt.setString(1, nickname);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getInt("losses");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static double getWinLossRatio(String nickname) {
		PreparedStatement stmt;
		try {
			stmt = SQLHandler.getConnection().prepareStatement("SELECT win_loss FROM statistics WHERE nickname = ?");
			stmt.setString(1, nickname);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getDouble("win_loss");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static String[][] getAllStatistics() {
		try {
			Statement stmt = SQLHandler.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM statistics");
			rs.last();

			int size = rs.getRow();
			rs.beforeFirst();

			// System.out.println(size);
			String[][] statz = new String[size][7];

			int height = 0;
			int width = 0;
			while (rs.next()) {
				String nick = rs.getString("nickname");
				int wins = rs.getInt("wins");
				int losses = rs.getInt("losses");
				double ratio = rs.getDouble("win_loss");
				int games_played = rs.getInt("games_played");
				String rank = rs.getString("rank");
				long playtime = rs.getLong("playtime");
				// System.out.println(nick);

				statz[height][width] = nick;
				statz[height][++width] = "" + wins;
				statz[height][++width] = "" + losses;
				statz[height][++width] = "" + ratio;
				statz[height][++width] = "" + games_played;
				statz[height][++width] = rank;
				statz[height][++width] = "" + playtime;
				height++;
				width = 0;
			}
			return statz;

		} catch (SQLException e) {
			e.printStackTrace();
			return new String[][] { { "ERROR", "OCCURED", "WHILE", "LOADING", "ALL", "STATISTICS", "SRY" } };
		}
	}

	/**
	 * interface with the database for matchmaking-system
	 * 
	 * @author Steffen Jacobs
	 * @return the ResultSet directly from the database that contains all
	 *         statistics fot a given player
	 * @throws SQLException
	 */
	public static ResultSet getStatisticsForPlayer(String playerName) throws SQLException {

		PreparedStatement stmt = SQLHandler.getConnection()
				.prepareStatement("SELECT * FROM statistics WHERE nickname = ?");
		stmt.setString(1, playerName);
		return stmt.executeQuery();
	}
	
	public static void addPlaytimeArray(String nickname, long[] playtime){
		PreparedStatement stmt;
		String text = "";
		for (int i = 0; i < playtime.length; i++) {
			text += (playtime[i] + "|");
		}
		try {
			stmt = SQLHandler.getConnection().prepareStatement("UPDATE statistics SET LAST_TIME_PLAYED = ? WHERE nickname = ?;");
			stmt.setString(1, text);
			stmt.setString(2, nickname);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static long[] getPlaytimeArray(String nickname){
		try {
			PreparedStatement stmt = SQLHandler.getConnection().prepareStatement("SELECT LAST_TIME_PLAYED FROM statistics WHERE nickname = ?");
			ResultSet rs = stmt.executeQuery();
			rs.next();
			String text = rs.getString("LAST_TIME_PLAYED");
			String[] values = text.split("|");
			long[] dates = new long[10];
			for (int i = 0; i < values.length; i++) {
				dates[i] = Long.parseLong(values[i]);
			}
			return dates;
		} catch (SQLException e) {		
			e.printStackTrace();
		}
		return new long[]{};
	}
	
	public static void insertPlaytimeToArray(String nickname, long playtime){
//		try {
//			PreparedStatement stmt = SQLHandler.getConnection().prepareStatement("SELECT LAST_TIME_PLAYED FROM statistics WHERE nickname = ?");
//			ResultSet rs = stmt.executeQuery();
//			rs.next();
//			String text = rs.getString("LAST_TIME_PLAYED");
//			String[] array = text.split("|");
//			
//		} catch (SQLException e) {		
//			e.printStackTrace();
//		}
	}
	
	public static void main(String[] args) {
		String hostname = "localhost";
		String port = "3306";
		String database = "accountmanager";
		String user = "jojo";
		String password = "password";
		SQLHandler.init(hostname, port, user, password, database);
		SQLHandler.connect();
		String name = "name";
		SQLOperations.createAccount(name, "rsdgdrgdr", "xxx", "yyyy");
		SQLStatisticsHandler.insertRowForFirstLogin(name);
		
		long[] array = new long[13];
		for (int i = 0; i < array.length; i++) {
			array[i] = 1460497370656L + i;

		}
		
		SQLStatisticsHandler.addPlaytimeArray(name, array);
	}
}
