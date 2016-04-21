package com.tpps.application.game;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.tpps.application.storage.CardStorageController;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.chat.client.ChatClient;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
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

	private static DominionController instance;
	private UUID lobbyID;

	private String username, email;
	private UUID sessionID;
	private SessionClient sessionClient;
	private GameClient gameClient;
//	private CardClient cardClient;
	private Matchmaker matchmaker;
	private CardStorageController storageController;
	private MainFrame mainFrame;
	private LoginGUIController loginGuiController;
	
	private MainMenuPanel mainMenuPanel;
	private GlobalChatPanel globalChatPanel;
	private PlayerSettingsPanel playerSettingsPanel;
	private StatisticsBoard statisticsBoardPanel;
	private CardEditor cardEditor;

	private BufferedImage originalBackground;
	public static BufferedImage selectedGameImage;
	private boolean turnFlag;
	
	private ChatClient chatClient;

	/** main entry point for client application */
	public static void main(String[] stuff) {
		instance = new DominionController();
		DominionController.instance.init();
	}
	
	/**
	 * 
	 * @param test
	 */
	public DominionController(boolean test) {
		storageController = new CardStorageController();
		// do nothing else, just init object
	}
	

	public DominionController() { }

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
			mainFrame = new MainFrame();
			loginGuiController = new LoginGUIController();
		} else {
			this.turnFlag = false;
			try {
				gameClient = new GameClient(new InetSocketAddress(
						Addresses.getRemoteAddress(), 1339),
						new ClientGamePacketHandler());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method is responsible to load all GUI components that are needed for: 
	 * - MainMenu 
	 * - CommunityBoard 
	 * - Lobby
	 * @author jhuhn
	 */
	private void loadPanels() {
		mainMenuPanel = new MainMenuPanel(this.mainFrame);
		globalChatPanel = new GlobalChatPanel();
		statisticsBoardPanel = new StatisticsBoard();
		playerSettingsPanel = new PlayerSettingsPanel(statisticsBoardPanel);
		// this.playerSettingsPanel.insertPlayer(this.username);
		try {
			this.originalBackground = ImageIO
					.read(ClassLoader
							.getSystemResource("resources/img/loginScreen/LoginBackground.jpg"));
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
			selectedGameImage = this.playerSettingsPanel.getSelectedPicture();
			System.out.println("FIRST: " + selectedGameImage);
			gameClient = new GameClient(new InetSocketAddress(
					Addresses.getRemoteAddress(), port),
					new ClientGamePacketHandler());
			// this.gameClient.getGameWindow().setBackgroundImage(this.getLobbyBackground());
			this.clearAllPlayersFromGUI();
			this.joinMainMenu();
			this.mainFrame.setVisible(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void finishMatch(){
		this.playerSettingsPanel.initStandardBackground();
		this.joinMainMenu();
	}
	
	/**
	 * This method starts the to search a lobby. It is called, when the user
	 * joins the lobby gui
	 * 
	 * @author jhuhn
	 */
	public void findMatch(){
		try {
			this.matchmaker.findMatch(this.username, this.sessionID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void abortSearching(){
		try {
			this.matchmaker.abort(this.username, this.sessionID);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is called to set the statistics data on the gui
	 * @author jhuhn
	 * @param statistics a twodimensional Array that is filled with all statistics from the database
	 */
	public void loadStatisticsToGui(String[][] statistics){
		this.statisticsBoardPanel.setTableData(statistics);
	}
	
	/**
	 * This method is called to send a packet to the LoginServer (like a push
	 * notification) to receive all statistics
	 * @author jhuhn
	 */
	public void sendPacketToGetStatistics(){
		this.loginGuiController.getLoginclient().sendPacketForAllStatistics();
	}
	
	/**
	 * This method initializes client instances
	 * @author jhuhn
	 */
	private void initClients(){
		this.chatClient = new ChatClient(this.username);
		this.matchmaker = new Matchmaker();
		System.out.println(this.username);
	}
	
	/**
	 * @author jhuhn
	 * @param message
	 */
	public void sendChatMessage(String message){
		this.chatClient.sendMessage(message);
	}
	
	/**
	 * @author jhuhn
	 * @param message
	 */
	public void reveiveChatMessageFromChatServer(String message){
		if(this.gameClient == null){	//player is not ingame, player is in globalchat
			this.globalChatPanel.appendChatLocal(message);
		}else{							//player is ingame
			this.gameClient.getGameWindow().getChatWindow().appendChatLocal(message);
		}
	}
	
	/**
	 * @author jhuhn
	 */
	public void endLogin(){
		this.loadPanels();
		this.initClients();
		
		this.joinMainMenu();
	}
	
	public void joinMainMenu(){
		mainFrame.setPanel(mainMenuPanel);
		mainFrame.setVisible(true);
	}
	
	public boolean isTurnFlag() {
		return turnFlag;
	}

	public void setTurnFlag(boolean turnFlag) {
		this.turnFlag = turnFlag;
	}
	
	public void setUsername(String name){
		this.username = name;
	}

	/**
	 * 
	 * @return
	 */
	public CardStorageController getStorageController() {
		return storageController;
	}

	public BufferedImage getLobbyBackground(){
		return this.playerSettingsPanel.getSelectedPicture();
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
	 * @author jhuhn
	 */
	public void joinLobbyGui() {
	//	statisticsBoardPanel = new StatisticsBoard();
	//	this.playerSettingsPanel.setStatisticsBoardPanel(this.statisticsBoardPanel);
		JPanel panel = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {				
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				
				h.drawImage(originalBackground, 0, 0,this.getWidth(), this.getHeight(), null);
				super.paint(h);
			}
		};
		panel.setLayout(new GridLayout(1, 2));
		panel.setVisible(true);
		panel.setOpaque(false);
		panel.add(this.globalChatPanel);		
		panel.add(this.playerSettingsPanel);
		//this.playerSettingsPanel.setStatisticsBoardPanel(this.statisticsBoardPanel);
		this.mainFrame.setPanel(panel);
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
	
	/**
	 * @author jhuhn
	 * @param player
	 */
	public void insertPlayerToGUI(String player){
		this.playerSettingsPanel.insertPlayer(player);
	}
	
	/**
	 * @author jhuhn
	 * @param player
	 */
	public void clearPlayerFromGUI(String player){
		this.playerSettingsPanel.removePlayer(player);
	}
	
	public void clearAllPlayersFromGUI(){
		this.playerSettingsPanel.clearAllPlayers();
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
	
	public void setCredentials(String username, String email){
		this.username = username;
		this.email = email;
	}


	public void openStatisticsGui() {
		JPanel panel = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				
				h.drawImage(originalBackground, 0, 0,this.getWidth(), this.getHeight(), null);
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
	
	public void openCardeditor(){
		this.mainFrame.setVisible(false);
		this.cardEditor = new CardEditor();
	}

	public UUID getLobbyID() {
		return lobbyID;
	}

	public void setLobbyID(UUID lobbyID) {
		this.lobbyID = lobbyID;
	}
	
	public void sendAIPacket(String name, boolean abort) {
		try {
			this.matchmaker.sendAIPacket(name, this.lobbyID, abort);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public LoginGUIController getLoginGuiController() {
		return loginGuiController;
	}
}