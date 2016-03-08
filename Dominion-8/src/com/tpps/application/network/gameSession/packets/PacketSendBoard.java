package com.tpps.application.network.gameSession.packets;

import java.util.LinkedList;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author ladler - Lukas Adler
 */
public class PacketSendBoard extends Packet {

	private static final long serialVersionUID = 4282913282327428691L;
	LinkedList<String> actionCardIds;
	LinkedList<String> coinCardIds;
	LinkedList<String> victoryCardIds;

	/**
	 * initializes the Packet with the parameters listed below
	 * @param coinCardIds the ids of the uppest coinCards on the board
	 * @param victoryCardIds the ids of the uppest victoryCards on the board
	 * @param actionCardIds the ids of the uppest actionCards on the board
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketSendBoard(LinkedList<String> coinCardIds,LinkedList<String> victoryCardIds, 
			LinkedList<String> actionCardIds) {
		super(PacketType.SEND_BOARD);
		this.coinCardIds = coinCardIds;
		this.victoryCardIds = victoryCardIds;
		this.actionCardIds = actionCardIds;		
	}
	
	
	/**
	 * 
	 * @return a LinkedList containing actionCardIds
	 */
	public LinkedList<String> getActionCardIds() {
		return actionCardIds;
	}
	
	/**
	 * 
	 * @return a LinkedList containing coinCardIds
	 */
	public LinkedList<String> getCoinCardIds() {
		return coinCardIds;
	}


	/**
	 * 
	 * @return a LinkedList containing victoryCardIds
	 */
	public LinkedList<String> getVictoryCardIds() {
		return victoryCardIds;
	}



	/**
	 * @return a readable String
	 * @author ladler - Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}