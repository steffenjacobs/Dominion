package com.tpps.application.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import javax.swing.JPanel;

import com.tpps.application.storage.CardStorageController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.chat.client.ChatClient;
import com.tpps.technicalServices.network.chat.server.ChatServer;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.game.ClientGamePacketHandler;
import com.tpps.technicalServices.network.game.GameClient;
import com.tpps.technicalServices.network.gameSession.packets.PacketShowEndScreen;
import com.tpps.technicalServices.network.login.server.LoginServer;
import com.tpps.technicalServices.network.matchmaking.client.Matchmaker;
import com.tpps.technicalServices.network.matchmaking.server.MatchmakingServer;
import com.tpps.technicalServices.util.ImageLoader;
import com.tpps.technicalServices.util.MyAudioPlayer;
import com.tpps.technicalServices.util.NetUtil;
import com.tpps.ui.LoadingScreen;
import com.tpps.ui.MainFrame;
import com.tpps.ui.MainMenuPanel;
import com.tpps.ui.cardeditor.CardEditor;
import com.tpps.ui.endscreen.EndPanel;
import com.tpps.ui.lobbyscreen.GlobalChatPanel;
import com.tpps.ui.lobbyscreen.PlayerSettingsPanel;
import com.tpps.ui.loginscreen.LoginGUIController;
import com.tpps.ui.settings.SettingsController;
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
	private static boolean offlineMode = false;

	private GameClient gameClient;
	private Matchmaker matchmaker;
	private CardStorageController storageController;
	private MainFrame mainFrame;
	private LoginGUIController loginGuiController;

	private MainMenuPanel mainMenuPanel;
	private GlobalChatPanel globalChatPanel;
	private PlayerSettingsPanel playerSettingsPanel;
	private StatisticsBoard statisticsBoardPanel;
	private EndPanel endPanel;

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
	private Semaphore waitForLobby = new Semaphore(1);
	private LoadingScreen loadingScreen;

	/**
	 * main entry point for client application
	 * 
	 * @param stuff
	 */
	public static void main(String[] stuff) {
		new DominionController();
	}

	/** closes the loading screen and joins main-menu */
	public void closeLoadingScreen() {
		if (this.loadingScreen != null) {
			this.loadingScreen.dispose();
			this.loadingScreen = null;
			joinMainMenu();
		}
	}

	/**
	 * updates the subtext of a loading-screen
	 * 
	 * @param subtext
	 *            the new subtext
	 */
	public void updateLoadingScrenSubtext(String subtext) {
		if (this.loadingScreen != null) {
			this.loadingScreen.setSubText(subtext);
		}
	}

	/**
	 * shows a new loading screen
	 * 
	 * @param message
	 *            the message to show
	 */
	public void showLoadingScreen(String message) {
		if (this.loadingScreen == null) {
			this.loadingScreen = new LoadingScreen(message);
		}
	}

	/**
	 * trivial
	 */
	private DominionController() {
		offlineMode = !NetUtil.isNetworkReachable(Addresses.getRemoteAddress());
		if (offlineMode) {
			Addresses.setRemoteHost(Addresses.getLocalHost());

			// start session-server
			new Thread(() -> {
				try {
					new SessionServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();

			// start matchmaking-server
			new Thread(() -> {
				try {
					new MatchmakingServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();

			// start login-server
			new Thread(() -> {
				try {
					new LoginServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();

			// start chat-server
			new Thread(() -> {
				try {
					new ChatServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();

		}
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
		storageController = new CardStorageController();
		storageController.loadCards();
		mainFrame = new MainFrame();
		loginGuiController = new LoginGUIController();
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
		this.originalBackground = ImageLoader.getImage("resources/img/loginScreen/LoginBackground.jpg");
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
			selectedGameImage = this.playerSettingsPanel.getSelectedPicture();

			GameLog.log(MsgType.INFO, "Starting " + (isOffline() ? "Offline" : "Online") + " Match.");

			this.gameClient = new GameClient(new InetSocketAddress(Addresses.getRemoteAddress(), port),
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
	 * 
	 * @param packetShowEndScreen
	 */
	public void finishMatch(PacketShowEndScreen packetShowEndScreen) {
		this.gameClient.getGameWindow().dispose();
		GameLog.log(MsgType.GUI, "GameWindow disposed");
		this.gameClient = null;
		this.playerSettingsPanel.initStandardBackground();
		this.endPanel = new EndPanel(packetShowEndScreen);
		GameLog.log(MsgType.GUI, "Endpanel initialized");
		this.mainFrame.setPanel(this.endPanel);
		this.mainFrame.setVisible(true);
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

	public void showSettings() {
		SettingsController.showSettingsWindow(getCenter(SettingsController.SETTINGS_WINDOW_SIZE.width,
				SettingsController.SETTINGS_WINDOW_SIZE.height));
	}

	private Point getCenter(int width, int height) {
		return new Point(
				(int) (getMainFrame().getLocation().getX() + getMainFrame().getSize().getWidth() / 2 - width / 2),
				(int) (getMainFrame().getLocation().getY() + getMainFrame().getSize().getHeight() / 2 - height / 2));
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
		this.statisticsBoardPanel.setStatisticsData(statistics);
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
	 * calls the receiveChatMessageFromChatServer method with default value true
	 * 
	 * for JavaDocumentation of this method see
	 * {receiveChatMessageFromChatServer(String, String, String, Color,
	 * boolean)}
	 * 
	 * @param message
	 * @param user
	 * @param timeStamp
	 * @param color
	 */
	public void receiveChatMessageFromChatServer(String message, String user, String timeStamp, Color color) {
		this.receiveChatMessageFromChatServer(message, user, timeStamp, color, true);
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
	 * @param point
	 */
	public void receiveChatMessageFromChatServer(String message, String user, String timeStamp, Color color,
			boolean point) {
		if (this.gameClient == null) { // player is not ingame, player is in
										// globalchat
			this.globalChatPanel.appendChatLocal(message, user, timeStamp, color, point);
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
		this.showSettings();
		this.mainFrame.setTitle("Dominion by TPPS - Playing as " + this.username + (offlineMode ? " (OFFLINE) " : ""));

		this.loadPanels();
		this.initClients();

		if (!isOffline()) {
			storageController.checkStandardCards(true);
		}
	}

	/**
	 * opens the main-menu
	 */
	public void joinMainMenu() {
		mainFrame.setPanel(mainMenuPanel);
		MyAudioPlayer.handleMainMusic(true);
		mainFrame.setVisible(true);
	}

	/**
	 * @return whether it is your turn
	 */
	public boolean isTurnFlag() {
		return turnFlag;
	}

	/**
	 * @param turnFlag
	 *            set whether it is your turn
	 */
	public void setTurnFlag(boolean turnFlag) {
		this.turnFlag = turnFlag;
	}

	/**
	 * @param name
	 *            the new username
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
	 * @param singlePlayer
	 */
	public void joinLobbyGui(boolean singlePlayer) {
		this.receiveChatMessageFromChatServer("             ", "****** You joined the lobbyscreen ******",
				"             ", Color.GREEN, false);
		this.globalChatPanel.getBackButton().setLobby(true);
		this.playerSettingsPanel.setStartButtonEnabled(true);
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
		this.playerSettingsPanel.getCardNamesSelected().clear();
		panel.add(this.playerSettingsPanel.updateCards());
		// this.playerSettingsPanel.setStatisticsBoardPanel(this.statisticsBoardPanel);
		this.mainFrame.setPanel(panel);
		if (singlePlayer) {
			this.playerSettingsPanel.add3AIs();
		}
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
		System.out.println("session set");
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
	 * @return whether the game is running in offline-mode
	 */
	public static boolean isOffline() {
		return offlineMode;
	}

	/**
	 * @return the current Session-ID
	 */
	public UUID getSessionID() {
		if (sessionID == null)
			try {
				System.out.println("waiting for session...");
				waitForSession.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return sessionID;
	}

	/**
	 * joins a single-player lobby
	 */
	public void joinSingleplayer() {
		DominionController.getInstance().joinLobbyGui(true);
		try {
			DominionController.getInstance().getMatchmaker().createPrivateMatch(this.username, this.sessionID);
		} catch (IOException e) {
			e.printStackTrace();
		}
		;
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
	public void joinStatisticsGui() {
		this.receiveChatMessageFromChatServer("             ", "*** You  joined  the  communityscreen ***",
				"             ", Color.GREEN, false);
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
		this.statisticsBoardPanel.updateFocus();
	}

	/**
	 * opens the cardeditor
	 * 
	 * @author jhuhn
	 */
	public void joinCardEditor() {
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
	 * 
	 * @param lobbyID
	 *            the Id of the lobby the player is in
	 */
	public void setLobbyID(UUID lobbyID) {
		waitForLobby.release(1);
		this.lobbyID = lobbyID;
	}

	/**
	 * @return the login-GUI-controller
	 */
	public LoginGUIController getLoginGuiController() {
		return loginGuiController;
	}

	/**
	 * @return whether the player is host
	 */
	public boolean isHost() {
		return isHost;
	}

	/**
	 * @param isHost
	 */
	public void setHost(boolean isHost) {
		if (isHost) {
			playerSettingsPanel.enableOrDisableEverything(true);
		} else {
			playerSettingsPanel.enableOrDisableEverything(false);
		}
		this.isHost = isHost;
		GameLog.log(MsgType.MM, "am I a host ? " + isHost);
	}

	/**
	 * blocks until the lobbyID was added
	 * 
	 * @throws InterruptedException
	 */
	public void waitForLobby() throws InterruptedException {
		waitForLobby.drainPermits();
		waitForLobby.acquire(1);
	}

}