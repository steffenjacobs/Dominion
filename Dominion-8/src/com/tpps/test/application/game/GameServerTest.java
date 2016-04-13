package com.tpps.test.application.game;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;

import com.tpps.technicalServices.network.game.GameServer;

public class GameServerTest {

	@Before
	public void setUp() throws Exception {
		new GameServer(1339);
	}

	@Test
	public void test() {
		assertThat(GameServer.getInstance(), is(notNullValue()));
	}

}
