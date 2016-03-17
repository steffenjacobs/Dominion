package com.tpps.ui.gameplay;

import java.awt.Image;
import java.io.IOException;

import com.tpps.application.game.DominionController;
import com.tpps.application.network.game.GameClient;
import com.tpps.application.network.gameSession.packets.PacketDiscardDeck;
import com.tpps.application.network.gameSession.packets.PacketEndActionPhase;
import com.tpps.application.network.gameSession.packets.PacketEndDiscardMode;
import com.tpps.application.network.gameSession.packets.PacketEndReactions;
import com.tpps.application.network.gameSession.packets.PacketEndTrashMode;
import com.tpps.application.network.gameSession.packets.PacketEndTurn;
import com.tpps.application.network.gameSession.packets.PacketPlayTreasures;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.components.GFButton;

public class ButtonClass extends GFButton {
	private static final long serialVersionUID = 1520424079770080041L;
	private Image original;

	public ButtonClass(double relativeX, double relativeY, double relativeWidth, double relativeHeight, int absWidth,
			int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, String caption) {
		super(relativeX, relativeY, relativeWidth, relativeHeight, absWidth, absHeight, _layer, sourceImage, _parent,
				caption);
		this.original = super.getOriginalImage();
		super.updatedBufferedImage(GraphicsUtil.setAlpha(super.getOriginalImage(), .6f));
	}

	@Override
	public GameObject clone() {
		return null;// return new TestButton(super.get);
	}

	@Override
	public void onMouseEnter() {
		super.updatedBufferedImage(original);

	}

	@Override
	public void onMouseExit() {
		super.updatedBufferedImage(GraphicsUtil.setAlpha(super.getOriginalImage(), .6f));

	}

	@Override
	public void onMouseClick() {

		if (this.getCaption().equals("")) {
			System.exit(0);
		}
		if (this.getCaption().equals("End ActionPhase")) {
			try {
				System.out.println("EndActionPhase");
				this.getParent().removeComponent(this);
				this.getParent().addComponent(GameWindow.playTreasures);
				DominionController.getInstance().getGameClient().sendMessage(new PacketEndActionPhase());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (this.getCaption().equals("Play Treasures")) {
			try {
				System.out.println("PacketPlayTreasures");
				this.getParent().removeComponent(this);
				DominionController.getInstance().getGameClient().sendMessage(new PacketPlayTreasures());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Caption: " + this.getCaption());
		if (this.getCaption().equals("End Turn")) {
			try {
				this.getParent().addComponent(GameWindow.endActionPhase);
				System.out.println("Packet EndTurn");
				DominionController.getInstance().getGameClient().sendMessage(new PacketEndTurn());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		if (this.getCaption().equals("Stop Discard")) {
			try {
				System.out.println("packet send end discard mode");
				DominionController.getInstance().getGameClient().sendMessage(new PacketEndDiscardMode());
				this.getParent().removeComponent(this);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		if (this.getCaption().equals("Stop Trash")) {
			try {
				DominionController.getInstance().getGameClient().sendMessage(new PacketEndTrashMode());
				this.getParent().removeComponent(this);
			} catch (IOException e) {
				
				e.printStackTrace();
			}			
		}
		if (this.getCaption().equals("Discard Deck")){
			try {
				System.out.println("PacketDiscardDeck");
				DominionController.getInstance().getGameClient().sendMessage(new PacketDiscardDeck());
				this.getParent().removeComponent(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (this.getCaption().equals("End Reactions")){
			GameClient gameClient = DominionController.getInstance().getGameClient();
			try {
				gameClient.sendMessage(new PacketEndReactions(gameClient.getClientId()));
				this.getParent().removeComponent(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onMouseDrag() {
		System.out.println("dragged " + this.toString());

	}

	@Override
	public String toString() {
		return "@" + System.identityHashCode(this) + " - " + super.getLocation() + " , " + super.getDimension() + " , "
				+ super.getLayer() + " , " + super.getImage() + " , " + super.getParent() + " , " + super.getCaption();
	}

	@Override
	public void onResize(int absWidth, int absHeight) {
		super.onResize(absWidth, absHeight);
	}

}