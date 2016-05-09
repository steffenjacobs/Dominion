package com.tpps.test.application.game;

import java.io.IOException;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tpps.application.game.CardName;
import com.tpps.application.game.Deck;
import com.tpps.application.game.GameBoard;
import com.tpps.application.game.Player;
import com.tpps.application.game.ai.ArtificialIntelligence;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.network.chat.server.ChatServer;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.technicalServices.network.game.ServerGamePacketHandler;

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
	public static void setUp() {
		GameLog.init();
		new Thread(() -> {
			try {
				new ChatServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		
		new Thread(()->{try {
			new SessionServer();
		} catch (Exception e) {
			e.printStackTrace();
		}}).start();
		try {
			board = new GameBoard(new String[] { CardName.MOAT.getName(), CardName.MILITIA.getName(),
					CardName.WITCH.getName(), CardName.THIEF.getName(), CardName.SPY.getName(),
					CardName.THRONEROOM.getName(), CardName.COUNCILROOM.getName(), CardName.ADVENTURER.getName(),
					CardName.CELLAR.getName(), CardName.CHAPEL.getName() });
			gameServer = new GameServer(1339,
					new String[] { CardName.MOAT.getName(), CardName.MILITIA.getName(), CardName.WITCH.getName(),
							CardName.THIEF.getName(), CardName.SPY.getName(), CardName.THRONEROOM.getName(),
							CardName.COUNCILROOM.getName(), CardName.ADVENTURER.getName(), CardName.CELLAR.getName(),
							CardName.CHAPEL.getName() });
			player = new Player(new Deck(board.getStartSet()), 1234, 1234, "Test0",
					UUID.fromString("00000000-0000-0000-0000-000000000000"), gameServer);
		} catch (IOException e) {
		}
		ai = new ArtificialIntelligence(player, UUID.fromString("00000000-0000-0000-0000-000000000000"),
				new ServerGamePacketHandler());
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
