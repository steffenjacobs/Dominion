package com.tpps.technicalServices.network.chat.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.chat.packets.PacketVotekick;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * This is a client just to send a packet to the given gameserver to kick a
 * client
 *
 * @author jhuhn
 */
public class VotekickClient extends PacketHandler{
	
	private Client gameclient;
	
	/**
	 * initialized the client
	 * 
	 * @author jhuhn
	 * @param gameserverPort Integer of the GameServer instance
	 */
	public VotekickClient(int gameserverPort) {
		try {
			this.gameclient = new Client(new InetSocketAddress(
					Addresses.getLocalHost(), gameserverPort), this, false);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * sends the packet to the GameServer to kick a client
	 * 
	 * @author jhuhn
	 * @param username
	 *            String representation of the user who gets kicked
	 */
	public void sendVotekickPacket(String username){
		try {
			this.gameclient.sendMessage(new PacketVotekick(username));
		} catch (IOException e) {		
			e.printStackTrace();
		}
		System.out.println("sent packet to gameserver to kick client " + username);
//		this.gameclient.disconnect();
	}
	

	@Override
	public void handleReceivedPacket(int port, Packet packet) {	}

}
