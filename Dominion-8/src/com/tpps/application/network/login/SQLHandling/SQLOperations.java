package com.tpps.application.network.login.SQLHandling;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author jhuhn - Johannes Huhn
 * This class delivers functionalities that is used to handle databases
 * The main focus of this class is handling the table of the account details
 */
public class SQLOperations {
	

	/**
	 * @author jhuhn - Johannes Huhn
	 * @param table String of the named table to ask in database
	 * @return true if the table exists in the database, false else
	 */
	public static boolean checkTable(String table){
		try {
			DatabaseMetaData meta = SQLHandler.getConnection().getMetaData();
			ResultSet rs = meta.getTables(null, null, "", new String[] { "TABLE" });
			while (rs.next()) {
				if (rs.getString("TABLE_NAME").equals(table)) {
					return true;
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * @param table String of the named database
	 * @return true if the table exists in the database, false else
	 */
	public static boolean checkDatabase(String database){
		ResultSet resultSet = null;
		try {
			resultSet = SQLHandler.getConnection().getMetaData().getCatalogs();
			
			while (resultSet.next()) {
				  String databaseName = resultSet.getString(1);
				    if(databaseName.equals(database)){
				        return true;
				    }
				} 
				resultSet.close();
		} catch (SQLException e) {
			System.err.println("database's name could not be checked.");
			e.printStackTrace();
		}
		return false;	
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * This method creates a table 'accountdetails' in the database
	 * Columns are: nickname, email, salt_hashed_pw, salt
	 */
	public static void createAccountdetailsTable(){
		String sqlStatement = "CREATE TABLE accountdetails ("
				+ "nickname NVARCHAR(24) PRIMARY KEY NOT NULL,"
				+ "email NVARCHAR(256) NOT NULL,"
				+ "salt_hashed_pw NVARCHAR(256) NOT NULL,"
				+ "salt NVARCHAR(8) NOT NULL);";
		try {
			Statement stmt = SQLHandler.getConnection().createStatement();
			stmt.executeUpdate(sqlStatement);
			System.out.println("Table accountdetails added successful");
		} catch (SQLException e) {
			System.err.println("ERROR creating Table accountdetails");
			e.printStackTrace();
		}
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 *  @param nickname plaintext representation of the claimed username,
	 *  @param	email plaintext representation of the claimed email
	 *  @return 1 if account created successfully,
	 *  		2 if nickname already in use,
	 *  		3 if email already in use 
	 *  */
	public static int createAccount(String nickname, String email, String salt_hashed_pw, String salt){		
		if(doesMailExists(email) == 1){
			System.out.println("email already in use");
			return 3;
		}
		
		PreparedStatement stmt;
		try {
			stmt = SQLHandler.getConnection().prepareStatement("INSERT INTO accountdetails (nickname, email, salt_hashed_pw, salt) VALUES(?,?,?,?)");
			stmt.setString(1, nickname);
			stmt.setString(2, email);
			stmt.setString(3, salt_hashed_pw);
			stmt.setString(4, salt);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Nickname already in use, Primary Key vialotion");
			return 2;			
		}
		System.out.println("Account " + nickname + " created successful");		
		return 1;
	}
	
	/** 
	 * @author jhuhn - Johannes Huhn
	 * @param email String representation of the plaintext of requested email
	 * @return 1 if email already exists in database, 0 if not.
	 */
	public static int doesMailExists(String email){
		try {
			PreparedStatement stmt = SQLHandler.getConnection().prepareStatement("SELECT * FROM accountdetails WHERE email = ?");
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			//if the ResultSet is empty, the email adress doesn't exist in the database
			//if the ResultSet isn't empty, the email adress already exists in the database
			if(!rs.next()){
				return 0;
			}
		} catch (SQLException e) {		
			e.printStackTrace();
		}
		return 1;
	}
	/**
	 * @author jhuhn - Johannes Huhn
	 * @param database String representation of the database to be created
	 * This method creates a database 
	 */
	public static void createDatabase(String database){		
		try {
//			PreparedStatement doesn't work to creating a databse, stackoverflow approved			
//			PreparedStatement stmt = sql.getConnection().prepareStatement("CREATE DATABASE ?;");
//			stmt.setString(1, database);
			
			Statement stmt = SQLHandler.getConnection().createStatement();		
			stmt.execute("CREATE DATABASE " + database);
			System.out.println("DATABASE " + database + " created successfull");			
		} catch (SQLException e) {
			System.err.println("Error creating a database called: " + database);
			e.printStackTrace();
		}		
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * @param nickname is the nickname or the email of the user
	 * @return a String representation of the requested salt, null for failure
	 */
	public static String getSaltForLogin(String nickname){
		PreparedStatement stmt = null;
		String column = "";
		if(nickname.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
			column = "email";
		}else{
			column = "nickname";
		}		
		try{
				stmt = SQLHandler.getConnection().prepareStatement("SELECT salt FROM accountdetails WHERE " + column + " = ?");	
				stmt.setString(1, nickname);
				ResultSet rs = stmt.executeQuery();
				rs.next();
			return rs.getString("salt");
		}catch (SQLException e){			
			System.err.println("Error by getting salt for your account verification \n Maybe nickname " + nickname + " doesn't exist \n");
		//	e.printStackTrace();
			return null;
		}		
	//	return null;
	}

	/**
	 * This method checks if the calculated hash matches with the hash out ouf the database
	 * @author jhuhn - Johannes Huhn
	 * @param nickname a String representation of the username, needed to get the salt hashed password
	 * @param doublehashedpw a String representation of the calculated hash
	 * @return true, if the calculated hash(deliver) matches with the hash out of the database 
	 */
	public static boolean rightDoubleHashedPassword(String nickname, String doublehashedpw){
		try {
			PreparedStatement stmt = SQLHandler.getConnection().prepareStatement("SELECT salt_hashed_pw FROM accountdetails WHERE nickname = ?");
			stmt.setString(1, nickname);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			String databasepw = rs.getString("salt_hashed_pw");
			System.out.println("hash aus db: " + databasepw);
			System.out.println("calculated hash: " + doublehashedpw);
			if(databasepw.equals(doublehashedpw)){
				return true;
			}
		} catch (SQLException e) {
			return false;
			//e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * This method gets all nicknames(PRIMARY KEY) in the table accountdetails
	 * @author jhuhn - Johannes Huhn
	 * @return a String with all nicknames in the table accountdetails
	 */
	public static String showAllNicknames(){
		StringBuffer buf = new StringBuffer("Nicknames: " + "\n");
		try {
			Statement stmt = SQLHandler.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT nickname FROM accountdetails");
			while(rs.next()){
				buf.append(rs.getString("nickname") + "\n");
			}
			return buf.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return buf.toString();
		}
	}
	
	/**
	 * This methods deletetes a table
	 * @author jhuhn - Johannes Huhn
	 * @param tablename a String representation of the table to delete
	 * @return true if the table deletes successfully, false else
	 */
	public static boolean deleteTable(String tablename){
		try {
			Statement stmt = SQLHandler.getConnection().createStatement();
			stmt.executeUpdate("DROP TABLE " + tablename);
			System.out.println("Table " + tablename + " deleted");
			return true;
		} catch (SQLException e) {		
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method gets all tables (names) that are in the database
	 * @author jhuhn - Johannes Huhn
	 * @return a String with all tables
	 */
	public static String showTables(){
		StringBuffer buf = new StringBuffer("All tables: \n");
		try {
			Statement stmt = SQLHandler.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("show tables");
			while(rs.next()){
				buf.append(rs.getString(1) + "\n");
			}
			return buf.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return "-- no tables-- or no connection";
		}
	}

}
