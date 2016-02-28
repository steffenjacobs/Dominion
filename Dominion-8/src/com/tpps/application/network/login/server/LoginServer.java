package com.tpps.application.network.login.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.tpps.application.network.core.Server;
import com.tpps.application.network.login.SQLHandling.SQLHandler;
import com.tpps.application.network.login.SQLHandling.SQLOperations;

public class LoginServer extends Server{
	
	private SQLOperations sql;
	
	public LoginServer(SQLOperations sql) throws IOException {
		super(new InetSocketAddress("127.0.0.1", 1338), new LoginPacketHandler(sql));
		((LoginPacketHandler)super.getHandler()).setServer(this);
	}
	
	public static void main(String[] args) {
		try {
			String hostname = "localhost";
			String port = "3306";
			String database = "accountmanager";
			String user = "jojo";
			String password = "password";
			SQLHandler sql = new SQLHandler(hostname, port, user, password, database);
			SQLOperations op = new SQLOperations(sql);
			new LoginServer(op);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
}
