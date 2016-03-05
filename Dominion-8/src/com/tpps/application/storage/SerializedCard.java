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

/** represents a Card which can be serialized very efficiently */
public class SerializedCard {

	private final LinkedHashMap<CardAction, Integer> actions;
	private final LinkedList<CardType> types;
	private final int cost;
	private final String name;
	private final BufferedImage image;

	public SerializedCard(LinkedHashMap<CardAction, Integer> actions, LinkedList<CardType> types, int cost, String name,
			BufferedImage image) {
		this.actions = actions;
		this.types = types;
		this.cost = cost;
		this.name = name;
		this.image = image;
	}

	public LinkedHashMap<CardAction, Integer> getActions() {
		return this.actions;
	}

	public LinkedList<CardType> getTypes() {
		return this.types;
	}

	public int getCost() {
		return this.cost;
	}

	public String getName() {
		return this.name;
	}

	public BufferedImage getImage() {
		return this.image;
	}

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
		for (int cnt = 0; cnt < length; cnt++) {
			arr = new byte[buff.getInt()];
			buff.get(arr);
			this.actions.put(CardAction.valueOf(new String(arr)), buff.getInt());
		}
	}

	public byte[] getBytes() throws IOException {

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
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
		for (Entry<CardAction, Integer> entr : this.actions.entrySet()) {
			buff.putInt(entr.getKey().name().length());
			buff.put(entr.getKey().name().getBytes());
			buff.putInt(entr.getValue());
		}
		return buff.array();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SerializedCard) {
			return this.getName().equals(((SerializedCard) other).getName());
		}
		return false;
	}

	/** needed for testing */
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

	@Override
	public String toString() {
		String res = "--" + this.getClass().getSimpleName() + "--\nName: " + this.name + "\nPrice:" + this.cost
				+ "\nTypes:\n";
		for (CardType ct : this.types) {
			res += "- " + ct.name() + "\n";
		}
		res += "Actions:\n";
		for (Entry<CardAction, Integer> entr : this.actions.entrySet()) {
			res += "- " + entr.getKey().name() + " - " + entr.getValue();
		}
		res += "\n-- --";

		return res;
	}
}
