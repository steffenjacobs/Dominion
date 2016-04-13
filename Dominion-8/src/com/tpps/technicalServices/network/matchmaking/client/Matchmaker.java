package com.tpps.technicalServices.network.matchmaking.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingRequest;
import com.tpps.technicalServices.network.matchmaking.server.MatchmakingServer;

public final class Matchmaker {
	private static Client client;
	private static MatchmakingHandler handler;

	private static void checkAndCreateClient() throws IOException {
		if (client == null || !client.isConnected()) {
			handler = new MatchmakingHandler();
			client = new Client(new InetSocketAddress(Addresses.getRemoteAddress(), MatchmakingServer.PORT_MATCHMAKING),
					handler, false);
		}
	}

	public static void findMatch(String username, UUID uid) throws IOException {
		checkAndCreateClient();
		client.sendMessage(new PacketMatchmakingRequest(username, uid, false));

	}

	public static void abort(String username, UUID uid) throws IOException {
		checkAndCreateClient();
		client.sendMessage(new PacketMatchmakingRequest(username, uid, true));
	}

	private static class MatchmakingHandler extends PacketHandler {

		@Override
		public void handleReceivedPacket(int port, Packet packet) {

			switch (packet.getType()) {
			case MATCHMAKING_ANSWER:
				//is called when the player is put into a matchmaking-lobby
				// TODO: show LobbyScreen
				break;
			case MATCHMAKING_PLAYER_INFO:
				//is called when a player joined or quitted the lobby
				// TODO: add player and remove one instance of "Waiting for
				// player..." @LobbyScreen

				break;
			case MATCHMAKING_SUCCESSFUL:
				//is called, when the lobby is full and the game starts
				// TODO: connect to the gameServer & start the round
				break;
			default:
				GameLog.log(MsgType.NETWORK_ERROR, "Bad packet received: " + packet);
				break;
			}
		}

	}

}
