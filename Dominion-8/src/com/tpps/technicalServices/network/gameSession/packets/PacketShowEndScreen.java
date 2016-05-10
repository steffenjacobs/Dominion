package com.tpps.technicalServices.network.gameSession.packets;

import java.util.LinkedHashMap;

import com.tpps.application.game.card.Tuple;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a client to server to show the end screen
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
	
	/**
	 * add al player to the player list
	 */
	public void add(String player, String name, int points) {
		namePoints.put(player, new Tuple<String>(name, points));
	
	}
	
	/**
	 * @param player
	 * @return name for a player
	 */
	public String getNameForPlayer(String player) {
		return this.namePoints.get(player).getFirstEntry();
	}
	
	/**
	 * 
	 * @param player
	 * @return points for a player
	 */
	public int getPointsForPlayer(String player) {
		return this.namePoints.get(player).getSecondEntry();
	}
	
	/**
	 * 
	 * @return the amount of players in the linkedhashmap
	 */
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