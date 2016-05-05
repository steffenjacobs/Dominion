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
import com.tpps.technicalServices.network.core.packet.Packet;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** @author ladler - Lukas Adler */
public class GameServer extends Server {

	private static int CLIENT_ID;
	private GameController gameController;
	private static GameServer instance;
	private SessionClient sessionClient;
	private LinkedList<Player> disconnectedUser;
	private final String[] selectedActionCards;

	public GameServer(int port, String[] selectedActionCards) throws IOException {
		// TODO: implement selectedActionCards
		super(new InetSocketAddress("0.0.0.0", port), new ServerGamePacketHandler());
		((ServerGamePacketHandler) super.getHandler()).setServer(this);
		this.sessionClient = new SessionClient(
				new InetSocketAddress(Addresses.getLocalHost(), SessionServer.getStandardPort()));
		this.selectedActionCards = selectedActionCards;
		this.gameController = new GameController(this, this.selectedActionCards);
		instance = this;
		this.getListenerManager().registerListener(new GameServerNetworkListener(this));
		this.disconnectedUser = new LinkedList<Player>();
		setConsoleInput();
	}

	/**
	 * @deprecated it will cause errors in future
	 * @return an instance of the GameServer
	 */
	public static GameServer getInstance() {
		return instance;
	}

	public void newGame() {
		this.disconnectAll();
		this.gameController = new GameController(this, this.selectedActionCards);
		setConsoleInput();
	}

	public static int getCLIENT_ID() {
		return CLIENT_ID++;
	}

	@Deprecated
	public static void main(String[] args) {
		if (args.length != 10)
			throw new NotImplementedException();
		try {
			new GameServer(1340, args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean validSession(String username, UUID sessionID) {
		return this.sessionClient.checkSessionSync(username, sessionID);
	}

	@Override
	public void sendMessage(int port, Packet packet) throws IOException {
		if (super.clients.containsKey(port)) {
			System.out.println("send message.");
			super.sendMessage(port, packet);
		} else {
			System.out.println("send no message");
		}
	}

	/**
	 * 
	 * @return the diesconnectedUsers
	 */
	public LinkedList<Player> getDisconnectedUser() {
		return this.disconnectedUser;
	}

	/**
	 * 
	 * @return the gameController
	 */
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