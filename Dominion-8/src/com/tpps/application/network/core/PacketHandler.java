package com.tpps.application.network.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tpps.application.network.packet.Packet;

public abstract class PacketHandler {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	// represents the server-instance who's packets are handled by this PacketHandler-instance
	protected Server parent;
	
	public abstract void handleReceivedPacket(int port, Packet packet);
	

	/**
	 * outputs a String to the console
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	// TODO: save log
	public void output(String str) {
		System.out.println(sdf.format(new Date()) + ": " + str);
	}

	/**
	 * constructor with parent server-instance
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketHandler(Server _parent){
		this.parent = _parent;
	}
	
	public PacketHandler(){
		
	}
	
	public void setParent(Server _parent){
		this.parent =_parent;
	}
}
