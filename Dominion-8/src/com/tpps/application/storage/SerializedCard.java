package com.tpps.application.storage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.util.GraphicsUtil;

/**
 * represents a Card which can be serialized very efficiently
 * 
 * @author Steffen Jacobs
 */
public class SerializedCard {

	private final LinkedHashMap<CardAction, String> actions;
	private final LinkedList<CardType> types;
	private final int cost;
	private final String name;
	private final BufferedImage image;

	/**
	 * constructor for SerializedCard, taking all required data
	 * 
	 * @param action
	 *            the list of CardActions mapped to their value
	 * @param types
	 *            the list of CardTypes
	 * @param cost
	 *            the card-cost
	 * @param name
	 *            the name of the card
	 * @param image
	 *            the image of the card (ARGB or RGB)
	 */
	public SerializedCard(LinkedHashMap<CardAction, String> actions, LinkedList<CardType> types, int cost, String name,
			BufferedImage image) {
		this.actions = actions;
		this.types = types;
		this.cost = cost;
		this.name = name;
		this.image = image;
	}
	
	/**
	 * copy constructor
	 * 
	 * @param serializedCard the card to copy
	 */
	public SerializedCard(SerializedCard serializedCard) {
		this.actions = serializedCard.getActions();
		this.types = serializedCard.getTypes();
		this.cost = serializedCard.getCost();
		this.name = serializedCard.getName();
		this.image = serializedCard.getImage();
	}

	/**
	 * constructor for SerializedCard, taking a serialized byte-array with all
	 * data
	 * 
	 * @param bytes
	 *            byte-array containing all important data
	 */
	public SerializedCard(byte[] bytes) throws IOException {
		if (bytes.length == 0) {
			throw new RuntimeException("ERROR: Bad Card-Storage.");
		}
		ByteBuffer buff = ByteBuffer.wrap(bytes);

		byte[] arr = new byte[buff.getInt()];
		buff.get(arr);
		this.image = ImageIO.read(new ByteArrayInputStream(arr));

		int length = buff.getInt();
		arr = new byte[length];
		buff.get(arr);
		this.name = new String(arr, Charset.forName("UTF-8"));
		this.cost = buff.getInt();

		this.types = new LinkedList<>();
		length = buff.getInt();
		for (int cnt = 0; cnt < length; cnt++) {
			arr = new byte[buff.getInt()];
			buff.get(arr);
			this.types.add(CardType.valueOf(new String(arr)));
		}

		this.actions = new LinkedHashMap<>();
		length = buff.getInt();
		String tmp;
		for (int cnt = 0; cnt < length; cnt++) {
			arr = new byte[buff.getInt()];
			buff.get(arr);
			tmp = new String(arr);
			arr = new byte[buff.getInt()];
			buff.get(arr);
			this.actions.put(CardAction.valueOf(tmp), new String(arr));
		}
	}

	/**
	 * getter for the actions of the card
	 * 
	 * @return actions of the card
	 */
	public LinkedHashMap<CardAction, String> getActions() {
		return this.actions;
	}

	/**
	 * getter for the types of the card
	 * 
	 * @return types of the card
	 */
	public LinkedList<CardType> getTypes() {
		return this.types;
	}

	/**
	 * getter for the cost of the card
	 * 
	 * @return cost of the card
	 */
	public int getCost() {
		return this.cost;
	}

	/**
	 * getter for the name of the card
	 * 
	 * @return name of the card
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * getter for the image of the card
	 * 
	 * @return image of the card
	 */
	public BufferedImage getImage() {
		return this.image;
	}

	/**
	 * converts this instance to a byte-array
	 * 
	 * @return a serialized representation of this instance of a SerializedCard
	 */
	public byte[] getBytes() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", byteStream);

			byte[] serializedJPG = byteStream.toByteArray();

			ByteBuffer buff = ByteBuffer.allocate(serializedJPG.length + 1024);
			buff.putInt(serializedJPG.length);
			buff.put(serializedJPG);

			byte[] tmp = this.name.getBytes(Charset.forName("UTF-8"));
			buff.putInt(tmp.length);
			buff.put(tmp);
			buff.putInt(this.cost);
			buff.putInt(this.types.size());
			for (CardType ct : this.types) {
				tmp = ct.name().getBytes(Charset.forName("UTF-8"));
				buff.putInt(tmp.length);
				buff.put(tmp);
			}

			buff.putInt(this.actions.size());
			for (Entry<CardAction, String> entr : this.actions.entrySet()) {
				buff.putInt(entr.getKey().name().length());
				buff.put(entr.getKey().name().getBytes());
				tmp = entr.getValue().getBytes(Charset.forName("UTF-8"));
				buff.putInt(tmp.length);
				buff.put(tmp);
			}
			return buff.array();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * equals-by-name method
	 * 
	 * @param other
	 *            second object to compare with
	 * 
	 * @return whether the two objects are equal by name
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof SerializedCard) {
			return this.getName().equals(((SerializedCard) other).getName());
		}
		return false;
	}

	/**
	 * equals-by-all-properties-method - needed for testing
	 * 
	 * @param card
	 *            second object to compare with
	 * @return whether every property matches
	 */
	public boolean equalsEntirely(SerializedCard card) {
		if (!this.equals(card))
			return false;
		if (this.cost != card.cost)
			return false;
		if (!this.actions.equals(card.actions))
			return false;
		if (!this.types.equals(card.types))
			return false;
		if (!GraphicsUtil.compareImages(this.image, card.image))
			return false;
		return true;
	}

	/**
	 * overridden toString-method
	 * 
	 * @return a readable representation of the object
	 */
	@Override
	public String toString() {
		String res = "--" + this.getClass().getSimpleName() + "--\nName: " + this.name + "\nPrice:" + this.cost
				+ "\nTypes:\n";
		for (CardType ct : this.types) {
			res += "- " + ct.name() + "\n";
		}
		res += "Actions:\n";
		for (Entry<CardAction, String> entr : this.actions.entrySet()) {
			res += "- " + entr.getKey().name() + " - " + entr.getValue();
		}
		res += "\n-- --";
		return res;
	}
}
