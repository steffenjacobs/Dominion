package com.tpps.application.game.ai.TBR;

import java.io.IOException;
import java.net.SocketAddress;

import com.tpps.technicalServices.network.core.Client;

/**
 * The GameHandler class provides methods for the AI to interact with the actual
 * game such as ending a turn, buying a card or playing all treasure cards from
 * hand
 * 
 * @author Nicolas Wipfler
 */
public class AIClient extends Client {

	private int clientId;
	
	/**
	 * 
	 * @param gameServer
	 *            the GameServer which contains all relevant game information
	 *            for the AI
	 * @throws IOException 
	 */
	public AIClient(SocketAddress _address, AIPacketHandler aiPacketHandler) throws IOException {
		super(_address, aiPacketHandler, false);
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
	public int getClientId() {
		return this.clientId;
	}
}