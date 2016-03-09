package com.tpps.ui.gameplay;

import java.awt.Image;
import java.io.IOException;

import com.tpps.application.game.DominionController;
import com.tpps.application.network.gameSession.packets.PacketEndActionPhase;
import com.tpps.application.network.gameSession.packets.PacketEndTurn;
import com.tpps.application.network.gameSession.packets.PacketPlayTreasures;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.components.GFButton;

public class ButtonClass extends GFButton {
	private static final long serialVersionUID = 1520424079770080041L;
	private GameWindow gameWindow;

	public ButtonClass(double relativeX, double relativeY, double relativeWidth, double relativeHeight,
			int absWidth, int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, String caption) {
		super(relativeX, relativeY, relativeWidth, relativeHeight, absWidth, absHeight, _layer, sourceImage,
				_parent, caption);
		this.gameWindow = gameWindow;
	}

	@Override
	public GameObject clone() {
		return null;// return new TestButton(super.get);
	}

	@Override
	public void onMouseEnter() {

	}

	@Override
	public void onMouseExit() {
		if (this.getCaption().equals("End ActionPhase")){
			this.updateImage(GraphicsUtil.setAlpha(this.getImage(), 0.6f));
		}

	}

	@Override
	public void onMouseClick() {

		
		if (this.getCaption().equals("")) {
			System.exit(0);
		}
		if (this.getCaption().equals("End ActionPhase")){
			try {
				System.out.println("EndActionPhase");
				this.getParent().removeComponent(this);
				this.getParent().addComponent(GameWindow.playTreasures);
				DominionController.getInstance().getGameClient().sendMessage(new PacketEndActionPhase());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (this.getCaption().equals("Play Treasures")){
			try {
				System.out.println("PacketPlayTreasures");
//				this.getParent().removeC
				DominionController.getInstance().getGameClient().sendMessage(new PacketPlayTreasures());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Caption: " + this.getCaption());
		if (this.getCaption().equals("End Turn")){
			try {
				System.out.println("Packet EndTurn");
				DominionController.getInstance().getGameClient().sendMessage(new PacketEndTurn());
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
	}

	@Override
	public void onMouseDrag() {
		System.out.println("dragged " + this.toString());

	}

	@Override
	public String toString() {
		return "@" + System.identityHashCode(this) + " - " + super.getLocation() + " , " + super.getDimension()
				+ " , " + super.getLayer() + " , " + super.getImage() + " , " + super.getParent() + " , "
				+ super.getCaption();
	}

	@Override
	public void onResize(int absWidth, int absHeight) {
		super.onResize(absWidth, absHeight);
	}

}