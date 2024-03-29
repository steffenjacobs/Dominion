package com.tpps.technicalServices.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * de-/serialzes objects
 * 
 * @author Steffen Jacobs
 */
public class ByteUtil {
	/**
	 * converts an Object to a byte-array using java
	 * native-serialization-techniques
	 * 
	 * @return serialized object
	 * @param packet the object to serialized
	 */
	public static byte[] getBytes(Object packet) {
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
				out.flush();
				out.close();
				bos.close();
			} catch (IOException ex) {
			}
		}
		return res;

	}

	/**
	 * converts a byte-array to an object
	 * @param bytes the bytes to deserialize
	 * 
	 * @return deserialized object
	 */
	public static Object getObject(byte[] bytes) {
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
				in.close();
				bis.close();
			} catch (IOException ex) {
			}
		}
		return (Packet) res;
	}

	/**
	 * converts an integer to a byte-array
	 * 
	 * @param value
	 *            integer to convert
	 * @return converted integer
	 */
	public static final byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
	}

	/**
	 * converts a long to a byte-array
	 * 
	 * @param value
	 *            long to convert
	 * @return converted long
	 */
	public static final byte[] longToByteArray(long value) {
		return new byte[] { (byte) (value >>> 56), (byte) (value >>> 48), (byte) (value >>> 40), (byte) (value >>> 32),
				(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
	}

}
