package com.tpps.technicalServices.util;

import java.util.ArrayList;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;

public final class CollectionsUtil {

	public static ArrayList<CardAction> arrayListAction(CardAction... actions) {
		ArrayList<CardAction> resultList = new ArrayList<CardAction>();
		for (CardAction action : actions) {
			resultList.add(action);
		}
		return resultList;
	}

	public static ArrayList<CardType> arrayListType(CardType... types) {
		ArrayList<CardType> resultList = new ArrayList<CardType>();
		for (CardType type : types) {
			resultList.add(type);
		}
		return resultList;
	}
}
