package com.tpps.loginhandling;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

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
			buf.append(temp.getColumnname() + " " + temp.getTypeAsString() + " NOT NULL,");
		}			
		buf.deleteCharAt(buf.length() -1);
		buf.append(");");
		System.out.println("Table created");
		try {
			Statement stmt = this.sql.getConnection().createStatement();
			stmt.executeUpdate(buf.toString());
		} catch (SQLException e) {
			System.err.println("Table couldn't get created, Maybe it already exists");
			e.printStackTrace();
		}		
	}
	
	
	
	
	
	public static void main(String[] args) {
		String hostname = "localhost";
		String port = "3306";
		String database = "accountmanager";
		String user = "jojo";
		String password = "password";
		
		SQLHandler sql = new SQLHandler(hostname, port, user, password, database);
	//	SQLOperations op = new SQLOperations(sql);
		SQLStatisticsHandler handler = new SQLStatisticsHandler(sql);
		sql.connect();
		Statistic one = new Statistic(SQLType.VARCHAR, "4", "desc_player");
		Statistic two = new Statistic(SQLType.INT, "wins");
		Statistic tree = new Statistic(SQLType.INT, "test");
		ArrayList<Statistic> statz = new ArrayList<Statistic>();
		statz.add(one);
		statz.add(two);
		statz.add(tree);
		handler.createStatisticsTable(statz);
		sql.closeConnection();
	}
}
