package com.tpps.technicalServices.network.login.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.login.SQLHandling.Password;
import com.tpps.technicalServices.network.login.SQLHandling.SQLHandler;
import com.tpps.technicalServices.network.login.SQLHandling.SQLOperations;
import com.tpps.technicalServices.network.login.SQLHandling.SQLStatisticsHandler;
import com.tpps.technicalServices.network.login.SQLHandling.Utilties;

/**
 * This class sets the LoginServer up.
 * 
 * @author jhuhn - Johannes Huhn
 */
public class LoginServer extends Server {

	private static int server_port = 1338;

	/**
	 * This method initializes the LoginServer object and the MySQL server
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param host
	 *            a String representation of the hostname
	 * @param port
	 *            a String representation of the used MySQL server port
	 * @param username
	 *            a String representation of the used username of the MySQL
	 *            database
	 * @param password
	 *            a String representation in plaintext of the MySQL server root
	 *            password
	 * @param database
	 *            a String representation of the database(MySQL) to use
	 * @throws IOException
	 */
	public LoginServer(String host, String port, String username, String password, String database) throws IOException {
		super(new InetSocketAddress(Addresses.getAllInterfaces(), server_port), new LoginPacketHandler());
		((LoginPacketHandler) super.getHandler()).setServer(this);
		SQLHandler.init();
		SQLHandler.connect();
		this.initMySQLServer(host, port, username, password, database);
		this.checkExistingDatabase();
		this.setConsoleOutput();
	}

	/** offline-mode constructor 
	 * @throws IOException */
	public LoginServer() throws IOException {
		super(new InetSocketAddress(Addresses.getAllInterfaces(), server_port), new LoginPacketHandler());
		((LoginPacketHandler) super.getHandler()).setServer(this);
		this.setConsoleOutput();
	}

	/**
	 * This methods is called when the server is finished with initializing This
	 * method outputs a 'Dominion Login Server' banner and delivers specific
	 * server commands like help, reconnect or some basic sql statements
	 * 
	 * @author jhuhn - Johannes Huhn
	 */
	private void setConsoleOutput() {
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("* * * * * Dominion Login Server - Team ++; * * * * *");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println();
		System.out.println("Enter 'help' to see all available commands.");
		System.out.println();

		String line = null;
		Scanner scanInput = new Scanner(System.in);
		while (true) {
			line = scanInput.nextLine();
			try {
				if (line.startsWith("exit") || line.startsWith("close connection")) {
					SQLHandler.closeConnection();
					System.exit(0);
					break;
				} else if (line.startsWith("create account")) {
					String[] words = line.split("\\s+");
					Password temp1 = new Password(words[3], new String("defsalt"));
					String firsthash = temp1.getHashedPassword();

					Password pw2 = new Password(firsthash);
					String doublehashed = pw2.getHashedPassword();

					System.out.println(SQLOperations.createAccount(words[2], "", doublehashed, pw2.getSalt()));
					SQLStatisticsHandler.insertRowForFirstLogin(words[2]);
				} else if (line.startsWith("show nicknames")) {
					System.out.println(SQLOperations.showAllNicknames());
				} else if (line.startsWith("reconnect")) {
					SQLHandler.closeConnection();
					SQLHandler.connect();
				} else if (line.startsWith("DROP TABLE")) {
					String[] words = line.split("\\s+");
					if (SQLOperations.checkTable(words[2])) {
						SQLOperations.deleteTable(words[2]);
					} else {
						System.out.println("Table: " + words[2] + " doesn't exist");
					}
				} else if (line.startsWith("show tables")) {
					System.out.println(SQLOperations.showTables());
				} else if (line.startsWith("CREATE TABLE accountdetails")) {
					if (!SQLOperations.checkTable("accountdetails")) {
						SQLOperations.createAccountdetailsTable();
					} else {
						System.out.println("Table accountdetails already exists");
					}
				} else if (line.trim().startsWith("CREATE TABLE statistics")) {
					if (!SQLOperations.checkTable("statistics")) {
						SQLStatisticsHandler.createStatisticsTable(Utilties.createStatisticsList());
					} else {
						System.out.println("TABLE statistics already exists");
					}
				} else if (line.startsWith("help")) {
					System.out.println("-------- Available Commands --------");
					System.out.println("DROP TABLE <tablename>");
					System.out.println("CREATE TABLE accountdetails");
					System.out.println("CREATE TABLE statistics");
					System.out.println("show tables");
					System.out.println("create account <username> <password>");
					System.out.println("show nicknames");
					System.out.println("exit" + " or " + "close connection");
					System.out.println("reconnect");
					System.out.println("help");
					System.out.println("------------------------------------");
				} else {
					System.out.println("Bad command: " + line + " Type in 'help' to see all avaible commands");
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Bad syntax.");
			}
		}
		scanInput.close();
	}

	/**
	 * This method initializes the SQLHandler with host, port etc. and connects
	 * to the MySQL databse
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param host
	 *            a String representation of the hostname
	 * @param port
	 *            a String representation of the used MySQL server port
	 * @param username
	 *            a String representation of the used username of the MySQL
	 *            database
	 * @param password
	 *            a String representation in plaintext of the MySQL server root
	 *            password
	 * @param database
	 *            a String representation of the database(MySQL) to use
	 */
	private void initMySQLServer(String host, String port, String username, String password, String database) {
		SQLHandler.init();
		SQLHandler.connect();
	}

	/**
	 * main entrypoint for the loginserver
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		try {
			String hostname = "localhost";
			String port = "3306";
			String database = "accountmanager";
			String user = "root";
			String password = "root";
			new LoginServer(hostname, port, user, password, database);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is important to setup the mysql database This method creates
	 * MySQL tables and/or databases, if they aren't created
	 * 
	 * @author jhuhn - Johannes Huhn
	 */
	private void checkExistingDatabase() {
		if (!SQLOperations.checkDatabase("accountmanager")) {
			SQLOperations.createDatabase("accountmanager");
		}
		if (!SQLOperations.checkTable("accountdetails")) {
			SQLOperations.createAccountdetailsTable();
		}
		if (!SQLOperations.checkTable("statistics")) {
			SQLStatisticsHandler.createStatisticsTable(Utilties.createStatisticsList());
		}
	}
}
