package com.tpps.technicalServices.network.matchmaking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.core.events.NetworkListener;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingPlayerInfo;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingSuccessful;

public class MatchmakingServer extends Server {

	public final static int PORT_MATCHMAKING = 1340;

	public static void main(String[] args) throws IOException {
		new MatchmakingServer(new InetSocketAddress(Addresses.getAllInterfaces(), PORT_MATCHMAKING),
				new MatchmakingPacketHandler());
	}

	public MatchmakingServer(SocketAddress address, PacketHandler _handler) throws IOException {
		super(address, _handler);
		super.getListenerManager().registerListener(new MatchmakingListener());
	}

	public void sendJoinPacket(MPlayer receiver, String joinedPlayer) {
		
		PacketMatchmakingPlayerInfo pmpj = new PacketMatchmakingPlayerInfo(joinedPlayer, true);
		try {
			super.sendMessage(MatchmakingController.getPortFromPlayer(receiver), pmpj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendSuccessPacket(MPlayer receiver, String[] opponents) {

		PacketMatchmakingSuccessful pms = new PacketMatchmakingSuccessful(opponents,0);
		try {
			super.sendMessage(MatchmakingController.getPortFromPlayer(receiver), pms);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendQuitPacket(MPlayer receiver, String quittedPlayer) {
		PacketMatchmakingPlayerInfo pmpj = new PacketMatchmakingPlayerInfo(quittedPlayer, false);
		try {
			super.sendMessage(MatchmakingController.getPortFromPlayer(receiver), pmpj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class MatchmakingListener implements NetworkListener {

		@Override
		public void onClientConnect(int port) {
		}

		@Override
		public void onClientDisconnect(int port) {
			MatchmakingController.onPlayerDisconnect(port);
		}
	}

}
