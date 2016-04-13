package com.tpps.technicalServices.network.matchmaking.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * packet sent by the game server to the matchmaking-server when a round ends
 * 
 * @author Steffen Jacobs
 */
public class PacketGameEnd extends Packet {
	private static final long serialVersionUID = 1304847662526789376L;

	private final String[] players;
	private final String winner;

	/**
	 * constructor setting the packet-type & players
	 * 
	 * @param players
	 *            the player participating in the match and ending the match
	 * @param winner
	 *            the player who won
	 */
	public PacketGameEnd(String[] players, String winner) {
		super(PacketType.GAME_END);
		this.winner = winner;
		this.players = players;
	}

	/** @return a representation of the packet as a String */
	@Override
	public String toString() {
		String res = this.getClass().getSimpleName() + ": Winner:" + this.getWinner() + " - Participants: ";
		for (String pl : this.getPlayers()) {
			res += pl + " ";
		}
		return res;
	}

	/** @return the winner of the match */
	public String getWinner() {
		return winner;
	}

	/** @return the participating players in the match who ended the match */
	public String[] getPlayers() {
		return players;
	}
}