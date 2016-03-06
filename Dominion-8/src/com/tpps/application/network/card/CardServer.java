package com.tpps.application.network.card;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.Server;
import com.tpps.application.storage.CardStorageController;

public class CardServer extends Server {
	private static final String SERVER_INTERFACE = "0.0.0.0";
	private static final int PORT = 1336;

	private CardStorageController serverStorage;

	public CardServer(SocketAddress address, PacketHandler _handler, CardStorageController cardStorage)
			throws IOException {
		super(address, _handler);
		this.serverStorage = cardStorage;
		this.serverStorage.loadCards();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> onApplicationExit()));
	}

	public static void main(String[] input) throws IOException {
		CardStorageController tmpStorage = new CardStorageController("serverCards.bin");
		new CardServer(new InetSocketAddress(SERVER_INTERFACE, PORT), new CardPacketHandlerServer(tmpStorage),
				tmpStorage);
	}

	public void onApplicationExit() {
		this.serverStorage.saveCards();
	}
}