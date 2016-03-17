package com.tpps.application.network.clientSession.client;

import com.tpps.application.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.application.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;

public class SessionPacketReceiver extends PacketHandler {

	/**
	 * is called when a packet is received
	 * 
	 * @author Steffen Jacobs
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