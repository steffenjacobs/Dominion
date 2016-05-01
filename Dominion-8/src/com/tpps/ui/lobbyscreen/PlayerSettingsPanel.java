package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.tpps.application.game.DominionController;
import com.tpps.application.game.card.CardType;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.util.GraphicsUtil;

/**
 * This class creates a JPanel with all gui components, that are shown on the
 * right side of the lobbygui
 * 
 * @author jhuhn
 *
 */
public class PlayerSettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Font head = new Font("Arial Black", Font.BOLD, 20);

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

	private JPanel panel;
	private JPanel panelMid;
	private JPanel panelWest;
	private JPanel panelEast;
	private BufferedImage blackBeauty, temp;
	// private BufferedImage brainCrossed;
	// private BufferedImage brain;

	/**
	 * constructor, initializes the lobby
	 * 
	 * @author jhuhn
	 */
	public PlayerSettingsPanel() {
		this.initOriginalBackgroundImages();
		this.initTransparentBackgroundImages();
		loadingImage();
		this.setOpaque(false);
		this.setLayout(new GridLayout(3, 1, 0, SPACE_PANEL_TO_PANEL));

		this.add(this.upperAreaPanel());
		this.add(this.middleAreaPanel());
		this.add(this.bottomAreaPanel());

		GameLog.log(MsgType.INIT, "PlayerSettingsPanel");
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

		plusKI = new JButton("Add AI") {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				h.drawImage(blackBeauty, 0, 0, this.getWidth(), this.getHeight(), null);
				super.paint(h);
			}
		};
		plusKI.setFont(new Font("Arial", Font.PLAIN, 22));
		plusKI.setOpaque(false);
		plusKI.setForeground(Color.WHITE);
		plusKI.setBorderPainted(true);
		plusKI.setContentAreaFilled(false);

		minusKI = new JButton("Remove AI") {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				h.drawImage(blackBeauty, 0, 0, this.getWidth(), this.getHeight(), null);
				super.paint(h);
			}
		};
		minusKI.setFont(new Font("Arial", Font.PLAIN, 22));
		minusKI.setOpaque(false);
		minusKI.setForeground(Color.WHITE);
		minusKI.setBorderPainted(true);
		minusKI.setContentAreaFilled(false);

		gbc.insets = new Insets(SPACE_PANEL_TO_PANEL, H_SPACE_EDGE_TO_FIRSTPANEL, 0, VERTICAL_GAP_KI);
		gbc.ipady = WEIGHT_BUTTON_KI;
		gbc.ipadx = WEIGHT_BUTTON_KI;
		panelEast.add(plusKI, gbc);
		panelEast.setOpaque(false);

		gbc.insets = new Insets(SPACE_PANEL_TO_PANEL, VERTICAL_GAP_KI, 0, H_SPACE_EDGE_TO_FIRSTPANEL);
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

		plusKI.addMouseListener(new KiListener());
		minusKI.addMouseListener(new KiListener());
		return panel;
	}

	private Dimension getCardSize(int wdt, int hght) {

		return new Dimension((int) (DominionController.getInstance().getMainFrame().getHeight() / 3d / hght * wdt),
				DominionController.getInstance().getMainFrame().getHeight() / 3 - SPACE_PANEL_TO_PANEL * 2
						- scrollBarHeight);
	}

	class CardDisplayButton extends JButton implements MouseListener {

		private SerializedCard card;

		private BufferedImage imgSelected = null;

		/**
		 * 
		 */
		private static final long serialVersionUID = 2289556894288934256L;

		public CardDisplayButton(SerializedCard originalCard) {
			this.card = originalCard;
			this.addMouseListener(this);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D h = (Graphics2D) g;
			h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			if (imgSelected == null) {
				h.drawImage(card.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
			} else {
				h.drawImage(imgSelected, 0, 0, this.getWidth(), this.getHeight(), null);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (imgSelected == null) {
				cardNamesSelected.add(this.card.getName());
				imgSelected = GraphicsUtil.colorScale(new Color(0, 0, 6), card.getImage(), .4f);
			} else {
				cardNamesSelected.remove(this.card.getName());
				imgSelected = null;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO
		}
	}

	private class CardResizeListener implements ComponentListener {

		SerializedCard card;

		public CardResizeListener(SerializedCard card) {
			this.card = card;
		}

		@Override
		public void componentResized(ComponentEvent e) {
			System.out.println("resize");
			System.out.println(panelMid.getComponentCount() + " - " + panelMid.getComponents());

			Component comp;
			for (int i = 0; i < panelMid.getComponentCount(); i++) {
				comp = panelMid.getComponent(i);
				System.out.println(getCardSize(card.getImage().getWidth(), card.getImage().getHeight()));
				comp.setPreferredSize(getCardSize(card.getImage().getWidth(), card.getImage().getHeight()));
				comp.revalidate();
			}
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void componentShown(ComponentEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void componentHidden(ComponentEvent e) {
			// TODO Auto-generated method stub

		}
	}

	ArrayList<String> cardNamesSelected = new ArrayList<>();

	/**
	 * @author jhuhn
	 * @return a JPanel to select cardsets
	 */
	private JScrollPane middleAreaPanel() {

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
		scrollMid.setBorder(BorderFactory.createEmptyBorder());

		Iterator<SerializedCard> it = DominionController.getInstance().getCardRegistry().getAllCards().iterator();

		panelMid.setLayout(new FlowLayout());

		SerializedCard firstCard = null;
		while (it.hasNext()) {
			SerializedCard card = it.next();
			if (firstCard == null) {
				firstCard = card;
			}

			if (!card.getTypes().contains(CardType.ACTION)) {
				continue;
			}

			CardDisplayButton displayedCard = new CardDisplayButton(card);
			displayedCard.setContentAreaFilled(false);

			System.out.println();

			displayedCard.setPreferredSize(getCardSize(card.getImage().getWidth(), card.getImage().getHeight()));

			displayedCard.setBorderPainted(false);
			displayedCard.setToolTipText(card.getName());
			displayedCard.setVisible(true);

			panelMid.add(displayedCard);
		}
		if (firstCard != null)

		{
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
		try {
			this.originalImages[0] = ImageIO
					.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/spring.jpg"));
			this.originalImages[1] = ImageIO
					.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/summer.jpg"));
			this.originalImages[2] = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/fall.jpg"));
			this.originalImages[3] = ImageIO
					.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/winter.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method sets all alpha values to all backgrounds
	 * 
	 * @author jhuhn
	 */
	public void initTransparentBackgroundImages() {
		this.transparentImages[0] = (BufferedImage) GraphicsUtil.setAlpha(originalImages[0], 0.5F);
		this.transparentImages[1] = (BufferedImage) GraphicsUtil.setAlpha(originalImages[1], 0.5F);
		this.transparentImages[2] = (BufferedImage) GraphicsUtil.setAlpha(originalImages[2], 0.5F);
		this.transparentImages[3] = (BufferedImage) GraphicsUtil.setAlpha(originalImages[3], 0.5F);
	}

	/**
	 * loads inital images to modify ui components in a good looking way
	 * 
	 * @author jhuhn
	 */
	public void loadingImage() {
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			// this.brain =
			// ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/brain.png"));
			// this.brainCrossed = ImageIO
			// .read(ClassLoader.getSystemResource("resources/img/lobbyScreen/braincrossed.png"));
			blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, PlayerSettingsPanel.ALPHA);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JTextField createHeader(String text) {
		JTextField header = new JTextField(text);
		Map attributes = head.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
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
		System.out.println("ZERO: " + this.selectedImage);
		return this.selectedImage;
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
				System.out.println("GUI: inserted Player: " + connectedPlayers[i].getText());
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
			if (connectedPlayers[i].getText().equals(player)) {
				System.out.println("GUI: removed Player: " + connectedPlayers[i].getText());
				connectedPlayers[i].resetSearchingField();
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
			if (!connectedPlayers[i].getText().startsWith("Loading")) {
				System.out.println("GUI: removed Player(all): " + connectedPlayers[i].getText());
				connectedPlayers[i].resetSearchingField();
			}
		}
	}

	/**
	 * this method initializes a standard background
	 * 
	 * @author jhuhn
	 */
	public void initStandardBackground() {
		this.changeSelectedPicture(0);
	}

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


	private ArrayList<String> aiNames = new ArrayList<>();
	
	private class KiListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// if (e.getSource().equals(minusKI)) {
			// minusKI.setText("");
			// temp = blackBeauty;
			//// blackBeauty = brainCrossed;
			// }
			// if (e.getSource().equals(plusKI)) {
			// plusKI.setText("");
			// temp = blackBeauty;
			//// blackBeauty = brain;
			//
			// }
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// if (e.getSource().equals(minusKI)) {
			// blackBeauty = temp;
			// minusKI.setText("Remove AI");
			// }
			// if (e.getSource().equals(plusKI)) {
			// blackBeauty = temp;
			// plusKI.setText("Add AI");
			// }
		}


		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			System.out.println("klick");
			if (e.getSource() == plusKI) {
				aiNames.add("" + System.identityHashCode(e));
				DominionController.getInstance().sendAIPacket("AI_" + System.identityHashCode(e), false);
			} else if (e.getSource() == minusKI && aiNames.size() > 0) {
				DominionController.getInstance().sendAIPacket("AI_" + aiNames.remove(0), true);
				System.out.println("minus");

			}
		}
	}
}
