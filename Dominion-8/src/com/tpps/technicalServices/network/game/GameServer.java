package com.tpps.technicalServices.network.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.UUID;

import com.tpps.application.game.GameController;
import com.tpps.application.game.Player;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
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
	private GameServerNetworkListener gameServerNetworkListener;

	public GameServer(int port, String[] selectedActionCards) throws IOException {
		super(new InetSocketAddress("0.0.0.0", port), new ServerGamePacketHandler());
		((ServerGamePacketHandler) super.getHandler()).setServer(this);
		this.sessionClient = new SessionClient(
				new InetSocketAddress(Addresses.getLocalHost(), SessionServer.getStandardPort()));
		this.selectedActionCards = selectedActionCards;
		this.gameController = new GameController(this, this.selectedActionCards);
		instance = this;
		this.gameServerNetworkListener = new GameServerNetworkListener(this);
		this.getListenerManager().registerListener(this.gameServerNetworkListener);
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

	/**
	 * sends a message to human players not to ais
	 */
	@Override
	public void sendMessage(int port, Packet packet) throws IOException {
		if (super.clients.containsKey(port)) {
//			GameLog.log(MsgType.INFO, "send message.");
			super.sendMessage(port, packet);
		} else {
//			GameLog.log(MsgType.INFO, "send no message");
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
	 * 
	 * @return the gameServerNetworkListener
	 */
	public GameServerNetworkListener getGameServerNetworkListener() {
		return this.gameServerNetworkListener;
	}

	/**
	 * sets up the console-input
	 * 
	 * @author Steffen Jacobs
	 */
	private void setConsoleInput() {
		GameLog.log(MsgType.INFO ,"            * * * * * * * * * * * * * *      ");
		GameLog.log(MsgType.INFO ,"      * * * * * * * * * * * * * * * * * * * *");
		GameLog.log(MsgType.INFO ,"* * * * * Dominion Game Server - Team ++; * * * * *");
		GameLog.log(MsgType.INFO ,"      * * * * * * * * * * * * * * * * * * * *");
		GameLog.log(MsgType.INFO ,"            * * * * * * * * * * * * * *      ");
		GameLog.log(MsgType.INFO ,"");
		GameLog.log(MsgType.INFO ,"Enter 'help' to see all available commands.");
		GameLog.log(MsgType.INFO ,"");
	}

}