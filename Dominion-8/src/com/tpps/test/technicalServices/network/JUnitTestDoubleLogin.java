package com.tpps.test.technicalServices.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.Test;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.clientSession.client.SessionPacketSenderAPI;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.core.SuperCallable;

public class JUnitTestDoubleLogin {

	static boolean test1, test2;

	@Test
	public void test() throws IOException, InterruptedException {
		new Thread(() -> {
			try {
				new SessionServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		Thread.sleep(1000);
		SessionClient client = new SessionClient(new InetSocketAddress(Addresses.getLocalHost(), 1337));
		String nickname = "TEST";
		
		SessionPacketSenderAPI.sendGetRequest(client, nickname, new SuperCallable<PacketSessionGetAnswer>() {
			@Override
			public PacketSessionGetAnswer callMeMaybe(PacketSessionGetAnswer answer) {
				assertEquals(0, answer.getAnswerCode());
				test1 = true;
				return null;
			}
		});

		Thread.sleep(250);

		SessionPacketSenderAPI.sendGetRequest(client, nickname, new SuperCallable<PacketSessionGetAnswer>() {
			@Override
			public PacketSessionGetAnswer callMeMaybe(PacketSessionGetAnswer answer) {
				assertEquals(1, answer.getAnswerCode());
				test2 = true;
				return null;
			}
		});

		Thread.sleep(250);

		assertTrue(test1);
		assertTrue(test2);
	}
}