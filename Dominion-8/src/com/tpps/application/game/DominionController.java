package com.tpps.application.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import com.mysql.fabric.Server;
import com.tpps.application.storage.CardStorageController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.card.CardClient;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.game.ClientGamePacketHandler;
import com.tpps.technicalServices.network.game.GameClient;

/**
 * main controller class containing main entry point for client-application
 * 
 * @author Steffen Jacobs
 */
public final class DominionController {

	private static DominionController instance;

	private String username, email;
	private UUID sessionID;
	private SessionClient sessionClient;
	private GameClient gameClient;
	private CardClient cardClient;
	private CardStorageController storageController;

	/** main entry point for client application */
	public static void main(String[] stuff) {
		instance = new DominionController();
	}

	/* constructor */
	public DominionController() {
		storageController = new CardStorageController();
		// new LoginGUIController();
		try {
			gameClient = new GameClient(new InetSocketAddress("localhost", 1339), new ClientGamePacketHandler());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	public CardStorageController getStorageController() {
		return storageController;
	}

	/**
	 * 
	 * @return
	 */
	public CardStorageController getCardRegistry() {
		return storageController;
	}

	/**
	 * 
	 * @param test
	 */
	public DominionController(boolean test) {
		storageController = new CardStorageController();
		// do nothing else, just init object
	}

	/**
	 * sets the session-client instance and starts keep-alive 
	 */
	public void setSessionClient(SessionClient sc) {
		if (this.sessionClient != null) {
			this.sessionClient.keepAlive(username, false);
			this.sessionClient.disconnect();
		}
		this.sessionClient = sc;
		this.sessionClient.keepAlive(username, true);
	}

	/* getters and setters */
	/**
	 * @param sessionID new session-id
	 */
	public void setSessionID(UUID sessionID) {
		this.sessionID = sessionID;
	}

	/** 
	 * @return the users username 
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the GameClientObject
	 */
	public GameClient getGameClient() {
		return gameClient;
	}

	/**
	 * @return the users email-address 
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the current Session-ID
	 */
	public UUID getSessionID() {
		return sessionID;
	}

	/**
	 * @return the current instance of the game 
	 */
	public static DominionController getInstance() {
		return instance;
	}

	/**
	 * @param username new Username
	 * @param mailAddress new Email-Address
	 */
	public void setCredentials(String username, String mailAddress) {
		this.username = username;
		this.email = mailAddress;
	}
}