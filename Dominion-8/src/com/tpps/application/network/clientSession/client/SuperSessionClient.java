package com.tpps.application.network.clientSession.client;

import java.io.IOException;
import java.net.SocketAddress;

import com.tpps.application.network.core.Client;

public class SuperSessionClient extends Client {

	public SuperSessionClient(SocketAddress address) throws IOException{
		super(address, new SessionPacketReceiver());
	}
}
