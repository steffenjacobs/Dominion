package com.tpps.loginhandling;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

//TODO: comment
public class SQLStatisticsHandler {

	private SQLHandler sql;
	
	public SQLStatisticsHandler(SQLHandler sql){
		this.sql = sql;
	}
	
	public void createStatisticsTable(ArrayList<Statistic> statistics){
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
			Statement stmt = this.sql.getConnection().createStatement();
			stmt.executeUpdate(buf.toString());
			System.out.println("Table created");
		} catch (SQLException e) {
			System.err.println("Table couldn't get created, Maybe it already exists");
			e.printStackTrace();
		}		
	}
	
	public void insertRowForFirstLogin(String nickname){
		try {
			PreparedStatement stmt = this.sql.getConnection().prepareStatement("INSERT INTO statistics (nickname, description, wins, losses) VALUES (?, '', 0, 0)");
			stmt.setString(1, nickname);
			stmt.executeUpdate();
			System.out.println("Added nickname Row for statistics");
		} catch (SQLException e) {		
			e.printStackTrace();
		}
	}
	
	public void addWinOrLoss(String nickname, boolean win){
		PreparedStatement stmt = null;
		try{
			if(win){
				stmt = this.sql.getConnection().prepareStatement("UPDATE statistics SET wins = wins +1 WHERE nickname = ?");
			}else{
				stmt = this.sql.getConnection().prepareStatement("UPDATE statistics SET losses = losses +1 WHERE nickname = ?");
			}
			stmt.setString(1, nickname);
			stmt.executeUpdate();
			this.updateWinLoss(nickname);
			System.out.println("Updated Wins and Losses");
		}catch(SQLException e){
			System.err.println("Error while updating win/loss db");
			e.printStackTrace();
		}		
	}
	
	public void setDescription(String nickname, String description){
		try {
			PreparedStatement stmt = this.sql.getConnection().prepareStatement("UPDATE statistics SET description = ? WHERE nickname = ?;");
			stmt.setString(1, description);
			stmt.setString(2, nickname);
			stmt.executeUpdate();
			System.out.println("set description for " + nickname + "  successful");
		} catch (SQLException e) {		
			e.printStackTrace();
		}
	}
	
	public void updateWinLoss(String nickname){
		try {
			PreparedStatement stmtgetwinquery = this.sql.getConnection().prepareStatement("SELECT wins FROM statistics WHERE nickname = ?");
			stmtgetwinquery.setString(1, nickname);
			PreparedStatement stmtgetlosses = this.sql.getConnection().prepareStatement("SELECT losses FROM statistics WHERE nickname = ?");
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
				PreparedStatement setwinloss = this.sql.getConnection().prepareStatement("UPDATE statistics SET win_loss = ? WHERE nickname = ?;");
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
