package com.tpps.technicalServices.network.statisticServer.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.login.SQLHandling.SQLHandler;

public class StatisticServer extends Server{
	
	private static int serverPort = 1345;

	public StatisticServer(String host, String port, String username, String password, String database) throws IOException {
		super(new InetSocketAddress(Addresses.getLocalHost(), serverPort), new StatisticServerPacketHandler());
		((StatisticServerPacketHandler)super.getHandler()).setServer(this);
		
		this.initMySQLServer(host, port, username, password, database);
		this.setConsoleOutput();
	}
	
	private void setConsoleOutput(){
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("* * * * * Dominion Statistic Server - Team ++; * * * * *");
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
				if (line.startsWith("help")) {
					System.out.println("-------- Available Commands --------");
					System.out.println("help");
					System.out.println("check sql con");
					System.out.println("------------------------------------");
				}else if(line.startsWith("check sql con")){
					System.out.println(SQLHandler.getConnection());
				}else{
					System.out.println("Bad Command: " + line);
				}
			} catch (Exception e) {				
				System.err.println("Bad syntax.");
			}
		}
//		scanInput.close();
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
		System.out.println(host + " " + port + " " + username + " " + password + " " + database );
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
			new StatisticServer(hostname, port, user, password, database);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
