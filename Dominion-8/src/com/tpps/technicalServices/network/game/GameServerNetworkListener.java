package com.tpps.technicalServices.network.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.LinkedList;

import com.tpps.application.game.Player;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.events.NetworkListener;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLog;
import com.tpps.technicalServices.network.gameSession.packets.PacketDisable;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnableDisable;
import com.tpps.technicalServices.network.matchmaking.packets.PacketGameEnd;
import com.tpps.technicalServices.network.matchmaking.server.MatchmakingServer;

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
		System.out.println("client disconnected");
		try {
			this.gameServer.broadcastMessage(new PacketBroadcastLog("INFO: player disconnected."));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		LinkedList<Player> players = this.gameServer.getGameController().getPlayers();
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			
				if (player.getPort() == port) {
//					if (player.getSessionID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
//						return;
//					}
					if (!this.gameServer.getDisconnectedUser().contains(player)) {
						this.gameServer.getDisconnectedUser().add(player);
					}
				}
			}		
		try {
			this.gameServer.broadcastMessage(new PacketDisable("user disconnected"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		new Thread(() -> {
			System.out.println("observer thread started");		
			
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (this.gameServer.getDisconnectedUser().size() >= 3) {
				System.out.println("end game because three users are disconnected");
				Client client;
				try {
					client = new Client(
							new InetSocketAddress(Addresses.getLocalHost(), MatchmakingServer.getStandardPort()),
							new PacketHandler() {

								@Override
								public void handleReceivedPacket(int port, Packet packet) {

								}
							}, false);
					client.sendMessage(new PacketGameEnd(this.gameServer.getGameController().getPlayerNames(),
							this.gameServer.getGameController().getWinningPlayer().getPlayerName(), gameServer.getPort()));
					this.gameServer.getGameController().endGame();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else if (this.gameServer.getDisconnectedUser().size() >= 1) {
				Player player = this.gameServer.getGameController().getPlayerPlayerByPort(port);
				this.gameServer.getGameController().getPlayers().remove(player);
				this.gameServer.getDisconnectedUser().remove(player);
				this.gameServer.getGameController().setActivePlayer(this.gameServer.getGameController().getRandomPlayer());
				try {
					this.gameServer.broadcastMessage(new PacketEnableDisable(this.gameServer.getGameController().getActivePlayer().getClientID(),
							this.gameServer.getGameController().getActivePlayerName()));
					this.gameServer.broadcastMessage(new PacketBroadcastLog(this.gameServer.getGameController().getPlayers().size() + " players left."));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
