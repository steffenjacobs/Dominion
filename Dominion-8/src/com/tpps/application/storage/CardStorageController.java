package com.tpps.application.storage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.game.card.Card;
import com.tpps.technicalServices.util.ByteUtil;

public final class CardStorageController {
	private static ConcurrentHashMap<String, SerializedCard> storedCards = new ConcurrentHashMap<String, SerializedCard>();
	private static final String STORAGE_FILE = "cards.bin";

	public static void loadCards() {
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(STORAGE_FILE));
			ByteBuffer buff = ByteBuffer.wrap(bytes);
			int count = buff.getInt();
			int length;
			SerializedCard card;
			byte[] arr;
			for (int i = 0; i < count; i++) {
				length = buff.getInt();
				arr = new byte[length];
				buff.get(arr);
				card = new SerializedCard(arr);
				storedCards.put(card.getName(), card);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void saveCards() {
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bytes.write(ByteUtil.intToByteArray(storedCards.size()));
			byte[] arr;
			for (SerializedCard card : storedCards.values()) {
				arr = card.getBytes();
				bytes.write(ByteUtil.intToByteArray(arr.length));
				bytes.write(arr);
			}
			bytes.flush();

			Files.write(Paths.get(STORAGE_FILE), bytes.toByteArray());
			bytes.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SerializedCard getCard(String name) {
		return storedCards.get(name);
	}

	public static void addCard(Card card) {
		storedCards.put(card.getName(), new SerializedCard(card.getActions(), card.getTypes(), card.getCost(),
				card.getName(), (BufferedImage) card.getImage()));
	}

	public static void addCard(SerializedCard card) {
		storedCards.put(card.getName(), card);
	}

	public static SerializedCard removeCard(SerializedCard card) {
		return storedCards.remove(card.getName());
	}

	public static SerializedCard removeCard(String cardName) {
		return storedCards.remove(cardName);
	}

	public static void clearCards() {
		storedCards.clear();
	}
}