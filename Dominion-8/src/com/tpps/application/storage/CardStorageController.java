package com.tpps.application.storage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
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
public class CardStorageController {

	private ConcurrentHashMap<String, SerializedCard> storedCards = new ConcurrentHashMap<String, SerializedCard>();
	private static final String STORAGE_FILE = "cards.bin";
	private String storageFile;

	public CardStorageController() {
		this.storageFile = STORAGE_FILE;
	}

	public CardStorageController(String filename) {
		this.storageFile = filename;
	}

	/**
	 * loads all cards from file
	 */
	public void loadCards() {
		try {
			if (!Files.exists(Paths.get(storageFile)))
				Files.createFile(Paths.get(storageFile));

			byte[] bytes = Files.readAllBytes(Paths.get(storageFile));
			if (bytes.length == 0) {
				System.err.println("ERROR: Storage-Container is empty!");
				Files.copy(Paths.get(storageFile), Paths.get(storageFile + "_old_" + System.currentTimeMillis()));
				return;
			}
			System.out.println("File length: " + bytes.length);
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
		} catch (BufferUnderflowException | IOException e) {
			System.err.println("ERROR: Storage-Container is broken!");
			e.printStackTrace();
		}

	}

	/**
	 * saves all stored cards to file
	 */
	public void saveCards() {
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

			Files.write(Paths.get(storageFile), bytes.toByteArray());
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
	public SerializedCard getCard(String name) {
		return storedCards.get(name);
	}

	/**
	 * adds a card to the storage
	 * 
	 * @param card
	 *            the card to add to the storage. Note: The card will be
	 *            packaged internally into a different class-type. Note 2: adds
	 *            the card to the file after calling save(). Note 3: If a card
	 *            with the same name already exists, the card will not be
	 *            overwritten.
	 */
	public void addCard(Card card) {
		storedCards.putIfAbsent(card.getName(), new SerializedCard(card.getActions(), card.getTypes(), card.getCost(),
				card.getName(), (BufferedImage) card.getImage()));
	}

	/**
	 * adds a card to the storage
	 * 
	 * @param card
	 *            the card to add to the storage. Note: This Card-Object will be
	 *            directly added to the storage. Note 2: Adds the Card to the
	 *            file after calling save(). Note 3: If a card with the same
	 *            name already exists, the card will not be overwritten.
	 */
	public void addCard(SerializedCard card) {
		storedCards.putIfAbsent(card.getName(), card);
	}

	/**
	 * removes a card from the storage Note: Removes the card from the file
	 * after calling save().
	 * 
	 * @param card
	 *            the card to be removed
	 */
	public SerializedCard removeCard(SerializedCard card) {
		return storedCards.remove(card.getName());
	}

	/**
	 * removes a card from the storage. Note: Removes the card from the file
	 * after calling save().
	 * 
	 * @param card
	 *            the name of the card to be removed
	 */
	public SerializedCard removeCard(String cardName) {
		return storedCards.remove(cardName);
	}

	/**
	 * removes all cards from the storage. Note: Removes the cards from the file
	 * after calling save().
	 */
	public void clearCards() {
		storedCards.clear();
	}

	/**
	 * checks if a card with a given name already exists
	 * 
	 * @param cardName
	 *            the given name to be checked
	 * @return whether the given card existed or not
	 */
	public boolean hasCard(String cardName) {
		return storedCards.containsKey(cardName);
	}

	/** lists all stored cards in the console */
	public void listCards() {
		System.out.println("--- Cards in storage (" + storedCards.size() + "): ---");
		for (SerializedCard card : storedCards.values()) {
			System.out.println(card.toString());
		}
		System.out.println("---       (" + storedCards.size() + ")         ---");
	}

	/**
	 * getter for the number of stored cards
	 * 
	 * @return the number of stored cards
	 */
	public int getCardCount() {
		return this.storedCards.size();
	}
}