package com.tpps.technicalServices.network.gameSession.packets;



import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client every time there are already
 * too much player on the server
 * 
 * @author ladler - Lukas Adler
 */
public class PacketBroadcastLog extends Packet {

	private static final long serialVersionUID = 6438319829526897629L;
	private final String msg;
	private final MsgType msgType;
	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketBroadcastLog(MsgType msgType, String msg) {
		super(PacketType.BROADCAST_LOG);
		this.msg = msg;
		this.msgType = msgType;
	}
	
	public MsgType getMsgType(){
		return this.msgType;
	}
	
	public String getMessage(){
		return this.msg;
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