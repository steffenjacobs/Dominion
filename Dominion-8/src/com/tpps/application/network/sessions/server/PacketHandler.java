package com.tpps.application.network.sessions.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;
import com.tpps.application.network.sessions.packets.PacketSessionCheckAnswer;
import com.tpps.application.network.sessions.packets.PacketSessionCheckRequest;
import com.tpps.application.network.sessions.packets.PacketSessionGetAnswer;
import com.tpps.application.network.sessions.packets.PacketSessionGetRequest;
import com.tpps.application.network.sessions.packets.PacketSessionKeepAlive;

/**
 * this class handles all the packet-stuff
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class PacketHandler {

	// represents the server-instance
	private SessionServer parent;

	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	/**
	 * is called when a packet wass received, unpacks the packet
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void onPacketReceived(int port, byte[] data) {
		// unpack the packet & handle async
		new Thread(() -> handlePacket(port, PacketType.getPacket(data))).start();
	}

	/**
	 * outputs a String to the console
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	// TODO: save log
	public static void output(String str) {
		System.out.println(sdf.format(new Date()) + ": " + str);
	}

	/**
	 * is called in async thread when a packet was received
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private void handlePacket(int port, Packet packet) {

		ServerConnectionThread requester = parent.getClientThread(port);
		if (packet == null) {
			output("<- Empty Packet from (" + port + ")");
			return;
		}

		// TODO: implement event-system
		switch (packet.getType()) {
		case SESSION_KEEP_ALIVE:
			output("-> Kept Alive " + ((PacketSessionKeepAlive) packet).getUsername());
			SessionManager.revalidate(((PacketSessionKeepAlive) packet).getUsername());
			break;
		case SESSION_GET_REQUEST:
			try {
				PacketSessionGetRequest pack = (PacketSessionGetRequest) packet;
				output("-> Session-Get-Request for " + pack.getUsername());
				UUID uid = SessionManager.getValidSession(pack.getUsername());
				requester.sendMessage(PacketType.getBytes(new PacketSessionGetAnswer(pack, uid)));
				output("<- Created Session: " + pack.getUsername() + " - " + uid.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case SESSION_CHECK_REQUEST:
			try {
				PacketSessionCheckRequest pack = (PacketSessionCheckRequest) packet;
				output("-> Session-Check-Request for " + pack.getUsername());
				boolean result = SessionManager.isValid(pack.getUsername(), pack.getSessionID());
				requester.sendMessage(PacketType.getBytes(new PacketSessionCheckAnswer(pack, result)));
				output("<- Checked Session: " + pack.getUsername() + " - " + pack.getSessionID() + " - Result: "
						+ result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			output("<- Bad Packet: " + packet);
		}
	}

	/**
	 * sets the parent
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void setParent(SessionServer srv) {
		this.parent = srv;
	}
}
