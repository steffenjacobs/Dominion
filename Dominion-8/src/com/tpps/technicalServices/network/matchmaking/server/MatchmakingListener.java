package com.tpps.technicalServices.network.matchmaking.server;

import com.tpps.technicalServices.network.core.events.NetworkListener;

public class MatchmakingListener implements NetworkListener {

	@Override
	public void onClientConnect(int port) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClientDisconnect(int port) {
		MatchmakingController.onPlayerDisconnect(port);
	}

}
