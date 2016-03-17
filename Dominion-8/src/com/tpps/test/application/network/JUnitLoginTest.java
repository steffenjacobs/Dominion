package com.tpps.test.application.network;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Test;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;
import com.tpps.application.network.login.client.LoginClient;
import com.tpps.application.network.login.packets.PacketLoginCheckAnswer;
import com.tpps.application.network.login.packets.PacketRegisterAnswer;
import com.tpps.ui.loginscreen.LoginGUIController;

public class JUnitLoginTest {
	final boolean DO_BAD_PW_BULK_TEST = false;
	final boolean DO_BULK_REGISTER_TEST = false;

	@Test
	public void test() throws InterruptedException {

		LoginGUIController cont = new LoginGUIController();

		LoginClient lc = new LoginClient(cont);
		TestPacketHandler handler = new TestPacketHandler();
		lc.getClient().addPacketHandler(handler);
		
		Thread.sleep(100);

		//test registration
		String accountname = "" + (Long.MAX_VALUE - System.currentTimeMillis());
		handler.clearPackets();
		
		lc.handleAccountCreation(accountname, "password", accountname + "@test.tld");
		Thread.sleep(600);
		
		assertEquals(1, handler.countPackets());
		assertEquals(PacketType.LOGIN_REGISTER_ANSWER, handler.getReceivedPackets().get(0).getType());
		assertEquals(1, ((PacketRegisterAnswer)handler.getReceivedPackets().get(0)).getState());
		
		handler.clearPackets();
		
		//same nick-name
		lc.handleAccountCreation(accountname, "password", accountname + "@test1.tld");

		Thread.sleep(600);
		
		assertEquals(1, handler.countPackets());
		assertEquals(PacketType.LOGIN_REGISTER_ANSWER, handler.getReceivedPackets().get(0).getType());
		assertEquals(2, ((PacketRegisterAnswer)handler.getReceivedPackets().get(0)).getState());
		
		handler.clearPackets();
		
		//Same email-address
		lc.handleAccountCreation(accountname + "_", "password", accountname + "@test.tld");
		
		Thread.sleep(600);
		
		assertEquals(1, handler.countPackets());
		assertEquals(PacketType.LOGIN_REGISTER_ANSWER, handler.getReceivedPackets().get(0).getType());
		assertEquals(3, ((PacketRegisterAnswer)handler.getReceivedPackets().get(0)).getState());
		

		if (DO_BAD_PW_BULK_TEST) {
			// prepare for bulk-test
			final int LOGIN_REQUEST_COUNT = 10000;
			handler.clearPackets();

			// bulk-test with bad pws
			for (int i = 0; i < LOGIN_REQUEST_COUNT; i++) {

				lc.handlelogin("aaa", "badpw");
			}

			// wait until finished
			Thread.sleep(15000);

			// check results
			assertEquals(LOGIN_REQUEST_COUNT, handler.countPackets());
		}

		if (DO_BULK_REGISTER_TEST) {

			// bulk-test with registering
			CopyOnWriteArrayList<Long> createdAccounts = new CopyOnWriteArrayList<>();
			final int REGISTER_REQUEST_COUNT = 100;
			handler.clearPackets();
			final long relName = System.currentTimeMillis();

			// bulk-test registration
			for (int i = 0; i < REGISTER_REQUEST_COUNT; i++) {
				createdAccounts.add(relName + i);
				lc.handleAccountCreation("TEST#" + relName + i, "password", relName + i + "@test.tld");
			}

			// wait...
			Thread.sleep(15000);

			// check if all requests were answered
			assertEquals(REGISTER_REQUEST_COUNT, handler.countPackets());
			for (Packet pack : handler.getReceivedPackets()) {
				assertEquals(PacketType.LOGIN_REGISTER_ANSWER, pack.getType());
				assertEquals(1, ((PacketRegisterAnswer) pack).getState());
			}

			// prepare check
			handler.clearPackets();

			// check if accounts are still there
			for (Long l : createdAccounts) {
				lc.handlelogin("TEST#" + l.longValue(), "password");
			}

			// wait...
			Thread.sleep(12000);

			// check if login was successful for all packets
			assertEquals(createdAccounts.size(), handler.countPackets());

			for (Packet pack : handler.getReceivedPackets()) {
				assertEquals(PacketType.LOGIN_CHECK_ANSWER, pack.getType());
				assertEquals(true, ((PacketLoginCheckAnswer) pack).getState());
			}
		}

	}
}