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
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingAnswer;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingPlayerInfo;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingRequest;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingSuccessful;
import com.tpps.technicalServices.network.matchmaking.server.MatchmakingServer;

/**
 * main matchmaking system for client
 * 
 * @author Steffen Jacobs
 */
public final class Matchmaker {
	private Client client;
	private PacketHandler handler;
	
	/**
	 * creates & opens a new connection to the matchmaking-server if necessary
	 */
	private void checkAndCreateClient() throws IOException {
		if (client == null || !client.isConnected()) {
			handler = new MatchmakingHandler();
			client = new Client(new InetSocketAddress(Addresses.getRemoteAddress(), MatchmakingServer.PORT_MATCHMAKING),
					handler, false);
		}
	}

	/**
	 * finds a match for the player
	 * 
	 * @param username
	 *            name of the player searching for a match
	 * @param uid
	 *            uuid of the player searching for a match
	 * @throws IOException
	 *             if there is no network connection available or the server is
	 *             unreachable
	 */
	public void findMatch(String username, UUID uid) throws IOException {
		checkAndCreateClient();
		client.sendMessage(new PacketMatchmakingRequest(username, uid, false));
	}

	/**
	 * aborts the search for a match for a player
	 * 
	 * @param username
	 *            name of the player aborting the search
	 * @param uid
	 *            uuid of the player aborting the search
	 */
	public void abort(String username, UUID uid) throws IOException {
		checkAndCreateClient();
		client.sendMessage(new PacketMatchmakingRequest(username, uid, true));
	}

	/** @return the actual network-client connected to the matchmaking-system */
	public Client getNetworkClient() {
		return client;
	}

	/**
	 * client packet-handler for the matchmaking
	 * 
	 * @author Steffen Jacobs
	 */
	private static class MatchmakingHandler extends PacketHandler {

		/**
		 * is called when a packet is received
		 * 
		 * @param port
		 *            the port the packet was received on (unnecessary here,
		 *            because all received packets are from the same server at
		 *            the same port
		 * @param packet
		 *            the received packet
		 */
		@Override
		public void handleReceivedPacket(int port, Packet packet) {

			switch (packet.getType()) {
			case MATCHMAKING_ANSWER:
				PacketMatchmakingAnswer pma = (PacketMatchmakingAnswer) packet;
				// is called when the player is put into a matchmaking-lobby
				// TODO: show LobbyScreen
				break;
			case MATCHMAKING_PLAYER_INFO:
				PacketMatchmakingPlayerInfo pmpi = (PacketMatchmakingPlayerInfo) packet;
				// is called when a player joined or quitted the lobby
				// TODO: add player and remove one instance of "Waiting for
				// player..." @LobbyScreen

				break;
			case MATCHMAKING_SUCCESSFUL:
				PacketMatchmakingSuccessful pms = (PacketMatchmakingSuccessful) packet;
				// is called, when the lobby is full and the game starts
				// TODO: connect to the gameServer & start the round
				break;
			default:
				GameLog.log(MsgType.NETWORK_ERROR, "Bad packet received: " + packet);
				break;
			}
		}
	}
}