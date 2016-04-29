package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author ladler - Lukas Adler
 */
public class PacketUpdateValues extends Packet {

	private static final long serialVersionUID = 4833326642942479357L;
	private final int actions, buys, coins;
	private final boolean shouldBeEnabled;

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketUpdateValues(int actions, int buys, int coins, boolean shouldBeEnabled) {
		super(PacketType.UPDATE_VALUES);
		this.actions = actions;
		this.buys = buys;
		this.coins = coins;
		this.shouldBeEnabled = shouldBeEnabled;
		
	}
	
	/**
	 * 
	 * @return if the gameWindow should be enabled again
	 */
	public boolean shouldBeEnabled() {
		return this.shouldBeEnabled;
	}
	
	/**
	 * 
	 * @return the actions of the activePlayer
	 */
	public int getActions() {
		return actions;
	}


	/**
	 * 
	 * @return the buys of the activePlayer
	 */
	public int getBuys() {
		return buys;
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
		return this.getClass().getSimpleName() + "coins: " + coins + "buys: " + buys + "actions: " + actions;
	}
}