package com.tpps.application.network.game;

import java.io.IOException;
import java.net.SocketAddress;

import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.gameSession.packets.PacketRegistratePlayerByServer;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class GameClient extends Client{
	
	private int clientId;

	public GameClient(SocketAddress _address, PacketHandler _handler) throws IOException {
		super(_address, _handler, false);	
		((ClientGamePacketHandler)super.getHandler()).setGameClient(this);
		registrateByServer();
	}	

	private void registrateByServer() throws IOException {		
		this.sendMessage(new PacketRegistratePlayerByServer());
	}
	
	/**
	 * sets the Client id with given clientId
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
