package com.tpps.technicalServices.network.game;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.tpps.application.game.Player;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.events.NetworkListener;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLog;
import com.tpps.technicalServices.network.gameSession.packets.PacketDisable;

public class GameServerNetworkListener implements NetworkListener {
	private GameServer gameServer;
	private AtomicBoolean packetSend;

	public GameServerNetworkListener(GameServer gameServer) {
		this.gameServer = gameServer;
		this.packetSend.set(false);
	}
	
	public AtomicBoolean getPacketSend() {
		return this.packetSend;
	}


	@Override
	public void onClientConnect(int port) {

	}

	@Override
	public void onClientDisconnect(int port) {
		
		Player player = this.gameServer.getGameController().getPlayerByPort(port);
		if (player == null) {
			return;
		}
 
			if (player.getPort() == port) {
				if (player.getSessionID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
					return;
				}

				if (!this.gameServer.getDisconnectedUser().contains(player)) {
					this.gameServer.getDisconnectedUser().add(player);
					GameLog.log(MsgType.MM, "client disconnected");
					try {
						this.gameServer.broadcastMessage(new PacketBroadcastLog(MsgType.INFO, "player disconnected."));
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					try {
						this.gameServer.broadcastMessage(new PacketDisable("user disconnected"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					new ObserverThread(this.gameServer, port).start();
					
				}
			}
		}


	}
