package com.tpps.test.application.game;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;

import com.tpps.technicalServices.network.game.GameServer;

public class GameServerTest {
	GameServer gameServer;

	@Before
	public void setUp() throws Exception {
		this.gameServer = new GameServer(1339, new String[10]);
	}

	@Test
	public void test() {
		assertThat(this.gameServer, is(notNullValue()));
	}

}
