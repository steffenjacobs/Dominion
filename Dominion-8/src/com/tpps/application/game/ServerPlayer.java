package com.tpps.application.game;

import java.util.ArrayList;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.ServerCard;

/**
 * 
 * */

public class ServerPlayer extends Player {

	public ServerPlayer() {
		this.deck = new Deck();
		this.cardHandSize = 5;
		// this.id = GameController.getPlayerID();
		// this.port = ;
	}

	public ServerPlayer(Deck deck, int id) {
		this.deck = deck;
		// this.id = GameController.getPlayerID();
	}
	
	/**
	 * calls the static method which executes the actions
	 * 
	 * @author ladler - Lukas Adler
	 */
	public void doAction(String cardId) {
		ServerCard serverCard = null;/*this.getDeck().getCard(cardId)*/;
		ArrayList<CardAction> actionsList = new ArrayList<CardAction>(serverCard
				.getActions().keySet());
		
		// Player player = GameController.getActivePlayer();
		for (int i = 0; i < actionsList.size(); i++) {
			switch (actionsList.get(i)) {
			case ADD_ACTION_TO_PLAYER:
				// player.addAction(this.actions.get(ADD_ACTION_TO_PLAYER));
				// sentPackage(player, numbAction);
				System.out.println("ADD_ACTION_TO_PLAYER: "
						+ serverCard.getActions().get(
								CardAction.ADD_ACTION_TO_PLAYER));
				break;
			case ADD_PURCHASE:
				System.out.println("ADD_PURCHASE: "
						+ serverCard.getActions().get(CardAction.ADD_PURCHASE));
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				System.out.println("ADD_TEMPORARY_MONEY_FOR_TURN: "
						+ serverCard.getActions().get(
								CardAction.ADD_TEMPORARY_MONEY_FOR_TURN));
				break;
			case DRAW:
				System.out.println("DRAW: "
						+ serverCard.getActions().get(CardAction.DRAW));
				break;
			case GAIN:
				System.out.println("GAIN: "
						+ serverCard.getActions().get(CardAction.GAIN));
				break;
			case DISCARD:
				System.out.println("DISCARD: "
						+ serverCard.getActions().get(CardAction.DISCARD));
				break;
			case TRASH:
				System.out.println("TRASH: "
						+ serverCard.getActions().get(CardAction.TRASH));
				break;
			case PUT_BACK:
				System.out.println("PUT_BACK: "
						+ serverCard.getActions().get(CardAction.PUT_BACK));
				break;
			case REVEAL:
				System.out.println("REVEAL: "
						+ serverCard.getActions().get(CardAction.REVEAL));
				break;
			case NONE:
				System.out.println("NONE: "
						+ serverCard.getActions().get(CardAction.NONE));
				break;
			case COUNT_FOR_VICTORY:
				System.out.println("COUNT_VOR_VICTORY: "
						+ serverCard.getActions().get(CardAction.COUNT_FOR_VICTORY));
				break;
			default:
				// call
				break;
			}
		}
	}

	
	
	
}
