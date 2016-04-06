package com.tpps.technicalServices.network.clientSession.client;

import com.tpps.technicalServices.logger.Log;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;

public class SessionPacketReceiver extends PacketHandler {

	/**
	 * is called when a packet is received
	 * 
	 * @author Steffen Jacobs
	 */
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		if (packet == null) {
			Log.log(MsgType.NETWORK_ERROR, "Bad packet.");
		} else {
			if (SessionClient.debug())
				Log.log(MsgType.NETWORK_INFO, packet.toString());
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