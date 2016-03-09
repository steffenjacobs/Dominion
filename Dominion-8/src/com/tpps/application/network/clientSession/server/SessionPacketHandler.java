package com.tpps.application.network.clientSession.server;

import java.util.UUID;

import com.tpps.application.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.application.network.clientSession.packets.PacketSessionCheckRequest;
import com.tpps.application.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.application.network.clientSession.packets.PacketSessionGetRequest;
import com.tpps.application.network.clientSession.packets.PacketSessionKeepAlive;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.core.packet.Packet;

/**
 * this class handles all the packet-stuff
 * 
 * @author Steffen Jacobs
 */
public class SessionPacketHandler extends PacketHandler {

	/**
	 * is called in async thread when a packet was received
	 * 
	 * @author Steffen Jacobs
	 */
	public void handleReceivedPacket(int port, Packet packet) {
		ServerConnectionThread requester = parent.getClientThread(port);
		if (packet == null) {
			super.output("<- Empty Packet from (" + port + ")");
			return;
		}

		switch (packet.getType()) {
		case SESSION_KEEP_ALIVE:
			super.output("-> Kept Alive " + ((PacketSessionKeepAlive) packet).getUsername());
			SessionManager.revalidate(((PacketSessionKeepAlive) packet).getUsername());
			break;
		case SESSION_GET_REQUEST:
			PacketSessionGetRequest pack = (PacketSessionGetRequest) packet;
			super.output("-> Session-Get-Request for " + pack.getUsername());
			UUID uid = SessionManager.getValidSession(pack.getUsername());
			requester.addPacketToQueue(new PacketSessionGetAnswer(pack, uid));
			super.output("<- Created Session: " + pack.getUsername() + " - " + uid.toString());
			break;
		case SESSION_CHECK_REQUEST:
			SessionServer.log("req");
			PacketSessionCheckRequest pack2 = (PacketSessionCheckRequest) packet;
			super.output("-> Session-Check-Request for " + pack2.getUsername());
			boolean result = SessionManager.isValid(pack2.getUsername(), pack2.getSessionID());
			requester.addPacketToQueue(new PacketSessionCheckAnswer(pack2, result));
			super.output("<- Checked Session: " + pack2.getUsername() + " - " + pack2.getSessionID() + " - Result: "
					+ result);
			break;
		default:
			super.output("<- Bad Packet: " + packet);
		}
	}
}
