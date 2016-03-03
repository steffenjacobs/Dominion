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

	public GameClient(SocketAddress _address, PacketHandler _handler) throws IOException {
		super(_address, _handler, false);	
//		this.sendMessage(new PacketRegistratePlayerByServer(playerId, playerName));
	}


}
