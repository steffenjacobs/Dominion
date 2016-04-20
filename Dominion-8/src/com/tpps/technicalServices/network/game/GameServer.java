package com.tpps.technicalServices.network.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.UUID;

import com.tpps.application.game.GameController;
import com.tpps.application.game.Player;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.core.Server;

/** @author ladler - Lukas Adler */
public class GameServer extends Server{
	
	private static int CLIENT_ID;
	private GameController gameController;
	private static GameServer instance;
	private SessionClient sessionClient;
	private LinkedList<Player> disconnectedUser;
	
	public GameServer(int port) throws IOException{
		super(new InetSocketAddress("0.0.0.0", port), new ServerGamePacketHandler());
		((ServerGamePacketHandler)super.getHandler()).setServer(this);
		this.sessionClient = new SessionClient(new InetSocketAddress(Addresses.getLocalHost(), 
				SessionServer.getStandardPort()));
		this.gameController = new GameController(this);
		instance = this;
		super.getListenerManager().registerListener(new GameServerNetworkListener(this));
		this.disconnectedUser = new LinkedList<Player>();
		setConsoleInput();		
	}
	
	
	/**
	 * @deprecated
	 * it will cause errors in future
	 * @return an instance of the GameServer
	 */
	public static GameServer getInstance() {
		return instance;
	}
	
	public void newGame() {
		this.disconnectAll();
		this.gameController = new GameController(this);
		setConsoleInput();
	}



	public static int getCLIENT_ID() {
		return CLIENT_ID++;
	}
	
	public static void main(String[] args) {
		try {
			new GameServer(1339);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean validSession(String username, UUID sessionID) {
		return this.sessionClient.checkSessionSync(username, sessionID);
	}
	
	/**
	 * 
	 * @return the diesconnectedUsers
	 */
	public LinkedList<Player> getDisconnectedUser() {
		return this.disconnectedUser;
	}

	public synchronized GameController getGameController() {
		return this.gameController;
	}

	/**
	 * sets up the console-input
	 * 
	 * @author Steffen Jacobs
	 */
	private void setConsoleInput() {
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("* * * * * Dominion Game Server - Team ++; * * * * *");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println();
		System.out.println("Enter 'help' to see all available commands.");
		System.out.println();
		
	}
	
}