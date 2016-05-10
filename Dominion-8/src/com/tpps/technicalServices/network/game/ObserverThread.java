package com.tpps.technicalServices.network.game;

import java.util.Iterator;
import java.util.UUID;

import com.tpps.application.game.Player;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLog;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnableDisable;

public class ObserverThread extends Thread {
	private GameServer gameServer;
	private int port;

	public ObserverThread(GameServer gameServer, int port) {
		this.gameServer = gameServer;
		this.port = port;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		System.out.println("observer thread started");

		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (this.gameServer.getDisconnectedUser().size() >= 3 || this.gameServer.getGameController().getPlayers().size() <= 1) {
			GameLog.log(MsgType.MM, "end game because three users are disconnected");
//			Client client;
//			try {
//				client = new Client(
//						new InetSocketAddress(Addresses.getLocalHost(), MatchmakingServer.getStandardPort()),
//						new PacketHandler() {
//
//							@Override
//							public void handleReceivedPacket(int port, Packet packet) {
//
//							}
//						}, false);
//				client.sendMessage(new PacketGameEnd(this.gameServer.getGameController().getPlayerNames(),
//						this.gameServer.getGameController().getWinningPlayer().getPlayerName(), gameServer.getPort()));
			if (!this.gameServer.getGameServerNetworkListener().getPacketSend().get()){
				this.gameServer.getGameController().endGame();
				this.gameServer.getGameServerNetworkListener().getPacketSend().set(true);
			}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		} else if (this.gameServer.getDisconnectedUser().size() >= 1 && this.gameServer.getDisconnectedUser().contains(this.gameServer.getGameController().getPlayerByPort(port))) {
			try {
				Player player1 = this.gameServer.getGameController().getPlayerByPort(port);
				this.gameServer.getGameController().getPlayers().remove(player1);
				this.gameServer.getDisconnectedUser().remove(player1);
				
				boolean kiFlag = true;
				for (Iterator<Player> iterator = this.gameServer.getGameController().getPlayers().iterator(); iterator.hasNext();) {
					Player player = (Player) iterator.next();
					if (!player.getSessionID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))){
						kiFlag = false;
					}
				}
				
				if (kiFlag || this.gameServer.getGameController().getPlayers().size() <= 1) {
					
						GameLog.log(MsgType.MM, "send message to matchmakingserver");
						if (!this.gameServer.getGameServerNetworkListener().getPacketSend().get()){
							this.gameServer.getGameController().endGame();
							this.gameServer.getGameServerNetworkListener().getPacketSend().set(true);
						}
					
					return;
				}
				
				
				this.gameServer.getGameController().resetSpyList();
				this.gameServer.getGameController().resetThiefList();
				for (Iterator<Player> iterator = this.gameServer.getGameController().getPlayers().iterator(); iterator
						.hasNext();) {
					Player setPlayer = (Player) iterator.next();
					setPlayer.setAllModesFalse();

				}
				this.gameServer.getGameController()
						.setActivePlayer(this.gameServer.getGameController().getRandomPlayer());
				try {
					this.gameServer.broadcastMessage(
							new PacketEnableDisable(this.gameServer.getGameController().getActivePlayer().getClientID(),
									this.gameServer.getGameController().getActivePlayerName(), false));
					this.gameServer.broadcastMessage(new PacketBroadcastLog(" [INFO] "
							+ this.gameServer.getGameController().getPlayers().size() + " players remaining."));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IndexOutOfBoundsException | NullPointerException e) {
				e.printStackTrace();
			}
		}

	}
}
