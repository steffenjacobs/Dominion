package com.tpps.application.game;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.network.game.GameServer;
import com.tpps.application.network.game.SynchronisationException;
import com.tpps.application.network.game.TooMuchPlayerException;
import com.tpps.application.network.gameSession.packets.PacketEnable;
import com.tpps.application.network.gameSession.packets.PacketEnableDisable;
import com.tpps.application.network.gameSession.packets.PacketEnableOthers;
import com.tpps.application.network.gameSession.packets.PacketShowEndReactions;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/**
 * @author Lukas Adler
 * @author Nicolas Wipfler
 * 
 * 
 * 
 * 
 * 
 * Methoden nach Logik sortieren und sichtbarkeit anpassen
 */
public class GameController {

	private LinkedList<Player> players;

	private boolean gameNotFinished;
	private Player activePlayer;
	private GameBoard gameBoard;
	private String gamePhase;

	/**
	 * 
	 */
	public GameController() {
		this.players = new LinkedList<Player>();

		this.gameBoard = new GameBoard();

		this.gameNotFinished = true;
	}

	/**
	 * 
	 */
	public void setNextActivePlayer() {
		Player activePlayer = this.getActivePlayer();
		LinkedList<Player> players = this.getPlayers();
		for (int i = 0; i < GameConstant.HUMAN_PLAYERS; i++) {
			Player player = players.get(i);
			if (player.equals(activePlayer)) {
				this.setActivePlayer(players.get(i < GameConstant.HUMAN_PLAYERS - 1 ? i + 1 : 0));
				break;
			}
		}

	}

	/**
	 * 
	 */
	public void checkForVictoryCard() {

	}

	/**
	 * 
	 * @param temporaryTrashPile
	 */
	public synchronized void updateTrashPile(LinkedList<Card> temporaryTrashPile) {
		CollectionsUtil.appendListToList(temporaryTrashPile, this.gameBoard.getTrashPile());
	}

	/**
	 * 
	 * @param player
	 * @param cardID
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean checkCardExistsAndDiscardOrTrash(Player player, String cardID) throws IOException {
		Card card = player.getDeck().getCardFromHand(cardID);
		if (card != null) {
			player.discardOrTrash(cardID, this.getGameBoard().getTrashPile());
			return true;
		}
		return false;
	}

	/**
	 * checks whether a card which was clicked exists and if it is allowed to
	 * play this card in this phase of the game. If it is allowed the card is
	 * played
	 * 
	 * @param cardID
	 * @throws IOException
	 * @throws SynchronisationException
	 */
	public synchronized boolean validateTurnAndExecute(String cardID, Player player) throws IOException {
		
		Card card = player.getDeck().getCardFromHand(cardID);		
		if (card != null) {
			if (player.isReactionMode() && card.getTypes().contains(CardType.REACTION)){
				System.out.println("spielt reaktionskarte");
				player.playCard(cardID);
				return true;
			}
			if (this.gamePhase.equals("actionPhase")) {
				
				if (card.getTypes().contains(CardType.ACTION) && this.getActivePlayer().getActions() > 0) {
					this.getActivePlayer().playCard(cardID);
					if (this.getActivePlayer().getActions() == 0) {
						this.setBuyPhase();
					}
					return true;
				}
			}
			if (this.gamePhase.equals("buyPhase")) {
				if (card.getTypes().contains(CardType.TREASURE)) {
//					GameLog.log(MsgType.DEBUG, "the card is a Treasure clicked in the buyphase");
					this.getActivePlayer().playCard(cardID);
					return true;
				}
			}
		}
		return false;
	}
	
	public synchronized boolean gain(String cardID, Player player) {
		System.out.println("gain");
		try{
		LinkedList<Card> cardList = this.getGameBoard().findCardListFromBoard(cardID);
		Card card = cardList.getLast();
		if (card.getCost() <= player.getGainValue()){
			getGameBoard().findAndRemoveCardFromBoard(cardID);
			player.setGainModeFalse();
			player.getDeck().getDiscardPile().add(card);		
			return true;
		}		
		}catch(SynchronisationException e){
			e.printStackTrace();
		}
		return false;
		
	}

	/**
	 * 
	 * @param cardID
	 * @return
	 * @throws SynchronisationException
	 */
	public synchronized boolean checkBoardCardExistsAppendToDiscardPile(String cardID) throws SynchronisationException {
		System.out.println("checkBoardCardExists");
		LinkedList<Card> cards = this.getGameBoard().findCardListFromBoard(cardID);
		Card card = cards.get(cards.size() - 1);
		Player player = this.getActivePlayer();
		if (this.gamePhase.equals("buyPhase") && player.getBuys() > 0 && player.getCoins() >= card.getCost()) {
			player.setBuys(player.getBuys() - 1);
			player.setCoins(player.getCoins() - card.getCost());
			cards.removeLast();
			CollectionsUtil.addCardToList(card, player.getDeck().getDiscardPile());
			return true;
		}
		return false;
	}

	/**
	 * checks if the card according to the given cardId is a tresure card on the
	 * hand
	 * 
	 * @param cardId
	 */
	public synchronized boolean isVictoryCardOnHand(String cardId) {
		Card card = this.getActivePlayer().getDeck().getCardFromHand(cardId);
		if (card == null) {
			return false;
		} else {
			if (card.getTypes().contains(CardType.VICTORY)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * calls the play Treasures method of the player adds the returned treasure
	 * cards from the player cardHand to the playedCard list
	 * 
	 * @throws IOException
	 */
	public synchronized void playTreasures() throws IOException {
		this.getActivePlayer().playTreasures();
	}

	/**
	 * 
	 */
	public synchronized void organizePilesAndrefreshCardHand() {
		
		CollectionsUtil.appendListToList(this.getActivePlayer().getPlayedCards(), this.getActivePlayer().getDeck().getDiscardPile());
		this.getActivePlayer().getDeck().refreshCardHand();
		this.getActivePlayer().refreshPlayedCardsList();
	}

	/**
	 * 
	 * @param value
	 */
	public synchronized void discardOtherDownto(String value) {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (!player.equals(activePlayer)) {
				if (player.getDeck().cardHandContainsReactionCard()) {
					player.setReactionCard(true);
					try {
						GameServer.getInstance().sendMessage(player.getPort(), new PacketShowEndReactions());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
					player.setDiscardMode();
					player.setDiscardOrTrashAction(CardAction.DISCARD_CARD,
							player.getDeck().getCardHand().size() - Integer.parseInt(value));
				
				
				player.setReactionMode();
				 try {
					GameServer.getInstance().broadcastMessage(player.getPort(), new PacketEnableOthers(this.activePlayer.getClientID()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized void drawOthers() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (!player.equals(activePlayer)){
				player.getDeck().draw();
			}
		}
	}
	
	/**
	 * enables the gui of the active Playe disables all others if the reaction mode is finished.
	 * returns otherwise
	 */
	public synchronized void checkReactionModeFinishedAndEnableGuis(){
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.isReactionMode()){
				return;
			}
		}
		try {
			GameServer.getInstance().sendMessage(this.getActivePlayer().getPort(), new PacketEnable());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sets the nextActivePlayer, resets the PlayerValues, set the gamePhase to
	 * ActionPhase
	 */
	public synchronized void endTurn() {
		
		this.getActivePlayer().resetPlayerValues();
		this.getActivePlayer().refreshPlayedCardsList();
		this.setNextActivePlayer();
		this.setActionPhase();
	}

	/**
	 * 
	 */
	public LinkedList<Player> getPlayers() {
		return this.players;
	}
	
	/**
	 * 
	 * @param clientId
	 * @return
	 */
	public synchronized Player getClientById(int clientId){
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.getClientID() == clientId){
				return player;
			}			
		}
		return null;
	}

	/**
	 * 
	 */
	public void setPlayers(LinkedList<Player> players) {
		this.players = players;
	}

	/**
	 * 
	 */
	public synchronized Player getActivePlayer() {
		return this.activePlayer;
	}

	/**
	 * 
	 */
	public void setActivePlayer(Player aP) {
		this.activePlayer = aP;
	}

	/**
	 * 
	 */
	public boolean isGameNotFinished() {
		return this.gameNotFinished;
	}

	/**
	 * 
	 */
	public void setGameNotFinished(boolean gameNotFinished) {
		this.gameNotFinished = gameNotFinished;
	}

	/**
	 * adds one player to the gameController if four players are registered on
	 * the gameController one randomly choosen player is set as active(this
	 * player will begin the game)
	 * 
	 * @param player
	 * @throws TooMuchPlayerException
	 *             if there connects one more player
	 */
	public void addPlayer(Player player) throws TooMuchPlayerException {
		if (this.players.size() < 4) {
			this.players.addLast(player);
			if (this.players.size() == 4) {
				this.activePlayer = getRandomPlayer();
			}
		} else {
			throw new TooMuchPlayerException();
		}
	}

	/**
	 * search for the card with the given cardId on the gameBoard if the card
	 * exists add this card to the discardPile of the active player. If the card
	 * not exists throw a
	 * 
	 * @param cardId
	 * @throws SynchronisationException
	 */
	public void buyOneCard(String cardId) throws SynchronisationException {
		Card card = gameBoard.findAndRemoveCardFromBoard(cardId);
		this.getActivePlayer().getDeck().getDiscardPile().add(card);
	}

	/**
	 * @return one of the four players who is randomly choosen
	 */
	private Player getRandomPlayer() {
		return this.players.get((int) (Math.random() * 4));
	}

	/**
	 * 
	 * @return
	 */
	public synchronized GameBoard getGameBoard() {
		return gameBoard;
	}

	/**
	 * 
	 */
	// private boolean gameFinished() {
	// /* Checkt die Stapel durch, ob 3 Stapel leer sind bzw. Provinzen leer */
	// /* Wenn ja: */
	// setGameNotFinished(false);
	// return false; // überarbeiten
	// }

	/**
	 * 
	 */
	public void setDiscardPhase() {
		System.out.println("DiscardPhaseWasSet");
		this.gamePhase = "discardPhase";
	}

	/**
	 * 
	 */
	public void setActionPhase() {
		System.out.println("ActionPhaseWasSet");
		this.gamePhase = "actionPhase";
	}

	/**
	 * 
	 */
	public synchronized void setBuyPhase() {
		System.out.println("BuyPhaseWasSet");
		this.gamePhase = "buyPhase";
	}

	/**
	 * 
	 * @return
	 */
	public String getGamePhase() {
		return this.gamePhase;
	}

	/**
	 * 
	 */
	public void startGame() {
		this.gamePhase = "actionPhase";
	}

	/**
	 * 
	 */
	public void endGame() {

	}

	/**
	 * 
	 */
	// private void turn(Player player) {
	// // turn
	// setActivePlayer(player);
	// }
}
