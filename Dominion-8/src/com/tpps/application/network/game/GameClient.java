package com.tpps.application.network.game;

import java.io.IOException;
import java.net.SocketAddress;

import com.tpps.application.game.GameStorageInterface;
import com.tpps.application.network.core.Client;
import com.tpps.application.network.gameSession.packets.PacketRegistratePlayerByServer;
import com.tpps.ui.gameplay.GameWindow;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class GameClient extends Client {

	private int clientId;

	public GameClient(SocketAddress _address, ClientGamePacketHandler _handler) throws IOException {
		super(_address, _handler, false);
		_handler.setGameClient(this);
		this.clientId = -1;
		GameWindow gameWindow = new GameWindow();
		gameWindow.setVisible(false);
		_handler.setGameWindow(gameWindow);
		_handler.setGameStorageInterface(new GameStorageInterface(gameWindow));
		registrateByServer();
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void registrateByServer() throws IOException {
		this.sendMessage(new PacketRegistratePlayerByServer());
	}

	/**
	 * sets the Client id with given clientId
	 * 
	 * @param clientId
	 */
	public void setClientId(int clientId) {
		this.clientId = clientId;
		System.out.println("clientId was set " + clientId);
	}

	/**
	 * 
	 * @return the clientId
	 */
	public int getClientId() {
		return clientId;
	}
}
