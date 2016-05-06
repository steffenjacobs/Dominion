package com.tpps.technicalServices.network.game;

import java.io.IOException;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.events.NetworkListener;
import com.tpps.technicalServices.network.gameSession.packets.PacketReconnect;

public class GameClientNetworkListener implements NetworkListener {
	private GameClient gameClient;

	public GameClientNetworkListener(GameClient gameClient) {
		this.gameClient = gameClient;
	}
	
	@Override
	public void onClientConnect(int port) {
		if (this.gameClient.getClientId() > -1) {
			try {
				this.gameClient.sendMessage(new PacketReconnect(DominionController.getInstance().getSessionID(),
						DominionController.getInstance().getUsername()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClientDisconnect(int port) {
		GameLog.log(MsgType.NETWORK_INFO ,"gameClient network listener " );
		DominionController.getInstance().setTurnFlag(false);	
		DominionController.getInstance().getGameClient().getGameWindow().setCaptionTurn("D");
	}

}
