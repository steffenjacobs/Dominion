package com.tpps.application.game;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mysql.fabric.Server;
import com.tpps.application.storage.CardStorageController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.card.CardClient;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.game.ClientGamePacketHandler;
import com.tpps.technicalServices.network.game.GameClient;
import com.tpps.ui.MainFrame;
import com.tpps.ui.MainMenuPanel;
import com.tpps.ui.lobbyscreen.GlobalChatPanel;
import com.tpps.ui.lobbyscreen.PlayerSettingsPanel;
import com.tpps.ui.loginscreen.LoginGUIController;
import com.tpps.ui.statisticsscreen.StatisticsBoard;

/**
 * main controller class containing main entry point for client-application
 * 
 * @author Steffen Jacobs
 */
public final class DominionController {

	private static DominionController instance;

	private String username, email;
	private UUID sessionID;
	private SessionClient sessionClient;
	private GameClient gameClient;
	private CardClient cardClient;
	private CardStorageController storageController;
	private MainFrame mainFrame;
	private LoginGUIController loginGuiController;
	
	private MainMenuPanel mainMenuPanel;
	private GlobalChatPanel globalChatPanel;
	private PlayerSettingsPanel playerSettingsPanel;
	private StatisticsBoard statisticsBoardPanel;

	private BufferedImage originalBackground;
	
	/** main entry point for client application */
	public static void main(String[] stuff) {
		instance = new DominionController();
		DominionController.instance.init();
	}

	

	/* constructor */
	public DominionController() {
		
	}
	
	
	private void init() {
		storageController = new CardStorageController();
		mainFrame = new MainFrame();
		loginGuiController = new LoginGUIController();
		this.loadPanels();
			
		try {
			gameClient = new GameClient(new InetSocketAddress(Addresses.getRemoteAddress(), 1339), new ClientGamePacketHandler());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadPanels(){
		mainMenuPanel = new MainMenuPanel(this.mainFrame);
		globalChatPanel = new GlobalChatPanel();
		playerSettingsPanel = new PlayerSettingsPanel();
		statisticsBoardPanel = new StatisticsBoard();
		
		try {
			this.originalBackground = ImageIO.read(ClassLoader.getSystemResource("resources/img/loginScreen/LoginBackground.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void endLogin(){
		mainFrame.setPanel(mainMenuPanel);
		mainFrame.setVisible(true);
	}
	
	public void joinMainMenu(){
		mainFrame.setPanel(mainMenuPanel);
	}

	/**
	 * 
	 * @return
	 */
	public CardStorageController getStorageController() {
		return storageController;
	}

	/**
	 * 
	 * @return
	 */
	public CardStorageController getCardRegistry() {
		return storageController;
	}
	
	/**
	 * opens the LobbyGui 
	 */
	public void joinLobbyGui() {
		JPanel panel = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(originalBackground, 0, 0, null);
				super.paint(g);
			}
		};
		panel.setLayout(new GridLayout(1, 2));
		panel.setVisible(true);
		panel.setOpaque(false);
		panel.add(this.globalChatPanel);
		panel.add(this.playerSettingsPanel);
		this.mainFrame.setPanel(panel);
	}



	/**
	 * 
	 * @param test
	 */
	public DominionController(boolean test) {
		storageController = new CardStorageController();
		// do nothing else, just init object
	}

	/**
	 * sets the session-client instance and starts keep-alive 
	 */
	public void setSessionClient(SessionClient sc) {
		if (this.sessionClient != null) {
			this.sessionClient.keepAlive(username, false);
			this.sessionClient.disconnect();
		}
		this.sessionClient = sc;
		this.sessionClient.keepAlive(username, true);
	}

	/* getters and setters */
	/**
	 * @param sessionID new session-id
	 */
	public void setSessionID(UUID sessionID) {
		this.sessionID = sessionID;
	}

	/** 
	 * @return the users username 
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the GameClientObject
	 */
	public GameClient getGameClient() {
		return gameClient;
	}

	/**
	 * @return the users email-address 
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the current Session-ID
	 */
	public UUID getSessionID() {
		return sessionID;
	}

	/**
	 * @return the current instance of the game 
	 */
	public static DominionController getInstance() {
		return instance;
	}

	/**
	 * @param username new Username
	 * @param mailAddress new Email-Address
	 */
	public void setCredentials(String username, String mailAddress) {
		this.username = username;
		this.email = mailAddress;
	}



	public void openStatisticsGui() {
		JPanel panel = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(originalBackground, 0, 0, null);
				super.paint(g);
			}
		};
		panel.setLayout(new GridLayout(1, 2));
		panel.setVisible(true);
		panel.setOpaque(false);
		panel.add(this.globalChatPanel);
		panel.add(this.statisticsBoardPanel);
		this.mainFrame.setPanel(panel);		
	}
}