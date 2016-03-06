package com.tpps.application.network.core.packet;

import com.tpps.technicalServices.util.ByteUtil;

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
			4), SESSION_CHECK_ANSWER(5), LOGIN_CHECK_REQUEST(6), LOGIN_CHECK_ANSWER(7), LOGIN_REGISTER_REQUEST(
					8), LOGIN_REGISTER_ANSWER(9), CARD_PLAYED(10), END_TURN(11), TEST(12), REGISTRATE_PLAYER_BY_SERVER(
							13), SEND_CLIENT_ID(14), CLIENT_SHOULD_DISCONECT(15), ENABLE_DISABLE(
									16), SEND_CHAT_ALL(17), SEND_CHAT_COMMAND(18), SEND_CHAT_ANSWER(19), CHAT_HANDSHAKE(
											20), RECONNECT(21), UPDATE_VALUES(
													22), SEND_HAND_CARDS(23), ENABLE_MONEY_CARDS_DISABLE_ACTION_CARDS(
															24), PLAY_TREASURES(25), BUY_CARD(
																	26), OPEN_GUI_AND_ENABLE_ONE(27), SEND_BOARD(28), UPDATE_COINS(29);

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
		return ByteUtil.getBytes(packet);
	}

	/**
	 * converts a byte-array to a packet
	 * 
	 * @return deserialized packet
	 * @author Steffen Jacobs
	 */
	public static Packet getPacket(byte[] bytes) {
		return (Packet) ByteUtil.getObject(bytes);
	}
}