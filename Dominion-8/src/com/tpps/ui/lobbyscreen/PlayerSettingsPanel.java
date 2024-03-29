package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.tpps.application.game.DominionController;
import com.tpps.application.game.card.CardType;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.FontLoader;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.ImageLoader;
import com.tpps.technicalServices.util.MyAudioPlayer;

/**
 * This class creates a JPanel with all gui components, that are shown on the
 * right side of the lobbygui
 * 
 * @author jhuhn
 *
 */
public class PlayerSettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Font head;

	private BufferedImage[] originalImages = new BufferedImage[4];
	private BufferedImage[] transparentImages = new BufferedImage[4];
	private BufferedImage selectedImage;
	private JLabel[] labelImages;

	private SearchingField[] connectedPlayers;

	private static final int SPACE_PANEL_TO_PANEL = 25;
	private static final int SPACE_PLAYER_TO_PLAYER = 5;
	private static final int SPACE_FIRSTPANEL_TO_SECONDPANEL = 10;
	private static int H_SPACE_EDGE_TO_FIRSTPANEL = 60;
	private static final int WEIGHT_BUTTON_KI = 40;
	private static final int VERTICAL_GAP_KI = 20;
	private static final int IMG_GRID_GAP = 20;
	private static int HEADER_TO_IMG_MARGIN = 15;
	private static int IMG_TO_BOTTOM = 15;
	private static final int IMG_TO_EDGE = 30;

	private int scrollBarHeight;

	private static final float ALPHA = 0.6F;

	private JButton plusKI, minusKI;
	private ArrayList<String> cardNamesSelected = new ArrayList<>();
	private ArrayList<CardDisplayButton> allCards = new ArrayList<CardDisplayButton>();

	private JPanel panel;
	private JPanel panelMid;
	private JPanel panelWest;
	private JPanel panelEast;
	private BufferedImage blackBeauty;

	private JScrollPane midScroller;
	private JPanel bottomAreaPanel;

	private StartButton startButton;
	private BufferedImage greenLanton;
	private BufferedImage redHead;
	private JPanel midpanel;

	private BufferedImage brainCrossed;
	private BufferedImage brain;

	private ArrayList<String> aiNames;
	private ArrayList<String> aiNameList;

	private int allplayers;

	// private boolean singlePlayer = false;

	/**
	 * constructor, initializes the lobby
	 * 
	 * @author jhuhn
	 */
	public PlayerSettingsPanel() {
		this.allplayers = 0;
		this.fontLoading();
		this.initOriginalBackgroundImages();
		this.initTransparentBackgroundImages();
		this.loadingImage();
		this.setOpaque(false);
		this.setLayout(new GridLayout(3, 1, 0, SPACE_PANEL_TO_PANEL));
		this.head = this.head.deriveFont(Font.BOLD, 25f);

		this.add(this.upperAreaPanel());
		// this.midScroller = this.middleAreaPanel();
		// this.add(midScroller);
		// this.add(this.bottomAreaPanel());
		this.bottomAreaPanel = this.bottomAreaPanel();
		GameLog.log(MsgType.INIT, "PlayerSettingsPanel");
	}

	/**
	 * init the aiNameList. Random names from this list will be chosen for AI
	 * names in the multi- and single player modes
	 */
	public void initAiNameList() {
		this.aiNameList = CollectionsUtil.getArrayList(new String[] { "Sir Lancelot", "Ritter der Kokosnuss", "Wizard of Oz", "King Arthur", "Felix Antoine Blume", "Charlie Chaplin", "Bruce Wayne",
				"Black Beauty", "Walter White", "Harry Potter", "Alice bei 1&1", "Larry der Lachs", "John the Ripper", "Sherlock Holmes", "L as Looser", "Mark Zuckerberg" });
		this.aiNames = new ArrayList<String>();
	}

	/**
	 * This constructor is only needed to handle a junit test
	 * 
	 * @author jhuhn
	 * @param junitTest
	 *            just to overload the constructor
	 */
	public PlayerSettingsPanel(boolean junitTest) {
		connectedPlayers = new SearchingField[4];
		for (int i = 0; i < connectedPlayers.length; i++) {
			connectedPlayers[i] = new SearchingField(true);
			connectedPlayers[i].start();
		}
	}

	/**
	 * @author jhuhn
	 * @return a JPanel with all components on the upperpanel, includind all
	 *         joined players, and add or remove AI buttons
	 */
	private JPanel upperAreaPanel() {
		this.panel = new JPanel(new BorderLayout());
		panelWest = new JPanel(new GridBagLayout());
		panelEast = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setOpaque(false);

		this.plusKI = new KIButton(brain, true, this);
		this.minusKI = new KIButton(brainCrossed, false, this);

		gbc.insets = new Insets(SPACE_PANEL_TO_PANEL, H_SPACE_EDGE_TO_FIRSTPANEL, 0, VERTICAL_GAP_KI);
		gbc.ipady = WEIGHT_BUTTON_KI;
		gbc.ipadx = WEIGHT_BUTTON_KI;
		this.startButton = new StartButton();
		panelEast.add(this.startButton, gbc);
		panelEast.setOpaque(false);

		gbc.insets = new Insets(SPACE_PANEL_TO_PANEL, VERTICAL_GAP_KI, 0, H_SPACE_EDGE_TO_FIRSTPANEL);
		gbc.gridy = 0;
		panelWest.add(plusKI, gbc);
		gbc.gridy = 1;
		panelWest.add(minusKI, gbc);
		panelWest.setOpaque(false);

		connectedPlayers = new SearchingField[4];
		for (int i = 0; i < connectedPlayers.length; i++) {
			connectedPlayers[i] = new SearchingField();
			connectedPlayers[i].start();
		}

		JPanel center = new JPanel(new GridLayout(8, 1));
		center.setOpaque(false);

		center.add(Box.createVerticalStrut(SPACE_PLAYER_TO_PLAYER));
		center.add(connectedPlayers[0]);

		center.add(Box.createVerticalStrut(SPACE_PLAYER_TO_PLAYER));
		center.add(connectedPlayers[1]);

		center.add(Box.createVerticalStrut(SPACE_PLAYER_TO_PLAYER));
		center.add(connectedPlayers[2]);

		center.add(Box.createVerticalStrut(SPACE_PLAYER_TO_PLAYER));
		center.add(connectedPlayers[3]);

		JTextField header = this.createHeader("Connected Players:");

		panel.add(panelWest, BorderLayout.WEST);
		panel.add(panelEast, BorderLayout.EAST);
		panel.add(center, BorderLayout.CENTER);
		panel.add(header, BorderLayout.NORTH);
		panel.add(Box.createVerticalStrut(SPACE_FIRSTPANEL_TO_SECONDPANEL), BorderLayout.PAGE_END);

		return panel;
	}

	/**
	 * @param enable
	 *            boolean to enable and disable startbutton
	 */
	public void setStartButtonEnabled(boolean enable) {
		this.startButton.setEnabled(enable);
	}

	private class StartButton extends JButton implements ActionListener, MouseListener {

		private static final long serialVersionUID = 1L;
		private boolean enabledFlag;
		private BufferedImage switchimage;
		private BufferedImage greenHover;

		public StartButton() {
			switchimage = PlayerSettingsPanel.this.redHead;
			this.greenHover = greenLanton;
			this.greenHover = GraphicsUtil.colorScale(Color.BLACK, greenHover, 0.75F);
			this.enabledFlag = false;
			this.setText("Start");
			this.setOpaque(false);
			this.setContentAreaFilled(false);
			this.setBorderPainted(false);
			this.setForeground(Color.WHITE);
			this.setHorizontalTextPosition(SwingConstants.CENTER);
			this.addActionListener(this);
			this.addMouseListener(this);
			this.setFont(head);
		}

		@Override
		public void paint(Graphics g) {
			Graphics2D h = (Graphics2D) g;
			h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			h.drawImage(switchimage, 0, 0, this.getWidth(), this.getHeight(), null);
			super.paint(h);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (enabledFlag) {
				MyAudioPlayer.doClick();

				this.setEnabled(false);
				this.enabledFlag = false;
				String[] selCards = new String[cardNamesSelected.size()];
				cardNamesSelected.toArray(selCards);

				try {
					DominionController.getInstance().getMatchmaker().sendStartPacket(DominionController.getInstance().getUsername(), DominionController.getInstance().getSessionID(),
							DominionController.getInstance().getLobbyID(), selCards);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				GameLog.log(MsgType.INFO, "Starting game...");
			} else {
				DominionController.getInstance().receiveChatMessageFromChatServer("You are not ready to start the match\n" + "         selected cards: "
						+ PlayerSettingsPanel.this.cardNamesSelected.size() + "/10" + "\n" + "         connectedplayers: " + PlayerSettingsPanel.this.connectedPlayers() + "/4", "BOT", "",
						Color.YELLOW);
			}
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (enabledFlag) {
				this.switchimage = greenHover;
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (enabledFlag) {
				this.switchimage = PlayerSettingsPanel.this.greenLanton;
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	/**
	 * handles the start button logic
	 */
	public void handleStartButton() {
		boolean validate = this.validateStartButton();
		this.startButton.enabledFlag = validate;
		if (validate) {
			this.startButton.switchimage = greenLanton;
		} else {
			this.startButton.switchimage = redHead;
		}
		this.startButton.revalidate();
		this.startButton.repaint();
	}

	/**
	 * @param enable
	 *            true: host can select gamesettings, false: no host no power
	 */
	public void enableOrDisableEverything(boolean enable) {
		this.minusKI.setEnabled(enable);
		this.plusKI.setEnabled(enable);
		this.startButton.setEnabled(enable);
		this.midScroller.setEnabled(enable);
		this.panelMid.setEnabled(enable);

		for (Iterator<CardDisplayButton> iterator = allCards.iterator(); iterator.hasNext();) {
			((CardDisplayButton) iterator.next()).setEnabled(enable);
		}
	}

	/**
	 * @return validates the startbutton logic
	 */
	public boolean validateStartButton() {
		if (!DominionController.getInstance().isHost()) {
			return false;
		}
		if (this.cardNamesSelected.size() != 10) {
			return false;
		}
		GameLog.log(MsgType.GAME_INFO, "connectedplayers: " + this.connectedPlayers());
		if (this.connectedPlayers() != 4) {
			return false;
		}
		return true;
	}

	/**
	 * updates cards in playersettingspanel
	 * 
	 * @author sjacobs
	 * @return an instance of updated playersettingpanel
	 */
	public PlayerSettingsPanel updateCards() {
		if (this.midpanel != null) {
			this.remove(midpanel);
			this.remove(bottomAreaPanel);
		}
		this.midScroller = cardAreaPanel();

		midpanel = new JPanel(new BorderLayout());
		midpanel.setOpaque(false);
		midpanel.add(midScroller, BorderLayout.CENTER);
		midpanel.add(Box.createHorizontalStrut(IMG_TO_EDGE), BorderLayout.WEST);
		midpanel.add(Box.createHorizontalStrut(IMG_TO_EDGE), BorderLayout.EAST);

		this.add(midpanel);
		this.add(bottomAreaPanel);
		return this;
	}

	private Dimension getCardSize(int wdt, int hght) {

		return new Dimension((int) (DominionController.getInstance().getMainFrame().getHeight() / 3d / hght * wdt),
				DominionController.getInstance().getMainFrame().getHeight() / 3 - SPACE_PANEL_TO_PANEL * 2 - scrollBarHeight);
	}

	private class CardDisplayButton extends JButton implements ActionListener {

		private SerializedCard card;

		private BufferedImage imgNotSelected = null;
		private boolean selected;

		private static final long serialVersionUID = 2289556894288934256L;

		public CardDisplayButton(SerializedCard originalCard) {
			this.card = originalCard;
			this.addActionListener(this);
			this.imgNotSelected = GraphicsUtil.colorScale(new Color(0, 0, 6), card.getImage(), .4f);
			this.selected = false;
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D h = (Graphics2D) g;
			h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			if (selected) {
				h.drawImage(card.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
			} else {
				h.drawImage(imgNotSelected, 0, 0, this.getWidth(), this.getHeight(), null);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			MyAudioPlayer.doClick();
			if (selected) {
				selected = false;
				cardNamesSelected.remove(this.card.getName());
			} else {
				selected = true;
				cardNamesSelected.add(this.card.getName());
			}
			PlayerSettingsPanel.this.handleStartButton();
		}
	}

	private class CardResizeListener implements ComponentListener {

		SerializedCard card;

		public CardResizeListener(SerializedCard card) {
			this.card = card;
		}

		@Override
		public void componentResized(ComponentEvent e) {
			Component comp;
			for (int i = 0; i < panelMid.getComponentCount(); i++) {
				comp = panelMid.getComponent(i);
				comp.setPreferredSize(getCardSize(card.getImage().getWidth(), card.getImage().getHeight()));
				comp.revalidate();
			}
		}

		@Override
		public void componentMoved(ComponentEvent e) {

		}

		@Override
		public void componentShown(ComponentEvent e) {

		}

		@Override
		public void componentHidden(ComponentEvent e) {

		}
	}

	/**
	 * @author jhuhn, sjacobs
	 * @return a JPanel to select cardsets
	 */
	private JScrollPane cardAreaPanel() {

		panelMid = new JPanel();
		panelMid.setOpaque(false);
		JScrollPane scrollMid = new JScrollPane(panelMid) {

			private static final long serialVersionUID = 7571416654753384462L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(blackBeauty, 0, 0, null);
			}
		};
		scrollMid.setOpaque(false);
		scrollMid.getViewport().setOpaque(false);
		scrollMid.getHorizontalScrollBar().setOpaque(false);
		scrollMid.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollMid.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollMid.setBorder(BorderFactory.createMatteBorder(0, 5, 5, 5, new Color(0, 0, 0, 20)));

		Iterator<SerializedCard> it = DominionController.getInstance().getCardRegistry().getAllCards().iterator();

		panelMid.setLayout(new FlowLayout());

		SerializedCard firstCard = null;
		while (it.hasNext()) {
			SerializedCard card = it.next();
			if (firstCard == null) {
				firstCard = card;
			}

			if (!card.getTypes().contains(CardType.ACTION) && !card.getName().equals("Gardens")) {
				continue;
			}

			CardDisplayButton displayedCard = new CardDisplayButton(card);
			displayedCard.setContentAreaFilled(false);
			allCards.add(displayedCard);

			displayedCard.setPreferredSize(getCardSize(card.getImage().getWidth(), card.getImage().getHeight()));

			displayedCard.setBorderPainted(false);
			displayedCard.setToolTipText(card.getName());
			displayedCard.setVisible(true);

			panelMid.add(displayedCard);
		}
		if (firstCard != null) {
			scrollMid.addComponentListener(new CardResizeListener(firstCard));
		}
		scrollBarHeight = scrollMid.getHorizontalScrollBar().getHeight();
		return scrollMid;
	}

	/**
	 * @author jhuhn
	 * @return a JPanel to select a nice gamewindow background
	 */
	private JPanel bottomAreaPanel() {
		JPanel overhead = new JPanel(new BorderLayout());
		overhead.setOpaque(false);

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(2, 2, IMG_GRID_GAP, IMG_GRID_GAP));

		labelImages = new JLabel[4];
		for (int i = 0; i < labelImages.length; i++) {
			labelImages[i] = new JLabel();
			labelImages[i].addMouseListener(new ImageListener());
			labelImages[i].setHorizontalAlignment(JLabel.CENTER);
		}

		this.initStandardBackground();

		panel.add(labelImages[0]);
		panel.add(labelImages[1]);
		panel.add(labelImages[2]);
		panel.add(labelImages[3]);

		JTextField header = this.createHeader("Choose Background:");
		header.setBorder(BorderFactory.createEmptyBorder(0, 0, HEADER_TO_IMG_MARGIN, 0));

		overhead.add(panel, BorderLayout.CENTER);
		overhead.add(header, BorderLayout.PAGE_START);
		overhead.add(Box.createVerticalStrut(IMG_TO_BOTTOM), BorderLayout.PAGE_END);
		overhead.add(Box.createHorizontalStrut(IMG_TO_EDGE), BorderLayout.LINE_START);
		overhead.add(Box.createHorizontalStrut(IMG_TO_EDGE), BorderLayout.LINE_END);
		return overhead;
	}

	/**
	 * handles the selcted picture logic
	 * 
	 * @author jhuhn
	 *
	 */
	private class ImageListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			MyAudioPlayer.doClick();
			if (e.getSource() == labelImages[0]) {
				PlayerSettingsPanel.this.changeSelectedPicture(0);
			} else if (e.getSource() == labelImages[1]) {
				PlayerSettingsPanel.this.changeSelectedPicture(1);
			} else if (e.getSource() == labelImages[2]) {
				PlayerSettingsPanel.this.changeSelectedPicture(2);
			} else if (e.getSource() == labelImages[3]) {
				PlayerSettingsPanel.this.changeSelectedPicture(3);
			}
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

	/**
	 * This method loads all backgroundimages
	 * 
	 * @author jhuhn
	 */
	public void initOriginalBackgroundImages() {
		this.originalImages[0] = ImageLoader.getImage("resources/img/lobbyScreen/spring.jpg");
		this.originalImages[1] = ImageLoader.getImage("resources/img/lobbyScreen/summer.jpg");
		this.originalImages[2] = ImageLoader.getImage("resources/img/lobbyScreen/fall.jpg");
		this.originalImages[3] = ImageLoader.getImage("resources/img/lobbyScreen/winter.jpg");
	}

	/**
	 * This method sets all alpha values to all backgrounds
	 * 
	 * @author jhuhn
	 */
	public void initTransparentBackgroundImages() {
		this.transparentImages[0] = GraphicsUtil.colorScale(Color.BLACK, originalImages[0], 0.2F);
		this.transparentImages[1] = GraphicsUtil.colorScale(Color.BLACK, originalImages[1], 0.2F);
		this.transparentImages[2] = GraphicsUtil.colorScale(Color.BLACK, originalImages[2], 0.2F);
		this.transparentImages[3] = GraphicsUtil.colorScale(Color.BLACK, originalImages[3], 0.2F);
	}

	/**
	 * loads inital images to modify ui components in a good looking way
	 * 
	 * @author jhuhn
	 */
	public void loadingImage() {
		this.blackBeauty = ImageLoader.getImage("resources/img/lobbyScreen/blackbeauty.png");
		this.greenLanton = ImageLoader.getImage("resources/img/lobbyScreen/greenlanton.png");
		this.greenLanton = (BufferedImage) GraphicsUtil.setAlpha(greenLanton, 0.6F);
		this.redHead = ImageLoader.getImage("resources/img/lobbyScreen/redhead.png");
		this.redHead = (BufferedImage) GraphicsUtil.setAlpha(redHead, 0.6F);
		this.brain = ImageLoader.getImage("resources/img/lobbyScreen/brain.png");
		this.brainCrossed = ImageLoader.getImage("resources/img/lobbyScreen/braincrossed.png");
		blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, PlayerSettingsPanel.ALPHA);
	}

	/**
	 * This method handles a backgroundselection vote
	 * 
	 * @author jhuhn
	 * @param index
	 *            integer representation of the selected background
	 */
	public void changeSelectedPicture(int index) {
		for (int i = 0; i < originalImages.length; i++) {
			if (i == index) {
				labelImages[i].setIcon(new ImageIcon(originalImages[i]));
				labelImages[i].setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
				this.selectedImage = originalImages[i];
			} else {
				labelImages[i].setIcon(new ImageIcon(transparentImages[i]));
				labelImages[i].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
			}
		}
	}

	/**
	 * @author jhuhn
	 * @param text
	 *            String representation of header text
	 * @return a JTextField object with a nice underlined header
	 */
	public JTextField createHeader(String text) {
		JTextField header = new JTextField(text);

		@SuppressWarnings("unchecked")
		Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>) head.getAttributes();

		// attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		header.setFont(head.deriveFont(attributes));

		header.setOpaque(false);
		header.setFocusable(false);
		header.setBorder(BorderFactory.createEmptyBorder());
		header.setHorizontalAlignment(JTextField.LEFT);
		header.setForeground(Color.WHITE);
		return header;
	}

	/**
	 * @author jhuhn
	 * @return the selected picture
	 */
	public BufferedImage getSelectedPicture() {
		GameLog.log(MsgType.GUI, "ZERO: " + this.selectedImage);
		return this.selectedImage;
	}

	/**
	 * loads a specific font
	 * 
	 * @author nagrawal
	 */
	public void fontLoading() {
		try {
			if (head == null) {
				head = FontLoader.getInstance().getXenipa();
				if (head == null) {
					head = new FontLoader().importFont();
				}
			}
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @author jhuhn
	 * @param player
	 *            String representation of the joined user
	 * @return true, if the user joined correctly on the UI, false else
	 */
	public synchronized boolean insertPlayer(String player) {
		for (int i = 0; i < connectedPlayers.length; i++) {
			if (!connectedPlayers[i].isPlayerFlag()) {
				this.connectedPlayers[i].setPlayer(player);
				GameLog.log(MsgType.GUI, "inserted Player: " + connectedPlayers[i].getText());
				this.handleStartButton();
				this.allplayers++;
				System.err.println("players " + allplayers);
				return true;
			}
		}
		return false;
	}

	/**
	 * @author jhuhn
	 * @param player
	 *            String representation of the joined user
	 * @return true, if the user removed correctly on the UI, false else
	 */
	public synchronized boolean removePlayer(String player) {
		for (int i = 0; i < connectedPlayers.length; i++) {
			if (connectedPlayers[i].getText().startsWith(player)) {
				GameLog.log(MsgType.GUI, "removed Player: " + connectedPlayers[i].getText());
				connectedPlayers[i].resetSearchingField();
				this.handleStartButton();
				this.allplayers--;
				System.err.println("players " + allplayers);
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * This method cleares all joined users from the gui
	 * 
	 * @author jhuhn
	 */
	public synchronized void clearAllPlayers() {
		for (int i = 0; i < connectedPlayers.length; i++) {
			GameLog.log(MsgType.GUI, "removed Player(all): " + connectedPlayers[i].getText());
			connectedPlayers[i].resetSearchingField();
		}
		this.allplayers = 0;
	}

	/**
	 * @return an Integer that represents the number of connectedplayers in th
	 *         gui
	 */
	public synchronized int connectedPlayers() {
		int players = 0;
		for (int i = 0; i < connectedPlayers.length; i++) {
			if (connectedPlayers[i].isPlayerFlag()) {
				players++;
			}
		}
		return players;
	}

	/**
	 * this method initializes a standard background
	 * 
	 * @author jhuhn
	 */
	public void initStandardBackground() {
		this.changeSelectedPicture(0);
	}

	/**
	 * changes the alpha value of a new BufferedImage
	 * 
	 * @param image
	 *            the image to edit
	 * @param alpha
	 *            the new alpha value
	 */
	protected static void setAlpha(BufferedImage image, float alpha) {
		image = (BufferedImage) GraphicsUtil.setAlpha(image, alpha);
	}

	/**
	 * @return a SearchingField array with connected players on the gui
	 * @author jhuhn
	 */
	public SearchingField[] getConnectedPlayers() {
		return connectedPlayers;
	}

	/**
	 * @return an arraylist of all selected cards
	 */
	public ArrayList<String> getCardNamesSelected() {
		return cardNamesSelected;
	}

	/**
	 * @author sjacobs
	 * @return an Arraylist of Stirngs with all AI names
	 */
	public ArrayList<String> getAiNames() {
		return aiNames;
	}

	/**
	 * @author nwipfler
	 * @return a String representation of a random AI
	 */
	public synchronized String getAiName() {
		// try {
		// Thread.sleep(50);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// this.plusKI.setEnabled(false);
		String randomName;
		try {
			randomName = this.aiNameList.get((int) (Math.random() * aiNameList.size()));
			while (aiNames.contains(randomName))
				randomName = this.aiNameList.get((int) (Math.random() * aiNameList.size()));
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			randomName = "Random Nr: " + ((int) (Math.random() * 30));
		}
		this.aiNames.add(randomName);
		// this.aiNameList.remove(randomName);
		// System.out.println("aiNameList, size: " + aiNameList.size() +
		// aiNameList.toString());
		// this.plusKI.setEnabled(true);
		return randomName;
	}

	/**
	 * removes the last AI name
	 * 
	 * @author nwipfler
	 * @return the removed AI Name
	 */
	public String removeAiName() {
		// this.minusKI.setEnabled(false);
		String removed = this.aiNames.remove(aiNames.size() - 1);
		// this.aiNameList.add(removed);
		// System.out.println("aiNameList, size: " + aiNameList.size() +
		// aiNameList.toString());
		// this.minusKI.setEnabled(true);
		return removed;
	}

	/**
	 * adds 3 AIs to the lobby, used for singleplayer
	 * 
	 * @author sjacobs
	 */
	public void add3AIs() {
		new Thread(() -> {
			try {
				DominionController.getInstance().waitForLobby();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.out.println("trying to add 3 ais " + DominionController.getInstance().getLobbyID());

			if (DominionController.getInstance().getLobbyID() == null) {
				try {
					DominionController.getInstance().waitForLobby();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < 3; i++) {
				((ActionListener) this.plusKI).actionPerformed(new ActionEvent(this, -1, ""));
				try {
					Thread.sleep(1200);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * @return all players
	 */
	public int getAllPlayers() {
		return allplayers;
	}

	/**
	 * @param allplayers
	 *            set all players
	 */
	public void setAllplayers(int allplayers) {
		this.allplayers = allplayers;
	}
}
