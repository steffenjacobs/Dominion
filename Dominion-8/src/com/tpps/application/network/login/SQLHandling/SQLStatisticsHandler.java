package com.tpps.application.network.login.SQLHandling;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class delivers all functionalities that is needed to handle
 * all player statistics in mysql database
 * @author jhuhn - Johannes Huhn
 *
 */
public class SQLStatisticsHandler {
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * @param statistics An ArrayList of Statistic, which contains all
	 * columns(included types e.g. VARCHAR) that should implement the
	 * statistics table in the database (PRIMARY KEY is nickname, hardcoded) 
	 */
	public static void createStatisticsTable(ArrayList<Statistic> statistics){
		StringBuffer buf = new StringBuffer();
		buf.append("CREATE TABLE statistics ( \n");
		Statistic temp;
		buf.append("nickname VARCHAR(24) PRIMARY KEY NOT NULL, \n");
		for (Iterator<Statistic> iterator = statistics.iterator(); iterator.hasNext();) {			
			temp = (Statistic) iterator.next();
			//buf.append(temp.getColumnname() + " " + temp.getTypeAsString() + " NOT NULL,");
			buf.append(temp.getColumnname() + " " + temp.getTypeAsString() + " ,");
		}			
		buf.deleteCharAt(buf.length() -1);
		buf.append(");");	
		System.out.println(buf.toString());
		try {
			Statement stmt = SQLHandler.getConnection().createStatement();
			stmt.executeUpdate(buf.toString());
			System.out.println("Table created");
		} catch (SQLException e) {
			System.err.println("Table couldn't get created, Maybe it already exists");
			e.printStackTrace();
		}		
	}
	
	/**
	 * This method inserts the initial row in the statistics table. Needed for creating an account.
	 * @author jhuhn - Johannes Huhn
	 * @param nickname String representation of the account name, that is used to initial the row
	 */
	public static void insertRowForFirstLogin(String nickname){
		try {
			PreparedStatement stmt = SQLHandler.getConnection().prepareStatement("INSERT INTO statistics (nickname, description, wins, losses) VALUES (?, '', 0, 0)");
			stmt.setString(1, nickname);
			stmt.executeUpdate();
			System.out.println("Added nickname Row for statistics");
		} catch (SQLException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * This method updates the wins and losses for the player statistics in the mysql database (including the ratio)
	 * @author jhuhn - Johannes Huhn
	 * @param nickname String representation of the account name
	 * @param win boolean value, true for win, false for loss
	 */
	public static void addWinOrLoss(String nickname, boolean win){
		PreparedStatement stmt = null;
		try{
			if(win){
				stmt = SQLHandler.getConnection().prepareStatement("UPDATE statistics SET wins = wins +1 WHERE nickname = ?");
			}else{
				stmt = SQLHandler.getConnection().prepareStatement("UPDATE statistics SET losses = losses +1 WHERE nickname = ?");
			}
			stmt.setString(1, nickname);
			stmt.executeUpdate();
			updateWinLoss(nickname);
			System.out.println("Updated Wins and Losses");
		}catch(SQLException e){
			System.err.println("Error while updating win/loss db");
			e.printStackTrace();
		}		
	}
	
	/**
	 * This method sets a description for a user, can be used to save more information about the player
	 * @author jhuhn - Johannes Huhn
	 * @param nickname String representation of the account name
	 * @param description String representation that delivers more detailed information about the nickname
	 */
	public static void setDescription(String nickname, String description){
		try {
			PreparedStatement stmt = SQLHandler.getConnection().prepareStatement("UPDATE statistics SET description = ? WHERE nickname = ?;");
			stmt.setString(1, description);
			stmt.setString(2, nickname);
			stmt.executeUpdate();
			System.out.println("set description for " + nickname + "  successful");
		} catch (SQLException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * This method updates the win - loss ratio in the mysql database
	 * @author jhuhn - Johannes Huhn
	 * @param nickname String representation of the account name
	 */
	private static void updateWinLoss(String nickname){
		try {
			PreparedStatement stmtgetwinquery = SQLHandler.getConnection().prepareStatement("SELECT wins FROM statistics WHERE nickname = ?");
			stmtgetwinquery.setString(1, nickname);
			PreparedStatement stmtgetlosses = SQLHandler.getConnection().prepareStatement("SELECT losses FROM statistics WHERE nickname = ?");
			stmtgetlosses.setString(1, nickname);
			ResultSet rswin = stmtgetwinquery.executeQuery();
			rswin.next();
			ResultSet rsloss = stmtgetlosses.executeQuery();
			rsloss.next();
			int wins = rswin.getInt(1);
			int losses = rsloss.getInt(1);
			if(losses != 0){
				float ratio = (float)wins/  (float)losses;
				System.out.println(ratio);
				PreparedStatement setwinloss = SQLHandler.getConnection().prepareStatement("UPDATE statistics SET win_loss = ? WHERE nickname = ?;");
				setwinloss.setFloat(1, ratio);
				setwinloss.setString(2, nickname);
				setwinloss.executeUpdate();						
				System.out.println("Updated Win/loss successful for " + nickname);
			}
		} catch (SQLException e) {		
			e.printStackTrace();
		}
	}
	
	
//	public static void main(String[] args) {
//		String hostname = "localhost";
//		String port = "3306";
//		String database = "accountmanager";
//		String user = "jojo";
//		String password = "password";
//		
//		SQLHandler sql = new SQLHandler(hostname, port, user, password, database);
//		SQLOperations op = new SQLOperations(sql);
//		SQLStatisticsHandler handler = new SQLStatisticsHandler(sql);
//		sql.connect();
//		Statistic one = new Statistic(SQLType.VARCHAR, "40", "description");
//		Statistic two = new Statistic(SQLType.INT, "wins");
//		Statistic tree = new Statistic(SQLType.INT, "losses");
//		Statistic four = new Statistic(SQLType.FLOAT, "4,2", "win_loss");
//		ArrayList<Statistic> statz = new ArrayList<Statistic>();
//		statz.add(one);
//		statz.add(two);
//		statz.add(tree);
//		statz.add(four);
//		handler.createStatisticsTable(statz);
//		handler.insertRowForFirstLogin("kevinS");
//		handler.addWinOrLoss("kevinS", true);
//		handler.addWinOrLoss("kevinS", false);
//		handler.setDescription("kevinS", "Hacker");
//		handler.updateWinLoss("kevinS");
//		sql.closeConnection();
//	}
}
