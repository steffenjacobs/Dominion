package com.tpps.application.network.clientSession.client;

import java.io.IOException;
import java.net.SocketAddress;

import com.tpps.application.network.core.Client;

public class SessionClient extends Client {

	public SessionClient(SocketAddress address) throws IOException{
		super(address, new SessionPacketReceiver());
	}
}
