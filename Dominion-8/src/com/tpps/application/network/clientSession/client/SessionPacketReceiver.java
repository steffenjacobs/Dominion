package com.tpps.application.network.clientSession.client;

import com.tpps.application.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.application.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.packet.Packet;

public class SessionPacketReceiver extends PacketHandler {

	/**
	 * is called when a packet is received
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */

	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		if (packet == null) {
			System.out.println("Bad packet.");
		} else {
			if (SessionTestClient.DEBUG_PACKETS)
				System.out.println(packet.toString());
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