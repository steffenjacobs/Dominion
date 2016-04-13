package com.tpps.technicalServices.network.matchmaking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.core.events.NetworkListener;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingPlayerInfo;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingSuccessful;

public class MatchmakingServer extends Server {

	private static MatchmakingServer instance;

	public final static int PORT_MATCHMAKING = 1340;

	public static void main(String[] args) throws IOException {
		instance = new MatchmakingServer(new InetSocketAddress(Addresses.getAllInterfaces(), PORT_MATCHMAKING),
				new MatchmakingPacketHandler());
	}

	public MatchmakingServer(SocketAddress address, PacketHandler _handler) throws IOException {
		super(address, _handler);
		super.getListenerManager().registerListener(new MatchmakingListener());
	}
	
	public void sendJoinPacket(MPlayer receiver, String joinedPlayer) {
		ArrayList<MPlayer> tmp = new ArrayList<>();
		tmp.add(receiver);
		sendJoinPacket(tmp, joinedPlayer);
	}

	public void sendJoinPacket(Collection<MPlayer> receivers, String joinedPlayer) {

		PacketMatchmakingPlayerInfo pmpj = new PacketMatchmakingPlayerInfo(joinedPlayer, true);
		try {
			for (MPlayer receiver : receivers) {
				super.sendMessage(MatchmakingController.getPortFromPlayer(receiver), pmpj);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendSuccessPacket(Collection<MPlayer> receivers, String[] opponents) {

		PacketMatchmakingSuccessful pms = new PacketMatchmakingSuccessful(opponents, 0);
		try {
			for (MPlayer receiver : receivers) {
				super.sendMessage(MatchmakingController.getPortFromPlayer(receiver), pms);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendQuitPacket(Collection<MPlayer> receivers, String quittedPlayer) {
		PacketMatchmakingPlayerInfo pmpj = new PacketMatchmakingPlayerInfo(quittedPlayer, false);
		try {
			for (MPlayer receiver : receivers) {
				super.sendMessage(MatchmakingController.getPortFromPlayer(receiver), pmpj);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static MatchmakingServer getInstance() {
		return instance;
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
