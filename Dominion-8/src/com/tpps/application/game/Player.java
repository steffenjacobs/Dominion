package com.tpps.application.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.game.card.Tuple;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.technicalServices.network.game.SynchronisationException;
import com.tpps.technicalServices.network.gameSession.packets.PacketDisable;
import com.tpps.technicalServices.network.gameSession.packets.PacketDiscardDeck;
import com.tpps.technicalServices.network.gameSession.packets.PacketDontShowEndReactions;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndDiscardMode;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndTrashMode;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendHandCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendRevealCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketSetAsideDrawedCard;
import com.tpps.technicalServices.network.gameSession.packets.PacketStartDiscardMode;
import com.tpps.technicalServices.network.gameSession.packets.PacketStartTrashMode;
import com.tpps.technicalServices.network.gameSession.packets.PacketTakeDrawedCard;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/**
 * @author Nicolas Wipfler, Lukas Adler
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
	private int gainValue, drawUntil;
	private CardType setAside;
	private Card drawedCard;
	private boolean discardMode, trashMode, reactionMode, reactionCard, gainMode, playTwice, revealMode, thief, witch, bureaucrat, spy,
			onHand;
	private Tuple<CardAction> discardOrTrashAction;
	private LinkedList<Card> playedCards, drawList, revealList, temporaryTrashPile, setAsideCards;

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
		this.thief = false;
		this.bureaucrat = false;
		this.witch = false;
		this.spy = false;
		this.drawList = new LinkedList<Card>();
		this.revealList = new LinkedList<Card>();
		this.temporaryTrashPile = new LinkedList<Card>();
		this.setAsideCards = new LinkedList<Card>();
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
	 * sets the player values on the initial values
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

	/**
	 * sets the discard mode on true
	 */
	public void setDiscardMode() {
		this.discardMode = true;
	}

	/**
	 * 
	 * @return if player is in thief mode or not
	 */
	public boolean isThief() {
		return thief;
	}

	/**
	 * sets the thiefMode true
	 */
	public void setThief() {
		this.thief = true;
	}

	/**
	 * sets the thiefMode false
	 */
	public void setThiefFalse() {
		this.thief = false;
	}

	/**
	 * 
	 * @return if the player is in the witch mode or not
	 */
	public boolean isWitch() {
		return witch;
	}

	/**
	 * sets the witch mode true
	 */
	public void setWitch() {
		this.witch = true;
	}

	/**
	 * sets the witch mode false
	 */
	public void setWitchFalse() {
		this.witch = false;
	}
	
	/**
	 * 
	 * @return if the player is in the bureaucrat mode or not
	 */
	public boolean isBureaucrat() {
		return this.bureaucrat;
	}
	
	/**
	 * sets the bureaucrat mode true
	 */
	public void setBureaucrat() {
		this.bureaucrat = true;
	}
	
	/**
	 * sets the bureaucrat mode false
	 */
	public void setBureaucratFalse() {
		this.bureaucrat = false;
	}
	
	/**
	 * 
	 * @return if the player is in the spy mode or not
	 */
	public boolean isSpy() {
		return this.spy;
	}
	
	/**
	 * sets the spy mode true
	 */
	public void setSpy() {
		this.spy = true;
	}
	
	/**
	 * sets the spy mode false
	 */
	public void setSpyFalse() {
		this.spy = false;
	}

	/**
	 * sets the tuple which contains the chosen discard or trash actions (listed in class CardAction) and the 
	 * value for the discard or trash action
	 * @param cardAction
	 * @param val
	 */
	public void setDiscardOrTrashAction(CardAction cardAction, int val) {
		this.discardOrTrashAction = new Tuple<CardAction>(cardAction, val);
	}

	/**
	 * sets the value if the player has a reaction card or not on the value specified through the parameter
	 * @param reactionCard
	 */
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
	public boolean isDiscardMode() {
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
	 * @return if the revealMode is set or not
	 */
	public boolean isRevealMode() {
		return revealMode;
	}

	/**
	 * 
	 * @return a list of ids of the cards which can be revealed
	 */
	public LinkedList<Card> getRevealList() {
		return revealList;
	}

	/**
	 * 
	 * @return if GainMode is set or not
	 */
	public boolean isGainMode() {
		return gainMode;
	}

	/**
	 * 
	 * @return the gainValu of the player
	 */
	public int getGainValue() {
		return gainValue;
	}

	/**
	 * @return if the trashMode is set or not
	 */
	public boolean isTrashMode() {
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

	public LinkedList<Card> getTemporaryTrashPile() {
		return this.temporaryTrashPile;
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
	 * 
	 * @return the last drawed card. be carefull is not set by every card
	 */
	public Card getDrawedCard() {
		return this.drawedCard;
	}

	/**
	 * 
	 * @return the cards which should be set aside after the action
	 */
	public LinkedList<Card> getSetAsideCards() {
		return this.setAsideCards;
	}

	/**
	 * 
	 * @return if the gained card should be put on the hand after the action (if on hand = true)
	 */
	public boolean isOnHand() {
		return onHand;
	}

	/**
	 * set on hand false if the gained card shall be put on the discard pile and not on the hand
	 */
	public void setOnHandFalse() {
		this.onHand = false;
	}

	/**
	 * @param coins
	 *            the coins to set
	 */
	public void setCoins(int coins) {
		this.coins = coins;
	}

	/**
	 * set reveal mode true if he has to reveal cards
	 */
	public void setRevealMode() {
		this.revealMode = true;
	}

	/**
	 * sets the reaction mode for the player
	 */
	public void setReactionMode() {
		this.reactionMode = true;
	}

	/**
	 * sets the reaction mode false
	 */
	public void setReactionModeFalse() {
		this.reactionMode = false;
	}

	/**
	 * resets the thief mode triggered through the card thief. thief, revealMode, reactionMode, reactionCard 
	 * are set on false. a new revealList is created.
	 */
	public void resetThiefMode() {
		this.thief = false;
		this.reactionCard = false;
		this.reactionMode = false;
		this.revealMode = false;
		this.revealList = new LinkedList<Card>();
	}

	/**
	 * takes the revealed card from the revealList append the list to the discard pile. sets the reveal mode false
	 * and creates a new reveal list
	 */
	public void takeRevealedCardsSetRevealModeFalse() {
		CollectionsUtil.appendListToList(revealList, getDeck().getDiscardPile());
		this.revealMode = false;
		revealList = new LinkedList<Card>();
	}

	/**
	 * appends the revealed cards to the draw pile. sets the reveal mode false. creates a new reveal list
	 */
	public void putBackRevealedCardsSetRevealModeFalse() {
		CollectionsUtil.appendListToList(revealList, getDeck().getDrawPile());
		this.revealMode = false;
		revealList = new LinkedList<Card>();
	}

	/**
	 * called by the game controller if player is in discard or trash mode. decides in which mode the player is
	 * and appends the cards to the right pile. calls the do action method for the card. 
	 * @param cardID
	 * @param trashPile
	 * @throws IOException
	 */
	public void discardOrTrash(String cardID, LinkedList<Card> trashPile) throws IOException {
		
		if (this.discardMode) {
			this.getDeck().getDiscardPile().add(doAction(cardID));
			return;
		}
		if (this.trashMode) {
			if (this.discardOrTrashAction.getFirstEntry().equals(CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND)
					&& !this.getDeck().getCardFromHand(cardID).getTypes().contains(CardType.TREASURE)) {
				return;
			}
			trashPile.add(doAction(cardID));
			return;

		}
	}

	/**
	 * discard mode is set on false drawList is append to the hand
	 */
	public void endDiscardAndDrawMode() {
		CollectionsUtil.appendListToList(drawList, this.getDeck().getCardHand());
		this.discardMode = false;
		drawList = new LinkedList<Card>();
	}

	/**
	 * sets trash mode on false
	 */
	public void endTrashMode() {
		this.trashMode = false;
	}

	/**
	 * called whenever a card is played calls the doAction method appends the played card to the played card list
	 * sets the play twice flag if a card should be played twice
	 * @param cardID
	 * @throws IOException
	 */
	public void playCard(String cardID) throws IOException {
		Card card = doAction(cardID);
		if (card != null) {
			this.playedCards.addLast(card);
			if (playTwice) {
				playTwice = false;
				doAction(cardID);
			}
		}

	}

	/**
	 * plays all treasures at once
	 * @throws IOException
	 */
	public void playTreasures() throws IOException {
		LinkedList<Card> cards = new LinkedList<Card>();
		LinkedList<String> treasureCards = this.getDeck().getTreasureCardsFromHand();
		for (Iterator<String> iterator = treasureCards.iterator(); iterator.hasNext();) {
			String cardId = (String) iterator.next();
			cards.add(doAction(cardId));
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
	 * most important method for the card action. every method which executes card actions is called 
	 * from this method
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
		if (!reactionCard && (this.discardMode || this.trashMode)) {
			discardOrTrash(serverCard);
			return serverCard;
		}

		LinkedList<CardAction> cardActions = new LinkedList<CardAction>(serverCard.getActions().keySet());
		if (serverCard.getTypes().contains(CardType.REACTION)) {
			cardActions = getRelevantCardActions(cardActions);
		}

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
			case DRAW_CARD_UNTIL:
				String[] values = value.split("_");
				if (values.length == 2) {
					this.drawUntil = Integer.parseInt(values[0]);
					dontRemoveFlag = true;
					this.getDeck().getCardHand().remove(serverCard);
					if (values[1].toLowerCase().equals("action")) {
						this.setAside = CardType.ACTION;
						drawUntil();
					}
				}
				break;
			case DRAW_CARD_OTHERS:
				GameServer.getInstance().getGameController().drawOthers();
				break;
			case GAIN_CARD:

				/* <------- ! fehlt ----> */
				this.gainMode = true;
				try {
					this.gainValue = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

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
			case GAIN_CARD_OTHERS:
				if (value.toLowerCase().equals("curse")) {
					GameServer.getInstance().getGameController().gainCurseOthers();
				}
				break;
			case GAIN_CARD_DRAW_PILE:
				try {
					if (value.toLowerCase().equals("silver")) {
						getDeck().getDrawPile().addLast(GameServer.getInstance().getGameController().getGameBoard()
								.getTableForTreasureCards().get("Silver").removeLast());
					}
				} catch (NoSuchElementException e) {
					System.out.println("no more silver card on the board");
				}
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
			case ALL_REVEAL_CARDS_TRASH_COINS_I_CAN_TAKE_DISCARD_OTHERS:
				GameServer.getInstance().getGameController().revealAndTakeCardsDiscardOthers();
				break;
			case REVEAL_UNTIL_TREASURES:
				revealUntilTreasures(Integer.parseInt(value));
				break;
			case REVEAL_CARD_OTHERS_PUT_IT_ON_TOP_OF_DECK:
				GameServer.getInstance().getGameController().revealCardOthersPutItOnTopOfDeck();
				break;
			case TRASH_CARD:

				if (value.equals("this")) {
					// trashFlag gesetzt karte trashen null zur�ckgeben
					trashFlag = true;

				} else {

					((GameServer) (GameServer.getInstance())).sendMessage(port, new PacketStartTrashMode());

					this.trashMode = true;
					this.discardOrTrashAction = new Tuple<CardAction>(act, Integer.parseInt(value));
				}
				// return?
				break;
			case TRASH_AND_ADD_TEMPORARY_MONEY:

				if (value.split("_")[0].toLowerCase().equals("copper")) {

					Card card = getDeck().getCardByNameFromHand("Copper");
					if (card != null) {

						getDeck().getCardHand().remove(card);
						getDeck().trash(card,
								GameServer.getInstance().getGameController().getGameBoard().getTrashPile());
						this.coins += Integer.parseInt(value.split("_")[1]);
					}
				}
				break;
			case TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND:
				this.trashMode = true;
				this.discardOrTrashAction = new Tuple<CardAction>(CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND,
						Integer.parseInt(value.split("_")[0]));
				this.gainValue = Integer.parseInt(value.split("_")[1]);
				break;
			case TRASH_AND_GAIN_MORE_THAN:
				this.trashMode = true;
				this.discardOrTrashAction = new Tuple<CardAction>(CardAction.TRASH_AND_GAIN_MORE_THAN,
						Integer.parseInt(value.split("_")[0]));
				this.gainValue = Integer.parseInt(value.split("_")[1]);
				break;
			case TRASH_AND_GAIN:
				this.trashMode = true;
				this.discardOrTrashAction = new Tuple<CardAction>(CardAction.TRASH_AND_GAIN,
						Integer.parseInt(value.split("_")[0]));
				this.gainValue = Integer.parseInt(value.split("_")[1]);
				break;

			case PUT_BACK:
				this.getDeck().putBack(serverCard);
				break;
			case REVEAL_CARD:

				this.revealMode = true;
				revealList.add(getDeck().removeSaveFromDrawPile());

				GameServer.getInstance().sendMessage(port,
						new PacketSendRevealCards(CollectionsUtil.getCardIDs(revealList)));
				// GameServer.getInstance().sendMessage(port,
				// new PacketSendHandCards(revealList));
				break;
			case REVEAL_CARD_ALL:
				GameServer.getInstance().getGameController().revealCardAll();
				break;
			case CHOOSE_CARD_PLAY_TWICE:
				this.actions++;

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
			this.getDeck().getCardHand().remove(serverCard);
		}
		if (this.reactionMode) {
			GameServer.getInstance().sendMessage(port, new PacketDontShowEndReactions());
			setModesFalse();
			GameServer.getInstance().sendMessage(port, new PacketDisable());
			GameServer.getInstance().getGameController().checkReactionModeFinishedAndEnableGuis();
		}
		if (trashFlag) {
			trashFlag = false;
			GameServer.getInstance().getGameController().getGameBoard().getTrashPile().add(serverCard);
			return null;
		}

		return serverCard;
	}

	/**
	 * reveals so much cards until value treasure cards are revealed
	 * @throws noSuchElement exceptions if not enough treasure cards are in the deck
	 * @param value
	 */
	private void revealUntilTreasures(int value) {
		LinkedList<Card> treasureList = new LinkedList<Card>();
		this.revealList = new LinkedList<Card>();

		// int min = getDeck().getTreasureAmountNotOnHand() < value ?
		// getDeck().getTreasureAmountNotOnHand() : value;
		try {
			while (treasureList.size() < value) {
				Card card = this.getDeck().removeSaveFromDrawPile();
				if (card.getTypes().contains(CardType.TREASURE)) {
					treasureList.add(card);
				} else {
					this.revealList.add(card);
				}
			}
		} catch (NoSuchElementException e) {
			System.out.println("not enough treasures are in the deck");
		}
		System.out.println("hinzuf�gen");
		CollectionsUtil.appendListToList(treasureList, this.getDeck().getCardHand());
		CollectionsUtil.appendListToList(this.revealList, this.getDeck().getDiscardPile());
		this.revealList = new LinkedList<Card>();

	}

	/**
	 * draws until this.drawUntil if card contains the type which is set in this.setAside the player is asked
	 * if he wants to take the card or to set it aside
	 * @throws no such elemnt exception if there are not enough cards to draw
	 */
	public void drawUntil() {
		try {
			while (this.getDeck().getCardHand().size() < this.drawUntil) {
				System.out.println("cardHandSize: " + this.getDeck().getCardHand().size());
				Card card = this.getDeck().removeSaveFromDrawPile();
				this.drawedCard = card;
				this.getDeck().getCardHand().add(card);
				try {
					GameServer.getInstance().sendMessage(port,
							new PacketSendHandCards(CollectionsUtil.getCardIDs(this.getDeck().getCardHand())));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				if (card.getTypes().contains(this.setAside)) {
					GameServer.getInstance().getGameController().setCardsDisabled();
					try {
						GameServer.getInstance().sendMessage(port, new PacketSetAsideDrawedCard());
						GameServer.getInstance().sendMessage(port, new PacketTakeDrawedCard());
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}
		CollectionsUtil.appendListToList(this.setAsideCards, getDeck().getDiscardPile());
	}

	/**
	 * sets the gainMode false sets the gainValue on -1 which shows that there is nothing to gain
	 * because no card costs -1
	 */
	protected void setGainModeFalse() {
		this.gainMode = false;
		this.gainValue = -1;
	}

	/**
	 * sets all flags set for the reactionMode on false
	 */
	public void setModesFalse() {
		this.discardMode = false;
		this.trashMode = false;
		this.reactionMode = false;
		this.reactionCard = false;
		this.gainMode = false;
		this.revealMode = false;
		this.thief = false;
		this.spy = false;
		this.witch = false;
		this.bureaucrat = false;
	}

	/**
	 * executes the discard or trash action after this flag was set in the doAction method.
	 * action is specified in the Tuple discardOrTrashAction
	 * @param card
	 * @throws IOException
	 */
	public void discardOrTrash(Card card) throws IOException {
		switch (this.discardOrTrashAction.getFirstEntry()) {
		case DISCARD_AND_DRAW:
			if (this.discardOrTrashAction.getSecondEntry() == -1) {
				this.getDeck().getCardHand().remove(card);
				drawList.add(this.getDeck().removeSaveFromDrawPile());
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
		case TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND:
			executeTrash(card);
			break;
		case TRASH_AND_GAIN_MORE_THAN:
			executeTrash(card);
			break;
		case TRASH_AND_GAIN:
			executeTrash(card);
			break;
		case TRASH_CARD:
			executeTrash(card);
			break;
		default:
			break;
		}
	}

	/**
	 * executes the trash Action specified in the Tuple discard or TrashAction
	 * @param card
	 * @throws IOException
	 */
	private void executeTrash(Card card) throws IOException {
		LinkedList<Card> cardHand = this.getDeck().getCardHand();
		if (cardHand.size() == 0) {
			this.trashMode = false;
			((GameServer) (GameServer.getInstance())).sendMessage(port, new PacketEndTrashMode());
		}
		this.getDeck().getCardHand().remove(card);

		this.discardOrTrashAction.decrementSecondEntry();
		if (this.discardOrTrashAction.getSecondEntry() == 0) {
			this.trashMode = false;
			if (this.discardOrTrashAction.getFirstEntry().equals(CardAction.TRASH_AND_GAIN)) {
				this.gainMode = true;
			}
			if (this.discardOrTrashAction.getFirstEntry().equals(CardAction.TRASH_AND_GAIN_MORE_THAN)
					|| this.discardOrTrashAction.getFirstEntry()
							.equals(CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND)) {
				this.gainMode = true;
				this.gainValue += card.getCost();
			}
			if (this.discardOrTrashAction.getFirstEntry().equals(CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND)) {
				this.onHand = true;
			}

		}
	}

	/**
	 * the temporary trashPile is reset
	 */
	public void resetTemporaryTrashPile() {
		this.temporaryTrashPile = new LinkedList<Card>();
	}

}