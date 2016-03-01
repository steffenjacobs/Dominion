package com.tpps.application.network.login.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.tpps.application.network.core.Server;
import com.tpps.application.network.login.SQLHandling.SQLHandler;
import com.tpps.application.network.login.SQLHandling.SQLOperations;

public class LoginServer extends Server{
	
	public LoginServer() throws IOException {
		super(new InetSocketAddress("0.0.0.0", 1338), new LoginPacketHandler());
		((LoginPacketHandler)super.getHandler()).setServer(this);
		checkExistingDatabase();
	}
	
	public static void main(String[] args) {
		try {
			String hostname = "localhost";
			String port = "3306";
			String database = "accountmanager";
			String user = "root";
			String password = "root";
			SQLHandler.init(hostname, port, user, password, database);
			new LoginServer();
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	private void checkExistingDatabase(){
		  if(!SQLOperations.checkDatabase("accountmanager")){
			  SQLOperations.createDatabase("accountmanager");
		  }
		  if(!SQLOperations.checkTable("accountdetails")){
			  SQLOperations.createAccountdetailsTable();
		  }
		 }
}
