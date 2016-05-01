package com.tpps.application.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.tpps.application.storage.CardStorageController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.chat.client.ChatClient;
import com.tpps.technicalServices.network.game.ClientGamePacketHandler;
import com.tpps.technicalServices.network.game.GameClient;
import com.tpps.technicalServices.network.matchmaking.client.Matchmaker;
import com.tpps.ui.MainFrame;
import com.tpps.ui.MainMenuPanel;
import com.tpps.ui.cardeditor.CardEditor;
import com.tpps.ui.lobbyscreen.GlobalChatPanel;
import com.tpps.ui.lobbyscreen.PlayerSettingsPanel;
import com.tpps.ui.loginscreen.LoginGUIController;
import com.tpps.ui.statisticsscreen.StatisticsBoard;

/**
 * main controller class containing main entry point for client-application
 * 
 * @author Steffen Jacobs, Johannes Huhn
 */
public final class DominionController {

	private static DominionController INSTANCE;
	private UUID lobbyID;

	private String username;
	private UUID sessionID;
	private GameClient gameClient;
	private Matchmaker matchmaker;
	private CardStorageController storageController;
	private MainFrame mainFrame;
	private LoginGUIController loginGuiController;

	private MainMenuPanel mainMenuPanel;
	private GlobalChatPanel globalChatPanel;
	private PlayerSettingsPanel playerSettingsPanel;
	private StatisticsBoard statisticsBoardPanel;

	private BufferedImage originalBackground;
	/**
	 * the background-image
	 */
	public static BufferedImage selectedGameImage;
	private boolean turnFlag;
	private boolean isHost;
	private ChatClient chatClient;
	
	@SuppressWarnings("unused")
	private CardEditor cardEditor;

	private Semaphore waitForSession = new Semaphore(1);
	

	/** main entry point for client application 
	 * @param stuff */
	public static void main(String[] stuff) {
		new DominionController();
	}

	/**
	 * trivial
	 */
	private DominionController() {
		DominionController.INSTANCE = this;
		DominionController.INSTANCE.init();
	}

	/**
	 * This method is called, when the user starts the .jar File, important
	 * components (e.g to handle the Login) will be initialized
	 * 
	 * @author jhuhn
	 */
	private void init() {
		boolean login = true;
		if (login) {
			storageController = new CardStorageController();
			storageController.loadCards();
			mainFrame = new MainFrame();
			loginGuiController = new LoginGUIController();
		} else {
			this.turnFlag = false;
			try {
				gameClient = new GameClient(new InetSocketAddress(Addresses.getRemoteAddress(), 1339),
						new ClientGamePacketHandler());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method is responsible to load all GUI components that are needed
	 * for: - MainMenu - CommunityBoard - Lobby
	 * 
	 * @author jhuhn
	 */
	private void loadPanels() {
		mainMenuPanel = new MainMenuPanel(this.mainFrame);
		globalChatPanel = new GlobalChatPanel();
		statisticsBoardPanel = new StatisticsBoard();
		playerSettingsPanel = new PlayerSettingsPanel().updateCards();
		try {
			this.originalBackground = ImageIO
					.read(ClassLoader.getSystemResource("resources/img/loginScreen/LoginBackground.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This Method starts the Match
	 * 
	 * @author jhuhn
	 * @param port
	 *            The Port that is needed to connect to the GameServer
	 */
	public void startMatch(int port) {
		try {
			System.out.println("in DominionController: start Match");
			selectedGameImage = this.playerSettingsPanel.getSelectedPicture();
			System.out.println("FIRST: " + selectedGameImage);
			gameClient = new GameClient(new InetSocketAddress(Addresses.getRemoteAddress(), port),
					new ClientGamePacketHandler());
			this.clearAllPlayersFromGUI();
			this.joinMainMenu();
			this.mainFrame.setVisible(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * is called when the match ends
	 */
	public void finishMatch() {
		this.playerSettingsPanel.initStandardBackground();
		this.joinMainMenu();
	}

	/**
	 * This method starts the to search a lobby. It is called, when the user
	 * joins the lobby gui
	 * 
	 * @author jhuhn
	 */
	public void findMatch() {
		try {
			this.matchmaker.findMatch(this.username, this.sessionID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * stops searching a match
	 * 
	 * @author jhuhn
	 */
	public void abortSearching() {
		try {
			this.matchmaker.abort(this.username, this.sessionID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is called to set the statistics data on the gui
	 * 
	 * @author jhuhn
	 * @param statistics
	 *            a twodimensional Array that is filled with all statistics from
	 *            the database
	 */
	public void loadStatisticsToGui(String[][] statistics) {
		this.statisticsBoardPanel.setTableData(statistics);
	}

	/**
	 * This method is called to send a packet to the LoginServer (like a push
	 * notification) to receive all statistics
	 * 
	 * @author jhuhn
	 */
	public void sendPacketToGetStatistics() {
		this.loginGuiController.getLoginclient().sendPacketForAllStatistics();
	}

	/**
	 * This method initializes client instances
	 * 
	 * @author jhuhn
	 */
	private void initClients() {
		this.chatClient = new ChatClient(this.username);
		this.matchmaker = Matchmaker.getInstance();
		GameLog.log(MsgType.INFO, "Username: " + this.username);
	}

	/**
	 * @return the instance of the matchmaker
	 */
	public Matchmaker getMatchmaker() {
		return this.matchmaker;
	}

	/**
	 * @author jhuhn
	 * @param message
	 *            String representation of the chat message to send
	 */
	public void sendChatMessage(String message) {
		this.chatClient.sendMessage(message);
	}

	/**
	 * @author jhuhn
	 * @param message
	 *            String representation of the chat message to send
	 * @param user
	 *            String representation of the user who sent the message
	 * @param timeStamp
	 *            String representation of the timestamp
	 * @param color
	 *            color of the user
	 */
	public void receiveChatMessageFromChatServer(String message, String user, String timeStamp, Color color) {
		if (this.gameClient == null) { // player is not ingame, player is in
										// globalchat
			this.globalChatPanel.appendChatLocal(message, user, timeStamp, color);
		} else { // player is ingame
			this.gameClient.getGameWindow().getChatWindow().appendChatLocal(message, user, timeStamp, color);
		}
	}

	/**
	 * this method initializes clients and ui components and loads the main menu
	 * 
	 * @author jhuhn
	 */
	public void endLogin() {

		storageController.checkStandardCardsAsync();

		this.loadPanels();
		this.initClients();

		this.joinMainMenu();
	}

	/**
	 * opens the main-menu
	 */
	public void joinMainMenu() {
		mainFrame.setPanel(mainMenuPanel);
		mainFrame.setVisible(true);
	}

	/**
	 * @return whether it is your turn
	 */
	public boolean isTurnFlag() {
		return turnFlag;
	}

	
	/**
	 * @param turnFlag set whether it is your turn
	 */
	public void setTurnFlag(boolean turnFlag) {
		this.turnFlag = turnFlag;
	}

	/**
	 * @param name the new username
	 */
	public void setUsername(String name) {
		this.username = name;
	}

	/**
	 * @author jhuhn
	 * @return the selected image instance of thelobby
	 */
	public BufferedImage getLobbyBackground() {
		return this.playerSettingsPanel.getSelectedPicture();
	}

	/**
	 * 
	 * @return the card-storage controller
	 */
	public CardStorageController getCardRegistry() {
		return storageController;
	}

	/**
	 * opens the LobbyGui
	 * 
	 * @author jhuhn
	 */
	public void joinLobbyGui() {
		this.globalChatPanel.getBackButton().setLobby(true);
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				h.drawImage(originalBackground, 0, 0, this.getWidth(), this.getHeight(), null);
				super.paint(h);
			}
		};
		panel.setLayout(new GridLayout(1, 2));
		panel.setVisible(true);
		panel.setOpaque(false);
		panel.add(this.globalChatPanel);
		panel.add(this.playerSettingsPanel.updateCards());
		// this.playerSettingsPanel.setStatisticsBoardPanel(this.statisticsBoardPanel);
		this.mainFrame.setPanel(panel);
	}

	/**
	 * @return the main application-frame
	 */
	public MainFrame getMainFrame() {
		return mainFrame;
	}

	/**
	 * @author jhuhn
	 * @param player
	 */
	public synchronized void insertPlayerToGUI(String player) {
		this.playerSettingsPanel.insertPlayer(player);
	}

	/**
	 * @author jhuhn
	 * @param player
	 *            String representation of the user who left the lobby
	 */
	public synchronized void clearPlayerFromGUI(String player) {
		this.playerSettingsPanel.removePlayer(player);
	}

	/**
	 * cleares all players in the lobby
	 * 
	 * @author jhuhn
	 */
	public synchronized void clearAllPlayersFromGUI() {
		this.playerSettingsPanel.clearAllPlayers();
	}

	/* getters and setters */
	/**
	 * @param sessionID
	 *            new session-id
	 */
	public void setSessionID(UUID sessionID) {
		this.sessionID = sessionID;
		waitForSession.release();
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
	 * @return the current Session-ID
	 */
	public UUID getSessionID() {
		if (sessionID == null)
			try {
				waitForSession.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return sessionID;
	}

	/**
	 * @return the current instance of the game
	 */
	public static DominionController getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DominionController();
		return INSTANCE;
	}

	/**
	 * opens the community gui
	 */
	public void openStatisticsGui() {
		this.globalChatPanel.getBackButton().setLobby(false);
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				h.drawImage(originalBackground, 0, 0, this.getWidth(), this.getHeight(), null);
				super.paint(h);
			}
		};
		panel.setLayout(new GridLayout(1, 2));
		panel.setVisible(true);
		panel.setOpaque(false);
		panel.add(this.globalChatPanel);
		panel.add(this.statisticsBoardPanel);
		this.mainFrame.setPanel(panel);
	}

	/**
	 * opens the cardeditor
	 * 
	 * @author jhuhn
	 */
	public void openCardeditor() {
		this.mainFrame.setVisible(false);
		this.cardEditor = new CardEditor();
	}

	/**
	 * @return the id of the lobby the player is in
	 */
	public UUID getLobbyID() {
		return lobbyID;
	}

	/**
	 * sets the lobby the player is in
	 * @param lobbyID the Id of the lobby the player is in
	 */
	public void setLobbyID(UUID lobbyID) {
		this.lobbyID = lobbyID;
	}
	
	/**
	 * @return the login-GUI-controller
	 */
	public LoginGUIController getLoginGuiController() {
		return loginGuiController;
	}
	
	public boolean isHost() {
		return isHost;
	}

	public void setHost(boolean isHost) {
		if(isHost){
			playerSettingsPanel.enableOrDisableEverything(true);
		}else{
			playerSettingsPanel.enableOrDisableEverything(false);
		}
		this.isHost = isHost;
		System.out.println("AM I a host ? " + isHost);
	}
	
}