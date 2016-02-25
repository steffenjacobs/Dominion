package com.tpps.loginhandling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHandler {
		
	private String host;
	private String port;
	private String username;
	private String password;
	private String database;
	
	private Connection connection;
	
	public SQLHandler(String host, String port, String username, String password, String database){
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;
		this.connection = null;
	}
	
//	public void init(String host, String port, String username, String password, String database){
//		this.host = host;
//		this.port = port;
//		this.username = username;
//		this.password = password;
//		this.database = database;
//	}
	
	
	public boolean isConnected() {
		try {
			return (connection != null) && (connection.isValid(1));
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public void connect() {
		if (isConnected()) {
			try {
				connection.close();
				System.out.println("Close MySQL connection");
			}
			catch (SQLException | NullPointerException e) { }
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
		}
		catch (SQLException e) {
			System.out.println("Could not connect to MySQL server! Exception: " + e.getMessage());
		}
		catch (ClassNotFoundException e) {
			System.out.println("JDBC Driver not found!");
		}
		System.out.println("Connected to MySQL-Server successfully");
	}
	
	
	public void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
				this.connection = null;
				System.out.println("Closed Connection to MySQL-Server successfully");
			}
			catch (SQLException e) {
				System.out.println("Error closing the MySQL Connection!");
				e.printStackTrace();
			}
		}
	}
	
	public Connection getConnection() {
		if (isConnected()) {
			return connection;
		}
		else {
			System.out.println("Lost MySQL Connection. Reconnecting...");
			this.connect();
			return connection;
		}
	}
}
