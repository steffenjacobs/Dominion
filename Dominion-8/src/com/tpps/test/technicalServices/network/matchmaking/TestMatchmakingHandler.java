package com.tpps.test.technicalServices.network.matchmaking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingAnswer;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingPlayerInfo;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingSuccessful;

/**
 * client packet-handler for the matchmaking
 * 
 * @author Steffen Jacobs
 */
public class TestMatchmakingHandler extends PacketHandler {

	/**
	 * 
	 */
	public AtomicInteger[] checks;
	/**
	 * 
	 */
	public String username;

	/**
	 * creates the atomic-integers used for testing
	 */
	public TestMatchmakingHandler() {
		checks = new AtomicInteger[3];
		for (int i = 0; i < 3; i++) {
			checks[i] = new AtomicInteger(0);
		}
	}

	/**trivial*/
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		System.out.println(System.identityHashCode(this) + ": " + packet);

		switch (packet.getType()) {
		case MATCHMAKING_ANSWER:
			// is called when this specific player was added to the
			// matchmaking-system
			PacketMatchmakingAnswer pma = (PacketMatchmakingAnswer) packet;
			// 4 checks
			assertEquals(1, pma.getAnswerCode());
			checks[0].incrementAndGet();
			break;
		case MATCHMAKING_PLAYER_INFO:
			// is called when a player joined or quitted the lobby
			PacketMatchmakingPlayerInfo pmpi = (PacketMatchmakingPlayerInfo) packet;
			// 1+1+2+1+1+3+1+1+4 = 16 checks
			assertTrue(pmpi.getPlayerName().startsWith(username));
			checks[1].incrementAndGet();

			break;
		case MATCHMAKING_SUCCESSFUL:
			// is called, when the lobby is full and the game starts
			PacketMatchmakingSuccessful pms = (PacketMatchmakingSuccessful) packet;
			// 5*4 = 20 checks
			assertEquals(4, pms.getJoinedPlayers().length);
			checks[2].incrementAndGet();
			for (int i = 0; i < 4; i++) {
				assertTrue(pms.getJoinedPlayers()[i].startsWith(username));
				checks[2].incrementAndGet();
			}
			System.out.println("start Packet received :) " + packet);
			break;
		default:
			System.err.println("Bad packet received: " + packet);
			break;
		}
	}
}
