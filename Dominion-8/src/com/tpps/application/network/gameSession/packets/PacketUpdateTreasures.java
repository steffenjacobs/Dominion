package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author ladler - Lukas Adler
 */
public class PacketUpdateTreasures extends Packet {

	private static final long serialVersionUID = 3379175636512685560L;
	private int coins;

	/**
	 * initializes the Packet with the actual amount of coins one player has
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketUpdateTreasures(int coins) {
		super(PacketType.UPDATE_TREASURES);
		this.coins = coins;
	}
	
	
	/**
	 * 
	 * @return the coins of the activePlayer
	 */
	public int getCoins() {
		return coins;
	}



	/**
	 * @return a readable String
	 * @author ladler - Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "coins: " + coins;
	}
}