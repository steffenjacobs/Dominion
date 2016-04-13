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
import com.tpps.technicalServices.network.card.CardClient;
import com.tpps.technicalServices.network.chat.client.ChatClient;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.game.ClientGamePacketHandler;
import com.tpps.technicalServices.network.game.GameClient;
import com.tpps.technicalServices.network.matchmaking.client.Matchmaker;
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
	private Matchmaker matchmaker;
	private CardStorageController storageController;
	private MainFrame mainFrame;
	private LoginGUIController loginGuiController;
	
	private MainMenuPanel mainMenuPanel;
	private GlobalChatPanel globalChatPanel;
	private PlayerSettingsPanel playerSettingsPanel;
	private StatisticsBoard statisticsBoardPanel;

	private BufferedImage originalBackground;
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

	private void init() {
		boolean login = false;
		if(login){
			storageController = new CardStorageController();
			mainFrame = new MainFrame();
			loginGuiController = new LoginGUIController();
		}else{
			this.turnFlag = false;
			try {
				gameClient = new GameClient(new InetSocketAddress(Addresses.getRemoteAddress(), 1339), new ClientGamePacketHandler());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @author jhuhn
	 */
	private void loadPanels(){
		mainMenuPanel = new MainMenuPanel(this.mainFrame);
		globalChatPanel = new GlobalChatPanel();		
		statisticsBoardPanel = new StatisticsBoard();
		playerSettingsPanel = new PlayerSettingsPanel(statisticsBoardPanel);
		this.playerSettingsPanel.insertPlayer(this.username);
		try {
			this.originalBackground = ImageIO.read(ClassLoader.getSystemResource("resources/img/loginScreen/LoginBackground.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void startMatch(int port){
		try {
			gameClient = new GameClient(new InetSocketAddress(Addresses.getRemoteAddress(), port), new ClientGamePacketHandler());
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
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
	 * @author jhuhn
	 * @param statistics
	 */
	public void loadStatisticsToGui(String[][] statistics){
		this.statisticsBoardPanel.setTableData(statistics);
	}
	
	/**
	 * @author jhuhn
	 */
	public void sendPacketToGetStatistics(){
		this.loginGuiController.getLoginclient().sendPacketForAllStatistics();
	}
	
	/**
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
	public void sendChatToGlobalChat(String message){
		this.chatClient.sendMessage(message);
	}
	
	/**
	 * @author jhuhn
	 * @param message
	 */
	public void reveiveChatMessageFromChatServer(String message){
		this.globalChatPanel.appendChatFromServer(message);
	}
	
	/**
	 * @author jhuhn
	 */
	public void endLogin(){
		this.loadPanels();
		this.initClients();
		
		mainFrame.setPanel(mainMenuPanel);
		mainFrame.setVisible(true);
	}
	
	public void joinMainMenu(){
		mainFrame.setPanel(mainMenuPanel);
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
	public void deletePlayerFromGUI(String player){
		this.playerSettingsPanel.removePlayer(player);
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
}