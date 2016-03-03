package com.tpps.application.network.login.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import com.tpps.application.network.core.Server;
import com.tpps.application.network.login.SQLHandling.SQLHandler;
import com.tpps.application.network.login.SQLHandling.SQLOperations;
import com.tpps.application.network.login.SQLHandling.SQLStatisticsHandler;
import com.tpps.application.network.login.SQLHandling.SQLType;
import com.tpps.application.network.login.SQLHandling.Statistic;
/**
 * This class sets the LoginServer up.
 * @author jhuhn - Johannes Huhn
 */
public class LoginServer extends Server{
	
	private static String domain = "127.0.0.1";
	private static int port = 1338;
	
	/**
	 * This method initializes the LoginServer object and the MySQL server
	 * @author jhuhn - Johannes Huhn
	 * @param host a String representation of the hostname
	 * @param port a String representation of the used MySQL server port
	 * @param username a String representation of the used username of the MySQL database
	 * @param password a String representation in plaintext of the MySQL server root password 
	 * @param database a String representation of the database(MySQL) to use
	 * @throws IOException
	 */
	public LoginServer(String sqlhost, String sqlport, String sqlusername, String sqlpassword, String sqldatabase) throws IOException {
		super(new InetSocketAddress(domain, port), new LoginPacketHandler());
		((LoginPacketHandler)super.getHandler()).setServer(this);
		
		this.initMySQLServer(sqlhost, sqlport, sqlusername, sqlpassword, sqldatabase);
		this.checkExistingDatabase();
		this.setConsoleOutput();
	}
	
	/**
	 * This methods is called when the server is finished with initializing
	 * This method outputs a 'Dominion Login Server' banner
	 * @author jhuhn - Johannes Huhn
	 */
	private void setConsoleOutput(){
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("* * * * * Dominion Login Server - Team ++; * * * * *");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println();
	//	System.out.println("Enter 'help' to see all available commands.");
	//	System.out.println();
	}
	
	/**
	 * This method initializes the SQLHandler with host, port etc. and connects to the MySQL databse
	 * @author jhuhn - Johannes Huhn
	 * @param host a String representation of the hostname
	 * @param port a String representation of the used MySQL server port
	 * @param username a String representation of the used username of the MySQL database
	 * @param password a String representation in plaintext of the MySQL server root password 
	 * @param database a String representation of the database(MySQL) to use
	 */
	private void initMySQLServer(String host, String port, String username, String password, String database){
		SQLHandler.init(host, port, username, password, database);
		SQLHandler.connect();
	}
	
	public static void main(String[] args) {
		try {
			String hostname = "localhost";
			String port = "3306";
			String database = "accountmanager";
			String user = "jojo";
			String password = "password";
			SQLHandler.init(hostname, port, user, password, database);
			SQLHandler.connect();
			new LoginServer(hostname, port, user, password, database);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is important to setup the mysql database
	 * This method creates MySQL tables and/or databases, if they aren't created
	 * @author jhuhn - Johannes Huhn
	 */
	private void checkExistingDatabase() {
		if (!SQLOperations.checkDatabase("accountmanager")) {
			SQLOperations.createDatabase("accountmanager");
		}
		if (!SQLOperations.checkTable("accountdetails")) {
			SQLOperations.createAccountdetailsTable();
		}
		if(!SQLOperations.checkTable("statistics")){
			Statistic one = new Statistic(SQLType.VARCHAR, "40", "description");
			Statistic two = new Statistic(SQLType.INT, "wins");
			Statistic tree = new Statistic(SQLType.INT, "losses");
			Statistic four = new Statistic(SQLType.FLOAT, "4,2", "win_loss");
			ArrayList<Statistic> statistics = new ArrayList<Statistic>();
			statistics.add(one);
			statistics.add(two);
			statistics.add(tree);
			statistics.add(four);
			SQLStatisticsHandler.createStatisticsTable(statistics);
		}
	}
}
