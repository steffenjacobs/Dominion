package com.tpps.technicalServices.network.core.packet;

import com.tpps.technicalServices.util.ByteUtil;

/**
 * represents a packet type
 * 
 * @author Steffen Jacobs NOTE: Add new Packets here. Please contact Steffen Jacobs for any
 *         questions & concerns.
 */
public enum PacketType {
	
	SESSION_GET_REQUEST(1),
	SESSION_GET_ANSWER(2),
	SESSION_KEEP_ALIVE(3),
	SESSION_CHECK_REQUEST(4),
	SESSION_CHECK_ANSWER(5),
	LOGIN_CHECK_REQUEST(6),
	LOGIN_CHECK_ANSWER(7),
	LOGIN_REGISTER_REQUEST(8),
	LOGIN_REGISTER_ANSWER(9),
	CARD_PLAYED(10),
	END_TURN(11),
	TEST(12),
	REGISTRATE_PLAYER_BY_SERVER(13),
	SEND_CLIENT_ID(14),
	CLIENT_SHOULD_DISCONECT(15),
	ENABLE_DISABLE(16),
	SEND_CHAT_ALL(17),
	SEND_CHAT_COMMAND(18),
	SEND_CHAT_ANSWER(19),
	CHAT_HANDSHAKE(20),
	RECONNECT(21),
	UPDATE_VALUES(22),
	SEND_HAND_CARDS(23),
	END_ACTION_PHASE(24),
	PLAY_TREASURES(25),
	BUY_CARD(26),
	OPEN_GUI_AND_ENABLE_ONE(27),
	SEND_BOARD(28),
	CARD_CHECK_IF_CARD_EXISTS_REQUEST(29),
	CARD_CHECK_IF_CARD_EXISTS_ANSWER(30),
	CARD_ADD_CARD(31),
	CARD_GET_CARD_REQUEST(32),
	CARD_GET_CARD_ANSWER(33),
	UPDATE_TREASURES(34),
	SEND_CHAT_TO_CLIENT(35),
	SEND_CHAT_VOTE(36),
	SEND_PLAYED_CARDS_TO_ALL_CLIENTS(37),
	CHAT_CONTROLLER(38),
	END_DISCARD_MODE(39),
	START_DISCARD_MODE(40),
	END_TRASH_MODE(41),
	START_TRASH_MODE(42),
	DISCARD_DECK(43),
	ENABLE_OTHERS(44),
	DISABLE(45),
	ENABLE(46),
	SHOW_END_REACTION_MODE(47),
	END_REACTIONS(48),
	SEND_REVEAL_CARDS(49),
	TAKE_CARDS(50),
	PUT_BACK_CARDS(51),
	REMOVE_EXTRA_TABLE(52),
	SEND_ACTIVE_BUTTONS(53),
	TEMPORARY_TRASH_CARDS(54),
	TAKE_THIEF_CARDS(55),
	PUT_BACK_THIEF_CARDS(56),
	DONT_SHOW_END_REACTION_MODE(57),
	TAKE_DRAWED_CARD(58),
	SET_ASIDE_DRAWED_CARD(59),
	GET_ALL_STATISTICS(60),
	MATCHMAKING_REQUEST(61),
	MATCHMAKING_ANSWER(62),
	MATCHMAKING_PLAYER_INFO(63),
	MATCHMAKING_SUCCESSFUL(64),
	UNUSED(65),
	GAME_END(66),
	BROADCAST_LOG(67), 
	MATCHMAKING_JOIN_LOBBY(68);

	private final int internalID;

	/**
	 * initializs PacketType with id
	 * 
	 * @author Steffen Jacobs
	 * @param id the internal integer-id
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
	 * @param packet the packet to convert
	 */
	public static byte[] getBytes(Packet packet) {
		return ByteUtil.getBytes(packet);
	}

	/**
	 * converts a byte-array to a packet
	 * 
	 * @return deserialized packet
	 * @author Steffen Jacobs
	 * @param bytes the serialized packet to convert
	 */
	public static Packet getPacket(byte[] bytes) {
		return (Packet) ByteUtil.getObject(bytes);
	}
}