package com.tpps.application.network.clientSession.server;

import java.io.IOException;
import java.util.UUID;

import com.tpps.application.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.application.network.clientSession.packets.PacketSessionCheckRequest;
import com.tpps.application.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.application.network.clientSession.packets.PacketSessionGetRequest;
import com.tpps.application.network.clientSession.packets.PacketSessionKeepAlive;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;

/**
 * this class handles all the packet-stuff
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class SessionPacketHandler extends PacketHandler {


	/**
	 * is called in async thread when a packet was received
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void handleReceivedPacket(int port, byte[] bytes) {
		Packet packet = PacketType.getPacket(bytes);
		ServerConnectionThread requester = parent.getClientThread(port);
		if (packet == null) {
			super.output("<- Empty Packet from (" + port + ")");
			return;
		}

		// TODO: implement event-system
		switch (packet.getType()) {
		case SESSION_KEEP_ALIVE:
			super.output("-> Kept Alive " + ((PacketSessionKeepAlive) packet).getUsername());
			SessionManager.revalidate(((PacketSessionKeepAlive) packet).getUsername());
			break;
		case SESSION_GET_REQUEST:
			try {
				PacketSessionGetRequest pack = (PacketSessionGetRequest) packet;
				super.output("-> Session-Get-Request for " + pack.getUsername());
				UUID uid = SessionManager.getValidSession(pack.getUsername());
				requester.sendMessage(PacketType.getBytes(new PacketSessionGetAnswer(pack, uid)));
				super.output("<- Created Session: " + pack.getUsername() + " - " + uid.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case SESSION_CHECK_REQUEST:
			try {
				PacketSessionCheckRequest pack = (PacketSessionCheckRequest) packet;
				super.output("-> Session-Check-Request for " + pack.getUsername());
				boolean result = SessionManager.isValid(pack.getUsername(), pack.getSessionID());
				requester.sendMessage(PacketType.getBytes(new PacketSessionCheckAnswer(pack, result)));
				super.output("<- Checked Session: " + pack.getUsername() + " - " + pack.getSessionID() + " - Result: "
						+ result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			super.output("<- Bad Packet: " + packet);
		}
	}
}
