package com.tpps.technicalServices.network.matchmaking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.core.events.NetworkListener;

public class MatchmakingServer extends Server {

	public final static int PORT_MATCHMAKING = 1340;

	public static void main(String[] args) throws IOException {
		new MatchmakingServer(new InetSocketAddress(Addresses.getAllInterfaces(), PORT_MATCHMAKING),
				new MatchmakingPacketHandler());
	}

	public MatchmakingServer(SocketAddress address, PacketHandler _handler) throws IOException {
		super(address, _handler);
		super.getListenerManager().registerListener(new MatchmakingListener());
		// TODO Auto-generated constructor stub
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
