package com.tpps.technicalServices.network.game;

import java.io.IOException;
import java.net.SocketAddress;

import com.tpps.application.game.DominionController;
import com.tpps.application.game.GameStorageInterface;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.gameSession.packets.PacketRegistratePlayerByServer;
import com.tpps.ui.gameplay.GameWindow;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class GameClient extends Client {

	private int clientId;
	private GameWindow gameWindow;
	
	public GameClient(SocketAddress _address, ClientGamePacketHandler _handler) throws IOException {
		super(_address, _handler, false);
		_handler.setGameClient(this);
		this.clientId = -1;
		GameLog.log(MsgType.INIT, "GameClient");
		this.gameWindow = new GameWindow();
		_handler.setGameWindow(gameWindow);
		_handler.setGameStorageInterface(new GameStorageInterface(gameWindow));
		super.getListenerManager().registerListener(new GameClientNetworkListener(this));
		registrateByServer();
	}
	
	public GameWindow getGameWindow() {
		return gameWindow;
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void registrateByServer() throws IOException {
		this.sendMessage(new PacketRegistratePlayerByServer(DominionController.getInstance().getUsername(),
				DominionController.getInstance().getSessionID()));
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
