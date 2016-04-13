package com.tpps.technicalServices.network.matchmaking.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * sent from the matchmaking-server to the clients in a lobby that is full and
 * the game is about to start
 * 
 * @author Steffen Jacobs
 */
public class PacketMatchmakingSuccessful extends Packet {
	private static final long serialVersionUID = 8824882326543059050L;

	private final String[] joinedPlayers;
	private final int gameserverPort;

	/**
	 * constructor for the Packet
	 * 
	 * @param joinedPlayers
	 *            the players in the lobby
	 * @param gameserverPort
	 *            the port of the gameserver that was started for the game
	 */
	public PacketMatchmakingSuccessful(String[] joinedPlayers, int gameserverPort) {
		super(PacketType.MATCHMAKING_SUCCESSFUL);
		this.joinedPlayers = joinedPlayers;
		this.gameserverPort = gameserverPort;
	}

	/** @return a representation of the packet as a String */
	@Override
	public String toString() {
		String res = this.getClass().getSimpleName() + ": Port:" + this.getGameserverPort() + " - Participants: ";
		for (String pl : this.getJoinedPlayers()) {
			res += pl + " ";
		}
		return res;
	}

	/** @return the players in the lobby */
	public String[] getJoinedPlayers() {
		return joinedPlayers;
	}

	/** @return the port of the gameserver for this lobby */
	public int getGameserverPort() {
		return gameserverPort;
	}

}
