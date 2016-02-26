package com.tpps.application.network.login.SQLHandling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author jhuhn - Johannes Huhn
 * This class delivers basic functionalities required to handle a MYSQL database like connecting or disconnecting to a specific mysql server 
 */
public class SQLHandler {
		
	private String host;
	private String port;
	private String username;
	private String password;
	private String database;
	private Connection connection;
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * Initialize the Object
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @param database
	 */
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
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * test if the client is still connected with the mysql database
	 * @return true if the connection is valid, false else
	 */
	public boolean isConnected() {
		try {
			return (connection != null) && (connection.isValid(1));
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * This method connects to the mysql server and to a specific database
	 * Furthermore this method tests if there is still a valid connection, if not the method connects again.
	 */
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
			System.out.println("Driver not found!");
		}
		System.out.println("Connected to MySQL-Server successfully");
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * This method closes the connection to the mysql server properly
	 */
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
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * gets the connection
	 * @return a valif connection object, that is used to query mysql statements
	 */
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
