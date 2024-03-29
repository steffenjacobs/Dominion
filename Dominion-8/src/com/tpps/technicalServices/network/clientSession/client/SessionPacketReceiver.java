package com.tpps.technicalServices.network.clientSession.client;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * PacketHandler for the client-end, receiving answer-packets from the server
 * 
 * @author Steffen Jacobs
 */
public class SessionPacketReceiver extends PacketHandler {

	/**
	 * is called when a packet is received
	 * 
	 * @param port
	 *            the port
	 * @param packet
	 *            the packet
	 */
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		if (packet == null) {
			GameLog.log(MsgType.NETWORK_ERROR, "Bad packet.");
		} else {
			if (SessionClient.debug())
				GameLog.log(MsgType.NETWORK_INFO, packet.toString());
			switch (packet.getType()) {
			case SESSION_CHECK_ANSWER:
				SessionPacketReceiverAPI.onPacketSessionCheckAnswer((PacketSessionCheckAnswer) packet);
				break;
			case SESSION_GET_ANSWER:
				SessionPacketReceiverAPI.onPacketSessionGetAnswer((PacketSessionGetAnswer) packet);
				break;
			default:
				break;
			}
		}
	}
}