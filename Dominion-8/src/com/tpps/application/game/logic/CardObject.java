package com.tpps.application.game.logic;

import java.awt.Image;

import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */

public class CardObject extends GameObject {
	
	private static final long serialVersionUID = 1L;
	private final Actions[] actions;

	/**
	 * sets the actions array containing the actions which the cardObject will execute
	 */
	public CardObject(Actions[] actions, int locX, int locY, int _layer, Image sourceImage, GraphicFramework _parent) {
		super(locX, locY, _layer, sourceImage,_parent);
		this.actions = actions;
	}

	/**
	 * calls the static method which executes the actions*W
	 * 
	 * @author ladler - Lukas Adler
	 */
	public void doAction() {
		for (int i = 0; i < actions.length; i++) {

			switch (actions[i]) {
			case ADD_ACTION_TO_PLAYER:
				// call
				break;
			case ADD_PURCHASE:
				// call
				break;
			case DRAW_CARDS:
				// call
				break;
			case DISCARD:
				// call
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				// call
				break;
			case TRASH:
				// call
				break;

			}
		}
	}

	@Override
	public GameObject clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onMouseEnter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseExit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseDrag() {
		// TODO Auto-generated method stub
		
	}
}
