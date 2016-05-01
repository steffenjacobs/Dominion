package com.tpps.application.storage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.game.DominionController;
import com.tpps.application.game.card.Card;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.card.CardClient;
import com.tpps.technicalServices.network.card.CardPacketHandlerClient;
import com.tpps.technicalServices.network.card.CardServer;
import com.tpps.technicalServices.util.ByteUtil;

/**
 * This class represents a card-storage
 * 
 * @author Steffen Jacobs
 */
public class CardStorageController {

	private ConcurrentHashMap<String, SerializedCard> storedCards = new ConcurrentHashMap<String, SerializedCard>();
	private static final String DEFAULT_STORAGE_FILE = "cards.bin";
	private String storageFile;
	private static final boolean DEBUG = false;
	private String[] standardCards = { "Copper", "Silver", "Gold", "Estate", "Duchy", "Province", "Cellar", "Chapel",
			"Chancellor", "Militia", "Moat", "Village", "Woodcutter", "Workshop", "Feast", "Moneylender", "Remodel",
			"Smithy", "Spy", "ThroneRoom", "CouncilRoom", "Thief", "Festival", "Laboratory", "Library", "Market",
			"Mine", "Witch", "Curse", "Adventurer", "Bureaucrat" };

	/**
	 * sets storage-file-name to default name
	 */
	public CardStorageController() {
		this.storageFile = DEFAULT_STORAGE_FILE;
		// GameLog.log(MsgType.INIT, "CardStorageController");
	}

	/**
	 * sets storage-file-name to parameter
	 * 
	 * @param filename
	 *            the name of the file to load the storage from
	 */
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
			if (DEBUG)
				GameLog.log(MsgType.INIT, "Loading storage from: " + Paths.get(storageFile));
			byte[] bytes = Files.readAllBytes(Paths.get(storageFile));
			if (bytes.length == 0) {
				GameLog.log(MsgType.INFO, "Info: Storage-Container is empty!");
				return;
			}
			if (DEBUG)
				GameLog.log(MsgType.DEBUG, "File length: " + bytes.length + " Bytes");
			ByteBuffer buff = ByteBuffer.wrap(bytes);
			int count = buff.getInt();
			SerializedCard card;
			byte[] arr;
			for (int i = 0; i < count; i++) {
				arr = new byte[buff.getInt()];
				buff.get(arr);
				card = new SerializedCard(arr);
				storedCards.put(card.getName(), card);
			}
		} catch (BufferUnderflowException | IOException e) {
			GameLog.log(MsgType.ERROR, "Storage-Container is broken!");
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
				card.getName(), (BufferedImage) card.getRenderdImage()));
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

	public void checkStandardCardsAsync() {
		new Thread(() -> {
			System.out.println("Checking standard cards...");
			boolean missing = false;

			for (String card : this.standardCards) {
				if (!this.hasCard(card)) {
					missing = true;
					break;
				}
			}
			if (missing) {
				System.out.println("Downloading missing cards...");
				this.checkAndDownloadCards(this.standardCards);
			}
			System.out.println("Check for standard cards finished.");
		}).start();
	}

	public void checkAndDownloadCards(String[] cardNames) {

		CardPacketHandlerClient cHandler = new CardPacketHandlerClient();

		CardClient client = null;
		try {
			client = new CardClient(new InetSocketAddress(Addresses.getRemoteAddress(), CardServer.getStandardPort()),
					cHandler, true, DominionController.getInstance());
			System.out.println(DominionController.getInstance().getUsername() + " - " + DominionController.getInstance().getSessionID());
			cHandler.setCardClient(client);
			Thread.sleep(1000);

			for (String name : cardNames) {
				if (!this.hasCard(name)) {
					GameLog.log(MsgType.INFO, "Started download of " + name);
					client.requestCardFromServer(name, false);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (client != null)
				client.disconnect();
		}
		this.saveCards();
	}

	/**
	 * removes a card from the storage Note: Removes the card from the file
	 * after calling save().
	 * 
	 * @param card
	 *            the card to be removed
	 * @return the card which was removed
	 */
	public SerializedCard removeCard(SerializedCard card) {
		return storedCards.remove(card.getName());
	}

	/**
	 * removes a card from the storage. Note: Removes the card from the file
	 * after calling save().
	 * 
	 * @param cardName
	 *            the name of the card to be removed
	 * @return the card which was removed by name
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
		System.out.println("--- Cards in storage (" + getCardCount() + "): ---");
		for (SerializedCard card : storedCards.values()) {
			System.out.println(card.toString());
		}
		System.out.println("---       (" + getCardCount() + ")         ---");
	}

	/**
	 * getter for the number of stored cards
	 * 
	 * @return the number of stored cards
	 */
	public int getCardCount() {
		return this.storedCards.size();
	}

	/** @return all stored cards */
	public Collection<SerializedCard> getAllCards() {
		return this.storedCards.values();
	}
}