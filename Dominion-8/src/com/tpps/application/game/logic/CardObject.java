package com.tpps.application.game.logic;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

/**
 * 
 * @author ladler - Lukas Adler
 * @author nwipfler - Nicolas Wipfler
 */

public class CardObject extends GameObject {

	private static final long serialVersionUID = 1L;
	private final ArrayList<Action> actions;
	private final ArrayList<Type> types;
	private final int cost;
	private final String name;

	/**
	 * sets the actions array containing the actions which the cardObject will
	 * execute
	 */
	public CardObject(ArrayList<Action> actions, ArrayList<Type> types,
			String name, int costs, double relativeLocX, double relativeLocY,
			double relativeWidth, double relativeHeight, int absWidth,
			int absHeight, int _layer, Image sourceImage,
			GraphicFramework _parent, int _id) {
		super(relativeLocX, relativeLocY, relativeWidth, relativeHeight,
				absWidth, absHeight, _layer, sourceImage, _parent, _id);
		this.name = name;
		this.actions = actions;
		this.cost = costs;
		this.types = types;
	}

	/**
	 * calls the static method which executes the actions
	 * 
	 * @author ladler - Lukas Adler
	 */
	public void doAction() {

		for (int i = 0; i < actions.size(); i++) {
			switch (actions.get(i)) {
			case ADD_ACTION_TO_PLAYER:
				// call
				break;
			case ADD_PURCHASE:
				// call
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				// call
				break;
			case DRAW:
				// call
				break;
			case GAIN:
				// call
				break;
			case DISCARD:
				// call
				break;
			case TRASH:
				// call
				break;
			case PUT_BACK:
				// call
				break;
			case REVEAL:
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

	@Override
	public void onResize(int absWidth, int absHeight) {
		// TODO Auto-generated method stub
	}

	/**
	 * @override toString()
	 */
	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		sBuf.append("CardObject: " + "'" + this.name + "'"/*
														 * \nsuper.toString():\n-
														 * " + super.toString()
														 */);
		sBuf.append("\nCost: " + this.cost);
		sBuf.append("\nAction(s):");
		for (Action act : actions) {
			sBuf.append("\n- " + act.toString());
		}
		sBuf.append("\nType(s):");
		for (Type type : types) {
			sBuf.append("\n- " + type.toString());
		}
		return sBuf.toString();
	}

	/**
	 * main method with test case for cardObject
	 */
	public static void main(String[] args) {
		ArrayList<Action> act = new ArrayList<Action>();
		act.add(Action.ADD_ACTION_TO_PLAYER);
		act.add(Action.ADD_PURCHASE);
		act.add(Action.ADD_TEMPORARY_MONEY_FOR_TURN);
		act.add(Action.DRAW);
		ArrayList<Type> type = new ArrayList<Type>();
		type.add(Type.ACTION);
		CardObject card = new CardObject(act, type, "Market", 5, 300, 300, 300,
				300, 300, 300, 1, new BufferedImage(1, 1, 2),
				new GraphicFramework(new JFrame("Hallo")), 10);
		System.out.println(card.toString());
	}
}
