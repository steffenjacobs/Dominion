package com.tpps.technicalServices.network.gameSession.packets;

import java.util.LinkedHashMap;

import com.tpps.application.game.card.Tuple;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client every time there are already
 * too much player on the server
 * 
 * @author ladler - Lukas Adler
 */
public class PacketShowEndScreen extends Packet {

	private static final long serialVersionUID = -6159725370768320315L;
	private LinkedHashMap<String, Tuple<String>> namePoints;


	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketShowEndScreen() {
		super(PacketType.SHOW_END_SCREEN);
		this.namePoints = new LinkedHashMap<String, Tuple<String>>();
		
	}
	
	public void add(String player, String name, int points) {
		namePoints.put(player, new Tuple<String>(name, points));
	
	}
	
	public String getNameForPlayer(String player) {
		return this.namePoints.get(player).getFirstEntry();
	}
	
	public int getPointsForPlayer(String player) {
		return this.namePoints.get(player).getSecondEntry();
	}
	
	public int getPlayerAmount() {
		return this.namePoints.size();
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