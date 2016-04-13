package com.tpps.technicalServices.network.matchmaking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.matchmaking.packets.PacketGameEnd;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingAnswer;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingRequest;

public class MatchmakingPacketHandler extends PacketHandler {

	private SessionClient sess;

	private ExecutorService threadPool = Executors.newCachedThreadPool();

	public MatchmakingPacketHandler() throws IOException {
		this.sess = new SessionClient(new InetSocketAddress(Addresses.getRemoteAddress(), 1337));
	}

	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		switch (packet.getType()) {
		case MATCHMAKING_REQUEST:
			handleClientConnect(port, (PacketMatchmakingRequest) packet);
			break;
		case MATCHMAKING_ABORT:
			// TODO: stop running validation-service if necessary
			MatchmakingController.onPlayerDisconnect(port);
			break;
		case GAME_END:
			PacketGameEnd endPacket = (PacketGameEnd) packet;
			MatchmakingController.onGameEnd(endPacket.getWinner(), endPacket.getPlayers());
			//called when the game ends
			break;
		default:
			GameLog.log(MsgType.NETWORK_ERROR, "Bad packet received: " + packet);
		}
	}

	public void handleClientConnect(int port, PacketMatchmakingRequest pmr) {

		threadPool.submit(() -> {
			if (sess.checkSessionSync(pmr.getPlayerName(), pmr.getPlayerID())) {
				MatchmakingController.addPlayer(MPlayer.initialize(pmr, port));
			} else {
				try {
					super.parent.sendMessage(port, new PacketMatchmakingAnswer(pmr, 0));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
