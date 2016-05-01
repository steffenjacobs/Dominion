package com.tpps.test.technicalServices.logger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import java.awt.Color;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLog;

public class GameLogTest {

	GameServer server;

	@Before
	public void setUp() throws Exception {
		this.server = new GameServer(1340, new String[10]);
		GameLog.init();
	}

	@Test
	public void testLog() {
		assertThat(this.server, is(notNullValue()));
		// int count = GameLog.getCountAndInc();
		// assertThat(count, is(1));
		try {
			server.broadcastMessage(new PacketBroadcastLog("", "", "Test", Color.GREEN));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// assertThat(GameLog.getAlreadyLogged(), is(1));
		// assertNull(GameLog.getWaitingLogs().get(1));
	}
}
