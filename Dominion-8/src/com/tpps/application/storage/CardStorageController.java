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

/**
 * This class represents a card-storage
 * 
 * @author Steffen Jacobs
 */
public final class CardStorageController {

	private static ConcurrentHashMap<String, SerializedCard> storedCards = new ConcurrentHashMap<String, SerializedCard>();
	private static final String STORAGE_FILE = "cards.bin";

	/**
	 * loads all cards from file
	 */
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

	/**
	 * saves all stored cards to file
	 */
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

	/**
	 * returns a card by the name (if exists) from the storage
	 * 
	 * @param name
	 *            the name of the card
	 * @return the requested card or null
	 */
	public static SerializedCard getCard(String name) {
		return storedCards.get(name);
	}

	/**
	 * adds a card to the storage
	 * 
	 * @param card
	 *            the card to add to the storage. Note: The card will be
	 *            packaged internally into a different class-type. Note 2: adds
	 *            the card to the file after calling save().
	 */
	public static void addCard(Card card) {
		storedCards.put(card.getName(), new SerializedCard(card.getActions(), card.getTypes(), card.getCost(),
				card.getName(), (BufferedImage) card.getImage()));
	}

	/**
	 * adds a card to the storage
	 * 
	 * @param card
	 *            the card to add to the storage. Note: This Card-Object will be
	 *            directly added to the storage. Note 2: Adds the Card to the
	 *            file after calling save().
	 */
	public static void addCard(SerializedCard card) {
		storedCards.put(card.getName(), card);
	}

	/**
	 * removes a card from the storage Note: Removes the card from the file
	 * after calling save().
	 * 
	 * @param card
	 *            the card to be removed
	 */
	public static SerializedCard removeCard(SerializedCard card) {
		return storedCards.remove(card.getName());
	}

	/**
	 * removes a card from the storage. Note: Removes the card from the file
	 * after calling save().
	 * 
	 * @param card
	 *            the name of the card to be removed
	 */
	public static SerializedCard removeCard(String cardName) {
		return storedCards.remove(cardName);
	}

	/**
	 * removes all cards from the storage. Note: Removes the cards from the file
	 * after calling save().
	 */
	public static void clearCards() {
		storedCards.clear();
	}
}