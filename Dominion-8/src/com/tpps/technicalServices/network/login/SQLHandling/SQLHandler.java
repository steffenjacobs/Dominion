package com.tpps.technicalServices.network.login.SQLHandling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class delivers basic functionalities required to handle a MYSQL database
 * like connecting or disconnecting to a specific mysql server
 * 
 * @author jhuhn - Johannes Huhn
 */
public class SQLHandler {
		
	private static String host;
	private static String port;
	private static String username;
	private static String password;
	private static String database;
	private static Connection connection;
	
	/**
	 * Initialize the Object
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param host
	 *            String representation of the host
	 * @param port
	 *            String representation of the databases port
	 * @param username
	 *            String representation of the databases username
	 * @param password
	 *            String representation of the databases password
	 * @param database
	 *            String representation of the specific database
	 */
	public static void init(String host, String port, String username, String password, String database){
		SQLHandler.host = host;
		SQLHandler.port = port;
		SQLHandler.username = username;
		SQLHandler.password = password;
		SQLHandler.database = database;
		SQLHandler.connection = null;	
	}
	
	/**
	 * test if the client is still connected with the mysql database
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @return true if the connection is valid, false else
	 */
	public static boolean isConnected() {
		try {
			return (connection != null) && (connection.isValid(1));
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * This method connects to the mysql server and to a specific database
	 * Furthermore this method tests if there is still a valid connection, if
	 * not the method connects again.
	 * 
	 * @author jhuhn - Johannes Huhn
	 */
	public static void connect() {
		if (isConnected()) {
			try {
				connection.close();
				System.out.println("Close MySQL connection");
			}
			catch (SQLException | NullPointerException e) { }
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://"
					+ SQLHandler.host + ":" + SQLHandler.port + "/"
					+ SQLHandler.database + "?useSSL=false",
					SQLHandler.username, SQLHandler.password);
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
	 * This method closes the connection to the mysql server properly
	 * 
	 * @author jhuhn - Johannes Huhn
	 */
	public static void closeConnection() {
		if (SQLHandler.connection != null) {
			try {
				SQLHandler.connection.close();
				SQLHandler.connection = null;
				System.out.println("Closed Connection to MySQL-Server successfully");
			}
			catch (SQLException e) {
				System.out.println("Error closing the MySQL Connection!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * gets the connection of the db
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @return a valid connection object, that is used to query mysql statements
	 */
	public static Connection getConnection() {
		if (isConnected()) {
			return connection;
		}
		else {
			System.out.println("Lost MySQL Connection. Reconnecting...");
			SQLHandler.connect();
			return connection;
		}
	}
}
