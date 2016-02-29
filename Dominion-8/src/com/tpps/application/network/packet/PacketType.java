package com.tpps.application.network.packet;

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
 * @author sjacobs - Steffen Jacobs
 * 
 *         NOTE: Add new Packets here. Please contact Steffen Jacobs for any
 *         questions & concerns.
 */
public enum PacketType {
	SESSION_GET_REQUEST(1), SESSION_GET_ANSWER(2), SESSION_KEEP_ALIVE(3), SESSION_CHECK_REQUEST(
			4), SESSION_CHECK_ANSWER(5), LOGIN_CHECK_REQUEST(6), LOGIN_CHECK_ANSWER(7), LOGIN_REGISTER_REQUEST(8), LOGIN_REGISTER_ANSWER(9), CARD_PLAYED(10), END_TURN(11);

	private final int internalID;

	/**
	 * initializs PacketType with id
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private PacketType(int id) {
		internalID = id;
	}

	/**
	 * @return packet-id
	 * @author sjacobs - Steffen Jacobs
	 */
	public int getID() {
		return internalID;
	}

	/**
	 * converts a Network-Packet to a byte-array
	 * 
	 * @return serialized packet
	 * @author sjacobs - Steffen Jacobs
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
	 * @author sjacobs - Steffen Jacobs
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