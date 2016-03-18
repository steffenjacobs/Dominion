package com.tpps.application.game;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.game.card.Tuple;
import com.tpps.application.network.game.GameServer;
import com.tpps.application.network.game.SynchronisationException;
import com.tpps.application.network.gameSession.packets.PacketDisable;
import com.tpps.application.network.gameSession.packets.PacketDiscardDeck;
import com.tpps.application.network.gameSession.packets.PacketEndDiscardMode;
import com.tpps.application.network.gameSession.packets.PacketEndTrashMode;
import com.tpps.application.network.gameSession.packets.PacketStartDiscardMode;
import com.tpps.application.network.gameSession.packets.PacketStartTrashMode;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/**
 * @author Nicolas Wipfler
 */
public class Player {

	private Deck deck;

	private final int id;
	private static int playerID = 0;

	private final int CLIENT_ID;
	private int port;

	private int actions;
	private int buys;
	private int coins;
	private int gainValue;
	private boolean discardMode, trashMode, reactionMode, reactionCard, gainMode;
	private Tuple<CardAction> discardOrTrashAction;
	private LinkedList<Card> playedCards, discardList;

	/**
	 * @param deck
	 * @param clientID
	 * @param port
	 */
	public Player(Deck deck, int clientID, int port) {
		this.reactionCard = false;
		this.discardMode = false;
		this.trashMode = false;
		this.reactionMode = false;
		this.gainMode = false;
		this.discardList = new LinkedList<Card>();
		this.deck = deck;
		this.id = playerID++;
		this.actions = GameConstant.INIT_ACTIONS;
		this.buys = GameConstant.INIT_PURCHASES;
		this.coins = GameConstant.INIT_TREASURES;
		this.CLIENT_ID = clientID;
		this.port = port;
		this.playedCards = new LinkedList<Card>();
	}

	/**
	 * @param clientID
	 * @param port
	 * @param initCards
	 */
	public Player(int clientID, int port, LinkedList<Card> initCards) {
		this(new Deck(initCards), clientID, port);
	}

	/**
	 * 
	 */
	public synchronized void resetPlayerValues() {
		this.coins = 0;
		this.buys = 1;
		this.actions = 1;
	}

	/**
	 * @return the deck
	 */
	public Deck getDeck() {
		return deck;
	}

	public void setDiscardMode() {
		this.discardMode = true;
	}

	public void setDiscardOrTrashAction(CardAction cardAction, int val) {
		this.discardOrTrashAction = new Tuple<CardAction>(cardAction, val);
	}

	public void setReactionCard(boolean reactionCard) {
		this.reactionCard = reactionCard;
	}

	/**
	 * 
	 * @return if the player has a reactionCard or not
	 */
	public boolean playsReactionCard() {
		return this.reactionCard;
	}

	/**
	 * @return if the discardMode is set or not
	 */
	public boolean getDiscardMode() {
		return this.discardMode;
	}

	/**
	 * 
	 * @return if the reactionMode is set or not
	 */
	public boolean isReactionMode() {
		return reactionMode;
	}

	/**
	 * 
	 * @return if GainMode is set or not
	 */
	public boolean isGainMode() {
		return gainMode;
	}

	public int getGainValue() {
		return gainValue;
	}

	/**
	 * @return if the trashMode is set or not
	 */
	public boolean getTrashMode() {
		return this.trashMode;
	}

	/**
	 * @param deck
	 *            the deck to set
	 */
	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	/**
	 * @return the id
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return the CLIENT_ID
	 */
	public int getClientID() {
		return CLIENT_ID;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the actions
	 */
	public int getActions() {
		return actions;
	}

	/**
	 * @return the buys
	 */
	public int getBuys() {
		return buys;
	}

	/**
	 * @return the played cards of the player in his turn
	 */
	public LinkedList<Card> getPlayedCards() {
		return playedCards;
	}

	/**
	 * new played cardsList
	 */
	public void refreshPlayedCardsList() {
		this.playedCards = new LinkedList<Card>();
	}

	/**
	 * @param buys
	 *            the buys to set
	 */
	public void setBuys(int buys) {
		this.buys = buys;
	}

	/**
	 * @return the coins
	 */
	public int getCoins() {
		return coins;
	}

	/**
	 * @param coins
	 *            the coins to set
	 */
	public void setCoins(int coins) {
		this.coins = coins;
	}

	/**
	 * 
	 */
	public void setReactionMode() {
		this.reactionMode = true;
	}

	/**
	 * 
	 * @param cardID
	 * @param trashPile
	 * @throws IOException
	 */
	public void discardOrTrash(String cardID, LinkedList<Card> trashPile) throws IOException {
		System.out.println("DiscardMode is set= " + this.discardMode);
		System.out.println("TrashMode is set ? " + this.trashMode);
		if (this.discardMode) {
			this.getDeck().getDiscardPile().add(doAction(cardID));
			return;
		}
		if (this.trashMode) {
			trashPile.add(doAction(cardID));
			return;
		}
	}

	/**
	 * 
	 */
	public void endDiscardAndDrawMode() {
		CollectionsUtil.appendListToList(discardList, this.getDeck().getCardHand());
		this.discardMode = false;
		discardList = new LinkedList<Card>();
	}

	/**
	 * 
	 */
	public void endTrashMode() {
		this.trashMode = false;
	}

	/**
	 * 
	 * @param cardID
	 * @throws IOException
	 */
	public void playCard(String cardID) throws IOException {
		System.out.println("kein discard mode oder trash mode");
		Card card = doAction(cardID);
		if (card != null) {
			this.playedCards.addLast(card);
		}

	}

	/**
	 * 
	 * @throws IOException
	 */
	public void playTreasures() throws IOException {
		LinkedList<Card> cards = new LinkedList<Card>();
		LinkedList<String> treasureCards = this.getDeck().getTreasureCardsFromHand();
		for (Iterator<String> iterator = treasureCards.iterator(); iterator.hasNext();) {
			String cardId = (String) iterator.next();
			cards.add(doAction(cardId));
			System.out.println("Treasures auf der Hand: " + cardId);
		}
		// this.activePlayer.getDeck().
		// // CollectionsUtil.appendListToList(treasureCards,
		// this.getPlayedCards());
		CollectionsUtil.appendListToList(cards, this.playedCards);
	}

	/**
	 * 
	 * @return the relevant actions in this round e.g. by an Action Reaction
	 *         card return the actions belonging to the actual gameMode
	 */
	public LinkedList<CardAction> getRelevantCardActions(LinkedList<CardAction> cardActions) {
		LinkedList<CardAction> relevantCardActions = new LinkedList<CardAction>();
		if (this.reactionMode) {
			for (int i = cardActions.size() - 1; i > 0; i--) {

				if (cardActions.get(i).equals(CardAction.SEPERATOR)) {
					break;
				}
				relevantCardActions.add(cardActions.get(i));
			}
		} else if (!this.reactionMode) {
			for (int i = 0; i < cardActions.size(); i++) {

				if (cardActions.get(i).equals(CardAction.SEPERATOR)) {
					break;
				}
				relevantCardActions.add(cardActions.get(i));
			}
		}
		return relevantCardActions;
	}

	/**
	 * calls the static method which executes the actions
	 * 
	 * @author Lukas Adler
	 * @throws IOException
	 */
	public Card doAction(String cardID) throws IOException {
		boolean dontRemoveFlag = false, trashFlag = false;
		Card serverCard = this.getDeck().getCardFromHand(cardID);
		if (serverCard == null) {
			try {
				throw new SynchronisationException();
			} catch (SynchronisationException e) {
				e.printStackTrace();
			}
		}
		System.out.println("coins: " + coins + "buys: " + buys + "actions: " + actions);
		if (!reactionCard && (this.discardMode || this.trashMode)) {
			discardOrTrash(serverCard);
			return serverCard;
		}
		
		LinkedList<CardAction> cardActions = new LinkedList<CardAction>(serverCard.getActions().keySet());
		if (serverCard.getTypes().contains(CardType.REACTION)) {
			cardActions = getRelevantCardActions(cardActions);
		}

		System.out.println(Arrays.toString(cardActions.toArray()));

		Iterator<CardAction> cardIterator = cardActions.iterator();
		if (!this.reactionMode && serverCard.getTypes().contains(CardType.ACTION)) {
			this.actions--;
		}
		System.out.println("DoAction");
		while (cardIterator.hasNext()) {
			CardAction act = cardIterator.next();
			String value = serverCard.getActions().get(act);
			switch (act) {
			case ADD_ACTION_TO_PLAYER:
				this.actions += Integer.parseInt(value);
				break;
			case ADD_PURCHASE:
				this.buys += Integer.parseInt(value);
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				this.coins += Integer.parseInt(value);
				break;
			case DRAW_CARD:
				this.getDeck().draw(Integer.parseInt(value));
				break;
			case GAIN_CARD:

				/* <------- ! fehlt ----> */
				this.gainMode = true;
				try {
					this.gainValue = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				System.out.println(value);
				// maybe neede
				// switch (value.toUpperCase()) {
				// case "CURSE":
				// break;
				// case "SILVER":
				// break;
				// case "":
				// break;
				// default:
				// break;
				// }
				break;
			case DISCARD_CARD:
				if (value.toLowerCase().equals("deck")) {
					GameServer.getInstance().sendMessage(port, new PacketDiscardDeck());
				}
				break;
			case DISCARD_AND_DRAW:
				this.discardMode = true;
				this.discardOrTrashAction = new Tuple<CardAction>(act, Integer.parseInt(value));
				((GameServer) (GameServer.getInstance())).sendMessage(port, new PacketStartDiscardMode());
				break;
			case DISCARD_OTHER_DOWNTO:
				GameServer.getInstance().getGameController().discardOtherDownto(value);
				break;
			case TRASH_CARD:
			
				if (value.equals("this")) {
//			trashFlag gesetzt karte trashen null zurückgeben		
					trashFlag = true;
					
				} else {

					System.out.println((GameServer.getInstance()));
					((GameServer) (GameServer.getInstance())).sendMessage(port, new PacketStartTrashMode());

					this.trashMode = true;
					this.discardOrTrashAction = new Tuple<CardAction>(act, Integer.parseInt(value));
				}
				// return?
				break;
			case PUT_BACK:
				this.getDeck().putBack(serverCard);
				break;
			case REVEAL_CARD:
				System.out.println("REVEAL: " + serverCard.getActions().get(CardAction.REVEAL_CARD));
				break;
			case IS_TREASURE:
				this.coins += Integer.parseInt(serverCard.getActions().get(CardAction.IS_TREASURE));
				break;
			case IS_VICTORY:
				// what?
				break;
			case DEFEND:

				break;
			default:
				break;
			}
		}

		 
		if (!dontRemoveFlag) {
			System.out.println("card was removed");
			this.getDeck().getCardHand().remove(serverCard);
		}
		if (this.reactionMode) {
			System.out.println("coins: " + coins + "buys: " + buys + "actions: " + actions);
			setModesFalse();
			GameServer.getInstance().sendMessage(port, new PacketDisable());
			GameServer.getInstance().getGameController().checkReactionModeFinishedAndEnableGuis();
		}
		if (trashFlag){
			trashFlag = false;
			GameServer.getInstance().getGameController().getGameBoard().getTrashPile().add(serverCard);
			return null;
		}
		
		return serverCard;
	}

	protected void setGainModeFalse() {
		this.gainMode = false;
		this.gainValue = -1;
	}

	/**
	 * sets all flags set for the reactionMode on false
	 */
	private void setModesFalse() {
		this.discardMode = false;
		this.trashMode = false;
		this.reactionMode = false;
		this.reactionCard = false;
		this.gainMode = false;
	}

	/**
	 * 
	 * @param card
	 * @throws IOException
	 */
	public void discardOrTrash(Card card) throws IOException {
		switch (this.discardOrTrashAction.getFirstEntry()) {
		case DISCARD_AND_DRAW:
			if (this.discardOrTrashAction.getSecondEntry() == -1) {
				System.out.println("Discard and Draw");
				this.getDeck().getCardHand().remove(card);
				discardList.add(this.getDeck().removeSaveFromDiscardPile());
				LinkedList<Card> cardHand = this.getDeck().getCardHand();
				if (cardHand.size() == 0) {
					endDiscardAndDrawMode();
					((GameServer) (GameServer.getInstance())).sendMessage(port, new PacketEndDiscardMode());
				}
			}
			break;
		case DISCARD_CARD:
			if (this.discardOrTrashAction.getSecondEntry() > 0) {
				this.discardOrTrashAction.decrementSecondEntry();
			}

			this.getDeck().getCardHand().remove(card);
			if (this.discardOrTrashAction.getSecondEntry() == 0) {
				this.discardMode = false;
				if (this.reactionMode) {
					setModesFalse();
					GameServer.getInstance().sendMessage(port, new PacketDisable());
					GameServer.getInstance().getGameController().checkReactionModeFinishedAndEnableGuis();
				}
			}
			break;
		case TRASH_CARD:
			LinkedList<Card> cardHand = this.getDeck().getCardHand();
			if (cardHand.size() == 0) {
				this.trashMode = false;
				((GameServer) (GameServer.getInstance())).sendMessage(port, new PacketEndTrashMode());
			}
			this.getDeck().getCardHand().remove(card);
			System.out.println("card added to trashPile" + card);
			this.discardOrTrashAction.decrementSecondEntry();
			if (this.discardOrTrashAction.getSecondEntry() == 0) {
				this.trashMode = false;
			}
			break;
		default:
			break;
		}
	}
}