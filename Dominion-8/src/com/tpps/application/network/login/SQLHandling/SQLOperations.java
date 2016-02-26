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
	
	private SQLHandler sql;
	
	/**
	 * initializes the Object
	 * @param sql SQLHandler which is needed to get basic mysql functionalities like getConnection()
	 */
	public SQLOperations(SQLHandler sql){
		this.sql = sql;
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * @param table String of the named table to ask in database
	 * @return true if the table exists in the database, false else
	 */
	public boolean checkTable(String table){
		try {
			DatabaseMetaData meta = this.sql.getConnection().getMetaData();
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
	public boolean checkDatabase(String database){
		ResultSet resultSet = null;
		try {
			resultSet = sql.getConnection().getMetaData().getCatalogs();
			
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
	public void createAccountdetailsTable(){
		String sqlStatement = "CREATE TABLE accountdetails ("
				+ "nickname VARCHAR(24) PRIMARY KEY NOT NULL,"
				+ "email VARCHAR(256) NOT NULL,"
				+ "salt_hashed_pw VARCHAR(256) NOT NULL,"
				+ "salt VARCHAR(8) NOT NULL);";
		try {
			Statement stmt = this.sql.getConnection().createStatement();
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
	public int createAccount(String nickname, String email, String salt_hashed_pw, String salt){		
		if(this.doesMailExists(email) == 1){
			System.out.println("email already in use");
			return 3;
		}
		
		PreparedStatement stmt;
		try {
			stmt = this.sql.getConnection().prepareStatement("INSERT INTO accountdetails (nickname, email, salt_hashed_pw, salt) VALUES(?,?,?,?)");
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
	public int doesMailExists(String email){
		try {
			PreparedStatement stmt = this.sql.getConnection().prepareStatement("SELECT * FROM accountdetails WHERE email = ?");
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
	public void createDatabase(String database){		
		try {
//			PreparedStatement doesn't work to creating a databse, stackoverflow approved			
//			PreparedStatement stmt = sql.getConnection().prepareStatement("CREATE DATABASE ?;");
//			stmt.setString(1, database);
			
			Statement stmt = this.sql.getConnection().createStatement();		
			stmt.execute("CREATE DATABASE " + database);
			System.out.println("DATABASE " + database + " created successfull");			
		} catch (SQLException e) {
			System.err.println("Error creating a database called: " + database);
			e.printStackTrace();
		}		
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * @param identification is the nickname or the email of the user
	 * @return a String representation of the requested salt, null for failure
	 */
	public String getSaltForLogin(String identification){
		PreparedStatement stmt = null;
		String column = "";
		if(identification.matches("[a-zA-Z0-9]+@[a-zA-Z]+\\.[a-zA-Z]+")){
			column = "email";
		}else{
			column = "nickname";
		}		
		try{
				stmt = this.sql.getConnection().prepareStatement("SELECT salt FROM accountdetails WHERE " + column + " = ?");	
				stmt.setString(1, identification);
				ResultSet rs = stmt.executeQuery();
				rs.next();
			return rs.getString("salt");
		}catch (SQLException e){			
			System.err.println("Error by getting salt for your account verification \n Maybe email or nickname " + identification + " doesn't exist \n" + e.getMessage());
			e.printStackTrace();
		}		
		return null;
	}

	public boolean rightDoubleHashedPassword(String nickname, String doublehashedpw){
		try {
			PreparedStatement stmt = this.sql.getConnection().prepareStatement("SELECT salt_hashed_pw FROM accountdetails WHERE nickname = ?");
			stmt.setString(1, nickname);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			String databasepw = rs.getString("salt_hashed_pw");
			if(databasepw.equals(doublehashedpw)){
				return true;
			}
		} catch (SQLException e) {		
			e.printStackTrace();
		}
		return false;
	}

}
