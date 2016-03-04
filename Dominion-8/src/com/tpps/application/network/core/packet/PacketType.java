package com.tpps.application.network.core.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * represents a packet type
 * 
 * @author Steffen Jacobs
 * 
 *         NOTE: Add new Packets here. Please contact Steffen Jacobs for any
 *         questions & concerns.
 */
public enum PacketType {
	SESSION_GET_REQUEST(1), SESSION_GET_ANSWER(2), SESSION_KEEP_ALIVE(3), SESSION_CHECK_REQUEST(
			4), SESSION_CHECK_ANSWER(5), LOGIN_CHECK_REQUEST(6), LOGIN_CHECK_ANSWER(7), 
	LOGIN_REGISTER_REQUEST(8), LOGIN_REGISTER_ANSWER(9), CARD_PLAYED(10), END_TURN(11), TEST(12),
	REGISTRATE_PLAYER_BY_SERVER(13), SEND_CLIENT_ID(14), CLIENT_SHOULD_DISCONECT(15), ENABLE_DISABLE(16),
	SEND_CHAT_ALL(17), SEND_CHAT_COMMAND(18), SEND_CHAT_ANSWER(19);

	private final int internalID;

	/**
	 * initializs PacketType with id
	 * 
	 * @author Steffen Jacobs
	 */
	private PacketType(int id) {
		internalID = id;
	}

	/**
	 * @return packet-id
	 * @author Steffen Jacobs
	 */
	public int getID() {
		return internalID;
	}

	/**
	 * converts a Network-Packet to a byte-array
	 * 
	 * @return serialized packet
	 * @author Steffen Jacobs
	 */
	public static byte[] getBytes(Packet packet) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] res = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(packet);
			res = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return res;
	}

	/**
	 * converts a byte-array to a packet
	 * 
	 * @return deserialized packet
	 * @author Steffen Jacobs
	 */
	public static Packet getPacket(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		Object res = null;
		try {
			in = new ObjectInputStream(bis);
			res = in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch (IOException ex) {
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
			}
		}
		return (Packet) res;
	}
}