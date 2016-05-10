package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client to show the active buttons
 * 
 * @author ladler - Lukas Adler
 */
public class PacketSendActiveButtons extends Packet {

	private static final long serialVersionUID = -636636834089606633L;
	private boolean endTurn, endActionPhase, playTreasures;

	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketSendActiveButtons(boolean endTurn, boolean endActionPhase, boolean playTreasures) {		
		super(PacketType.SEND_ACTIVE_BUTTONS);
		this.endTurn = endTurn;
		this.endActionPhase = endActionPhase;
		this.playTreasures = playTreasures;
	}
	
	


	public boolean isEndTurn() {
		return endTurn;
	}




	public boolean isEndActionPhase() {
		return endActionPhase;
	}




	public boolean isPlayTreasures() {
		return playTreasures;
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