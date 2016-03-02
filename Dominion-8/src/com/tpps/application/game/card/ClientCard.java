package com.tpps.application.game.card;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.tpps.ui.GraphicFramework;

/**
 * 
 * @author ladler - Lukas Adler
 * @author nwipfler - Nicolas Wipfler
 */

public class ClientCard extends Card {

	public ClientCard(LinkedHashMap<CardAction, Integer> actions,
			LinkedList<CardType> types, String name, int cost,
			GraphicFramework _parent) {
		super(actions, types, name, cost, _parent);
	}

	private static final long serialVersionUID = 6557548927740502052L;
	
}