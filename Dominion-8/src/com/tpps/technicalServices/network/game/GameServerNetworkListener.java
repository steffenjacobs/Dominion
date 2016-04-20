package com.tpps.technicalServices.network.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import com.tpps.application.game.Player;
import com.tpps.technicalServices.network.core.events.NetworkListener;
import com.tpps.technicalServices.network.gameSession.packets.PacketDisable;

public class GameServerNetworkListener implements NetworkListener {
	private GameServer gameServer;

	public GameServerNetworkListener(GameServer gameServer) {
		this.gameServer = gameServer;

	}

	@Override
	public void onClientConnect(int port) {

	}

	@Override
	public void onClientDisconnect(int port) {

		LinkedList<Player> players = this.gameServer.getGameController().getPlayers();
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			
				if (player.getPort() == port) {
					if (player.getSessionID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
						return;
					}
					if (!this.gameServer.getDisconnectedUser().contains(player)) {
						this.gameServer.getDisconnectedUser().add(player);
					}
				}
			}		
		try {
			this.gameServer.broadcastMessage(new PacketDisable());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
