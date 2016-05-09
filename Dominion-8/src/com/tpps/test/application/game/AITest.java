package com.tpps.test.application.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tpps.application.game.CardName;
import com.tpps.application.game.Deck;
import com.tpps.application.game.GameBoard;
import com.tpps.application.game.Player;
import com.tpps.application.game.ai.ArtificialIntelligence;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.chat.server.ChatServer;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.technicalServices.network.game.ServerGamePacketHandler;
import com.tpps.technicalServices.network.gameSession.packets.PacketRegistratePlayerByServer;
import com.tpps.test.technicalServices.network.TestPacketHandler;

/**
 * AI Test class
 * 
 * @author Nicolas
 *
 */
public class AITest {

	static Player player;
	static GameServer gameServer;
	static GameBoard board;
	static ArtificialIntelligence ai;

	/**
	 * 
	 */
	@BeforeClass
	public static void setUp() throws InterruptedException, IOException {
		GameLog.init();
		new Thread(() -> {
			try {
				new ChatServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				new SessionServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		board = new GameBoard(new String[] { CardName.MOAT.getName(), CardName.MILITIA.getName(), CardName.WITCH.getName(), CardName.THIEF.getName(), CardName.SPY.getName(),
				CardName.THRONEROOM.getName(), CardName.COUNCILROOM.getName(), CardName.ADVENTURER.getName(), CardName.CELLAR.getName(), CardName.CHAPEL.getName() });
		gameServer = new GameServer(1339, new String[] { CardName.MOAT.getName(), CardName.MILITIA.getName(), CardName.WITCH.getName(), CardName.THIEF.getName(), CardName.SPY.getName(),
				CardName.THRONEROOM.getName(), CardName.COUNCILROOM.getName(), CardName.ADVENTURER.getName(), CardName.CELLAR.getName(), CardName.CHAPEL.getName() });

		Client cl = new Client(new InetSocketAddress(Addresses.getLocalHost(), 1339), new TestPacketHandler());

		cl.sendMessage(new PacketRegistratePlayerByServer("test0", UUID.fromString("00000000-0000-0000-0000-000000000000")));
		cl.sendMessage(new PacketRegistratePlayerByServer("test1", UUID.fromString("00000000-0000-0000-0000-000000000000")));
		cl.sendMessage(new PacketRegistratePlayerByServer("test2", UUID.fromString("00000000-0000-0000-0000-000000000000")));
		cl.sendMessage(new PacketRegistratePlayerByServer("test3", UUID.fromString("00000000-0000-0000-0000-000000000000")));
		player = new Player(new Deck(board.getStartSet()), 1234, 1234, "Test0", UUID.fromString("00000000-0000-0000-0000-000000000000"), gameServer);

		ai = new ArtificialIntelligence(player, UUID.fromString("00000000-0000-0000-0000-000000000000"), new ServerGamePacketHandler());

		Thread.sleep(1000);
	}

	/**
	 * 
	 */
	@Test
	public void firstTest() {
		// ai.setBuyPhase();
		ai.getPlayer().getGameServer().getGameController().getGamePhase().equals("buyPhase");
	}
}
