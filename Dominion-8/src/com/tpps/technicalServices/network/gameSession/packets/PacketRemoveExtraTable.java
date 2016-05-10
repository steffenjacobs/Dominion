package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client to remove the extra table
 * 
 * @author ladler - Lukas Adler
 */
public class PacketRemoveExtraTable extends Packet {

	private static final long serialVersionUID = 6122808614252399592L;


	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketRemoveExtraTable() {		
		super(PacketType.REMOVE_EXTRA_TABLE);
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