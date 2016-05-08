package com.tpps.technicalServices.network.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import com.tpps.application.game.Player;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
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

					new Thread(() -> {
						System.out.println("observer thread started");

						try {
							Thread.sleep(10000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (this.gameServer.getDisconnectedUser().size() >= 3) {
							GameLog.log(MsgType.MM, "end game because three users are disconnected");
							Client client;
							try {
								client = new Client(new InetSocketAddress(Addresses.getLocalHost(),
										MatchmakingServer.getStandardPort()), new PacketHandler() {

											@Override
											public void handleReceivedPacket(int port, Packet packet) {

											}
										}, false);
								client.sendMessage(
										new PacketGameEnd(this.gameServer.getGameController().getPlayerNames(),
												this.gameServer.getGameController().getWinningPlayer().getPlayerName(),
												gameServer.getPort()));
								this.gameServer.getGameController().endGame();
							} catch (IOException e) {
								e.printStackTrace();
							}
					} else if (this.gameServer.getDisconnectedUser().size() >= 1) {
						try {
							Player player1 = this.gameServer.getGameController().getPlayerByPort(port);
							this.gameServer.getGameController().getPlayers().remove(player1);
							this.gameServer.getDisconnectedUser().remove(player1);
							this.gameServer.getGameController()
									.setActivePlayer(this.gameServer.getGameController().getRandomPlayer());
							try {
								this.gameServer.broadcastMessage(new PacketEnableDisable(
										this.gameServer.getGameController().getActivePlayer().getClientID(),
										this.gameServer.getGameController().getActivePlayerName(), false));
								this.gameServer.broadcastMessage(new PacketBroadcastLog(
										" [INFO] " + this.gameServer.getGameController().getPlayers().size()
												+ " players remaining."));
							} catch (Exception e) {
								e.printStackTrace();
							}
						} catch (IndexOutOfBoundsException e) {
							e.printStackTrace();
						}
					}
				}).start();

				}
			}
		}
	}
