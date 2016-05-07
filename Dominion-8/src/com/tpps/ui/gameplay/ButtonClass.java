package com.tpps.ui.gameplay;

import java.awt.Image;
import java.io.IOException;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.game.GameClient;
import com.tpps.technicalServices.network.gameSession.packets.PacketDiscardDeck;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndActionPhase;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndDiscardMode;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndReactions;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndTrashMode;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndTurn;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayTreasures;
import com.tpps.technicalServices.network.gameSession.packets.PacketPutBackCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketPutBackThiefCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketSetAsideDrewCard;
import com.tpps.technicalServices.network.gameSession.packets.PacketTakeCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketTakeDrewCard;
import com.tpps.technicalServices.network.gameSession.packets.PacketTakeThiefCards;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.MyAudioPlayer;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.components.GFButton;

/**
 * GameGui Button Class
 * 
 * @author Nishit Agrawal - nagrawal
 *
 */

public class ButtonClass extends GFButton {
	private static final long serialVersionUID = 1520424079770080041L;
	private Image original;
	private String parameter="";

	/**
	 * constructor calling the GraphicFramework and drawing the buttons
	 * 
	 * @param relativeX
	 * @param relativeY
	 * @param relativeWidth
	 * @param relativeHeight
	 * @param absWidth
	 * @param absHeight
	 * @param _layer
	 * @param sourceImage
	 * @param _parent
	 * @param caption
	 */
	public ButtonClass(double relativeX, double relativeY, double relativeWidth, double relativeHeight, int absWidth,
			int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, String caption) {
		super(relativeX, relativeY, relativeWidth, relativeHeight, absWidth, absHeight, _layer, sourceImage, _parent,
				caption);
		this.original = super.getBufferedImage();
		super.updatedBufferedImage(GraphicsUtil.setAlpha(super.getBufferedImage(), .6f));
	}

	public ButtonClass(double relativeX, double relativeY, double relativeWidth, double relativeHeight, int absWidth,
			int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, String caption, String parameter) {
		super(relativeX, relativeY, relativeWidth, relativeHeight, absWidth, absHeight, _layer, sourceImage, _parent,
				caption);
		this.parameter = parameter;
		this.original = super.getBufferedImage();
		super.updatedBufferedImage(GraphicsUtil.setAlpha(super.getBufferedImage(), .6f));
	}

	@Override
	public GameObject clone() {
		return null;// return new TestButton(super.get);
	}

	/**
	 * setting back the original image.
	 */

	public void onMouseEnter() {
		super.updatedBufferedImage(original);
	}

	/**
	 * alpha animation added.
	 */

	public void onMouseExit() {
		super.updatedBufferedImage(GraphicsUtil.setAlpha(super.getBufferedImage(), .6f));
	}

	/**
	 * Event handling when button is clicked for the specefic type of button.
	 * 
	 */

	public void onMouseClick() {
		if (parameter.equals("play")) {
			GameWindow.getInstance().getGraphicFramework().removeComponent(this);
			MyAudioPlayer.handleGameMusic(false);
			GameWindow.getInstance().getGraphicFramework().addComponent(GameWindow.getInstance().getMuteButton());
			return;
		}
		if (parameter.equals("mute")) {
			GameWindow.getInstance().getGraphicFramework().removeComponent(this);
			MyAudioPlayer.handleGameMusic(true);
			GameWindow.getInstance().getGraphicFramework().addComponent(GameWindow.getInstance().getPlayButton());
			return;
		}
		if (DominionController.getInstance().isTurnFlag()) {
			if (parameter.equals("exit")) {
				System.exit(0);
			}
			DominionController.getInstance().setTurnFlag(false);
			DominionController.getInstance().getGameClient().getGameWindow().setCaptionTurn("execute");

			if (this.getCaption().equals("End ActionPhase")) {
				try {
					GameLog.log(MsgType.GUI ,"EndActionPhase");
					this.getFramework().removeComponent(this);
					this.getFramework().addComponent(GameWindow.playTreasures);
					DominionController.getInstance().getGameClient().sendMessage(new PacketEndActionPhase());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Play Treasures")) {
				try {
					GameLog.log(MsgType.GUI ,"PacketPlayTreasures");
					this.getFramework().removeComponent(this);
					DominionController.getInstance().getGameClient().sendMessage(new PacketPlayTreasures());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			GameLog.log(MsgType.GUI ,"Caption: " + this.getCaption());
			if (this.getCaption().equals("End Turn")) {
				try {
					this.getFramework().addComponent(GameWindow.endActionPhase);
					GameLog.log(MsgType.GUI ,"Packet EndTurn");
					DominionController.getInstance().getGameClient().sendMessage(new PacketEndTurn());
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			if (this.getCaption().equals("Stop Discard")) {
				try {
					GameLog.log(MsgType.GUI ,"packet send end discard mode");
					DominionController.getInstance().getGameClient().sendMessage(new PacketEndDiscardMode());
					this.getFramework().removeComponent(this);
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Stop Trash")) {
				try {
					DominionController.getInstance().getGameClient().sendMessage(new PacketEndTrashMode());
					this.getFramework().removeComponent(this);
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Discard Deck")) {
				try {
					GameLog.log(MsgType.GUI ,"PacketDiscardDeck");
					DominionController.getInstance().getGameClient().sendMessage(new PacketDiscardDeck());
					this.getFramework().removeComponent(this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("End Reactions")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketEndReactions(gameClient.getClientId()));
					this.getFramework().removeComponent(this);
					this.getFramework().addComponent(GameWindow.endActionPhase);
					this.getFramework().addComponent(GameWindow.endTurn);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Take Cards")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketTakeCards(gameClient.getClientId()));
					this.getFramework().removeComponent(this);
					this.getFramework().removeComponent(GameWindow.putBack);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Put Back")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketPutBackCards(gameClient.getClientId()));
					this.getFramework().removeComponent(this);
					this.getFramework().removeComponent(GameWindow.takeCards);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Take Thief Cards")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketTakeThiefCards());
					this.getFramework().removeComponent(GameWindow.takeThiefCards);
					this.getFramework().removeComponent(GameWindow.putBackThiefCards);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Put Back Thief Cards")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketPutBackThiefCards());
					this.getFramework().removeComponent(GameWindow.putBackThiefCards);
					this.getFramework().removeComponent(GameWindow.takeThiefCards);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Take Drawed Card")) {
				GameLog.log(MsgType.GUI ,"take drewCard");
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketTakeDrewCard());
					this.getFramework().removeComponent(GameWindow.takeDrawedCard);
					this.getFramework().removeComponent(GameWindow.setAsideDrawedCard);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (this.getCaption().equals("Set Aside Drawed Card")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketSetAsideDrewCard());
					this.getFramework().removeComponent(GameWindow.takeDrawedCard);
					this.getFramework().removeComponent(GameWindow.setAsideDrawedCard);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * checking
	 */
	public void onMouseDrag() {
		if (DominionController.getInstance().isTurnFlag()) {
			if (this.getCaption().equals("")) {
				System.exit(0);
			}
			if (this.getCaption().equals("End ActionPhase")) {
				try {
					GameLog.log(MsgType.GUI ,"EndActionPhase");
					this.getFramework().removeComponent(this);
					this.getFramework().addComponent(GameWindow.playTreasures);
					DominionController.getInstance().getGameClient().sendMessage(new PacketEndActionPhase());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (this.getCaption().equals("Play Treasures")) {
				try {
					GameLog.log(MsgType.GUI ,"PacketPlayTreasures");
					this.getFramework().removeComponent(this);
					DominionController.getInstance().getGameClient().sendMessage(new PacketPlayTreasures());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			GameLog.log(MsgType.GUI ,"Caption: " + this.getCaption());
			if (this.getCaption().equals("End Turn")) {
				try {
					this.getFramework().addComponent(GameWindow.endActionPhase);
					GameLog.log(MsgType.GUI ,"Packet EndTurn");
					DominionController.getInstance().getGameClient().sendMessage(new PacketEndTurn());
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			if (this.getCaption().equals("Stop Discard")) {
				try {
					GameLog.log(MsgType.GUI ,"packet send end discard mode");
					DominionController.getInstance().getGameClient().sendMessage(new PacketEndDiscardMode());
					this.getFramework().removeComponent(this);
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Stop Trash")) {
				try {
					DominionController.getInstance().getGameClient().sendMessage(new PacketEndTrashMode());
					this.getFramework().removeComponent(this);
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Discard Deck")) {
				try {
					GameLog.log(MsgType.GUI ,"PacketDiscardDeck");
					DominionController.getInstance().getGameClient().sendMessage(new PacketDiscardDeck());
					this.getFramework().removeComponent(this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("End Reactions")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketEndReactions(gameClient.getClientId()));
					this.getFramework().removeComponent(this);
					this.getFramework().addComponent(GameWindow.endActionPhase);
					this.getFramework().addComponent(GameWindow.endTurn);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Take Cards")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketTakeCards(gameClient.getClientId()));
					this.getFramework().removeComponent(this);
					this.getFramework().removeComponent(GameWindow.putBack);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (this.getCaption().equals("Put Back")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketPutBackCards(gameClient.getClientId()));
					this.getFramework().removeComponent(this);
					this.getFramework().removeComponent(GameWindow.takeCards);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Take Thief Cards")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketTakeThiefCards());
					this.getFramework().removeComponent(GameWindow.takeThiefCards);
					this.getFramework().removeComponent(GameWindow.putBackThiefCards);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Put Back Thief Cards")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketPutBackThiefCards());
					this.getFramework().removeComponent(GameWindow.putBackThiefCards);
					this.getFramework().removeComponent(GameWindow.takeThiefCards);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.getCaption().equals("Take Drawed Card")) {
				GameLog.log(MsgType.GUI ,"take drewCard");
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketTakeDrewCard());
					this.getFramework().removeComponent(GameWindow.takeDrawedCard);
					this.getFramework().removeComponent(GameWindow.setAsideDrawedCard);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (this.getCaption().equals("Set Aside Drawed Card")) {
				GameClient gameClient = DominionController.getInstance().getGameClient();
				try {
					gameClient.sendMessage(new PacketSetAsideDrewCard());
					this.getFramework().removeComponent(GameWindow.takeDrawedCard);
					this.getFramework().removeComponent(GameWindow.setAsideDrawedCard);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * checking buttons reaction.
	 */

	public String toString() {
		return "@" + System.identityHashCode(this) + " - " + super.getLocation() + " , " + super.getDimension() + " , "
				+ super.getLayer() + " , " + super.getRenderdImage() + " , " + super.getFramework() + " , "
				+ super.getCaption();
	}

	@Override
	public void onResize(int absWidth, int absHeight) {
		super.onResize(absWidth, absHeight);
	}

}