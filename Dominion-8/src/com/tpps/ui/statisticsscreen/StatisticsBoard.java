package com.tpps.ui.statisticsscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.login.SQLHandling.Ranking;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.ImageLoader;

/**
 * This class provides all UI functionalities that come with loading all
 * statistics from the database
 * 
 * @author Johannes Huhn, Steffen Jacobs
 */
public class StatisticsBoard extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int VERTICAL_STRUT = 30;
	private static final int HORIZONTAL_STRUT = 50;
	private static final int CELL_ROW_HEIGHT = 50;
	// private BufferedImage blackBeauty;
	private final Font fontTable = new Font("Calibri", Font.PLAIN, 20);
	private final Font fontHead = new Font("Arial Black", Font.BOLD, 14);
	private final Font fontSearch = new Font("Calibri", Font.PLAIN, 45);
	private JTable table;
	private DefaultTableModel model;
	private JTextField jtf;

	private static final Dimension STATSCARD_SIZE = new Dimension(540, 350);

	private int sortColumn = 0;

	private Object columnNames[] = { "nickname", "wins", "losses", "w/l ratio", "total matches", "rank", "playtime" };

	private String[][] statistics;

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

		jtf = new JTextField() {

			private static final long serialVersionUID = 3876533247395550610L;

			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.6"), 0, 0, null);
				super.paintComponent(g);
			}
		};

		jtf.setForeground(Color.WHITE);
		jtf.setFont(fontSearch);

		jtf.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				filterTable(jtf.getText());
			}
		});
		jtf.setOpaque(false);
		jtf.setCaretColor(jtf.getForeground());

		JPanel jpText = new JPanel();
		jpText.setOpaque(false);
		jpText.setLayout(new BorderLayout());
		jpText.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_START);
		jpText.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_END);
		jpText.add(Box.createVerticalStrut(VERTICAL_STRUT), BorderLayout.PAGE_START);
		jpText.add(jtf, BorderLayout.CENTER);
		this.add(this.createTable(this), BorderLayout.CENTER);
		this.add(Box.createVerticalStrut(VERTICAL_STRUT), BorderLayout.PAGE_END);
		this.add(Box.createVerticalStrut(VERTICAL_STRUT), BorderLayout.PAGE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_END);
		this.add(jpText, BorderLayout.PAGE_START);

		GameLog.log(MsgType.INIT, "StatisticsBoard");
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
			this.model.setValueAt(Ranking.getRankByScore((String) this.model.getValueAt(i, 0),
					Integer.parseInt((String) this.model.getValueAt(i, 5))), i, 5);
		}
	}

	/**
	 * @return a time-sting hh:mm:ss
	 * @param milliseconds
	 *            delta-time
	 */
	private String getTimeString(long milliseconds) {
		long second = (milliseconds / 1000) % 60;
		long minute = (milliseconds / (1000 * 60)) % 60;
		long hour = (milliseconds / (1000 * 60 * 60));

		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	/**
	 * sets the statistics-data
	 * 
	 * @param statistics
	 *            the statistics to update
	 */
	public void setStatisticsData(String[][] statistics) {
		this.statistics = statistics;
		this.setTableData(statistics);
	}

	/**
	 * filters the table by input
	 * 
	 * @param start
	 *            the start-sequence to filter
	 */
	private void filterTable(String start) {
		ArrayList<String[]> filtered = new ArrayList<>();
		for (String[] s : statistics) {
			if (s[0].toLowerCase().startsWith(start.toLowerCase())) {
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

	private String[][] getStatsOf(String playerName) {
		if (statistics.length < 1) {
			return null;
		}
		String[][] result = new String[7][2];
		int oldSort = sortColumn;
		for (int i = 0; i < statistics[0].length; i++) {
			sortTableByColumn(i);
			for (int j = 0; j < statistics.length; j++) {
				if (statistics[j][0].equals(playerName)) {
					result[i][0] = statistics[j][i];
					result[i][1] = String.valueOf(j * 100 / statistics.length);
					break;
				}
			}
		}
		sortTableByColumn(oldSort);
		return result;
	}

	private String formatPercentage(String value) {
		if (value.equals("0"))
			return "(Best)\n";
		return "(Top " + value + "%)\n";
	}

	private JPanel createStatisticsCard(String playerName, MouseMotionAdapter dragListener) {
		System.out.println("creating stats-card for " + playerName);

		JPanel panel = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1511323112772019677L;

			private final BufferedImage black = GraphicsUtil.resize(ImageLoader.getImage("black_0.4"),
					STATSCARD_SIZE.width, STATSCARD_SIZE.height);

			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(black, 0, 0, null);
				super.paintComponent(g);
			}

		};

		String[][] stats = getStatsOf(playerName);

		if (stats == null) {
			panel.add(new JLabel("Player not found: " + playerName), BorderLayout.CENTER);
			return panel;
		}

		JPanel gridPane = new JPanel(new BorderLayout());

		Font font = new Font("Calibri", Font.PLAIN, 19);
		Font bigFont = new Font("Calibri", Font.BOLD, 25);

		JLabel header = new JLabel();
		header.setFont(bigFont);
		header.setText("Statistics for " + stats[0][0] + ": ");
		header.setOpaque(false);
		header.setForeground(Color.WHITE);
		header.setHorizontalAlignment(JLabel.CENTER);
		gridPane.add(header, BorderLayout.PAGE_START);

		JTextPane textPane = new JTextPane();
		textPane.setFocusable(false);
		textPane.setOpaque(false);
		textPane.setFont(font);
		textPane.addMouseMotionListener(dragListener);
		textPane.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				locX = e.getX();
				locY = e.getY();

			}
		});
		textPane.setCursor(header.getCursor());

		Style styleWhite = textPane.addStyle("Style", null);
		StyleConstants.setForeground(styleWhite, Color.WHITE);
		StyleConstants.setLineSpacing(styleWhite, 0.6f);

		Style styleGray = textPane.addStyle("Style", null);
		StyleConstants.setForeground(styleGray, new Color(180, 180, 190));

		StyledDocument doc = textPane.getStyledDocument();

		try {
			doc.insertString(doc.getLength(), " Wins:			" + stats[1][0], styleWhite);
			doc.insertString(doc.getLength(), "		" + formatPercentage(stats[1][1]), styleGray);
			doc.insertString(doc.getLength(), " Losses:			" + stats[2][0], styleWhite);
			doc.insertString(doc.getLength(), "		" + formatPercentage(stats[2][1]), styleGray);
			doc.insertString(doc.getLength(), " Win-Loss-Ration:		" + stats[3][0], styleWhite);
			doc.insertString(doc.getLength(), "		" + formatPercentage(stats[3][1]), styleGray);
			doc.insertString(doc.getLength(), " Total Matches:		" + stats[4][0], styleWhite);
			doc.insertString(doc.getLength(), "		" + formatPercentage(stats[4][1]), styleGray);

			String rankString = Ranking.getRankByScore(stats[0][0], Integer.parseInt(stats[5][0]));
			doc.insertString(doc.getLength(), " Rank:			" + rankString, styleWhite);
			doc.insertString(doc.getLength(),
					"	" + (rankString.length() < 9 ? "	" : "") + formatPercentage(stats[5][1]), styleGray);

			String timeString = getTimeString(Long.parseLong(stats[6][0]));
			doc.insertString(doc.getLength(), " Playtime:		" + timeString, styleWhite);
			doc.insertString(doc.getLength(),
					"	" + (timeString.length() < 9 ? "	" : "") + formatPercentage(stats[6][1]), styleGray);
			doc.setParagraphAttributes(0, doc.getLength(), styleWhite, false);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// for (int i = 0; i < 1; i++) {
		// txts[i] = new JLabel(stats[i][0] + " - " + stats[i][1] + 1);
		// lbls[i].setOpaque(false);
		// lbls[i].setForeground(Color.WHITE);
		// lbls[i].setHorizontalAlignment(JLabel.CENTER);
		// gridPane.add(lbls[i]);
		// }
		// txts[5].setText(Ranking.getRankByScore(playerName,
		// Integer.valueOf(stats[5][0])));

		// layout.addLayoutComponent(gridPane, BorderLayout.CENTER);

		gridPane.add(textPane, BorderLayout.CENTER);

		panel.add(gridPane);
		panel.setOpaque(false);
		gridPane.setOpaque(false);

		return panel;
	}

	private int locX, locY;

	/**
	 * 
	 * @param parentPanel the parent panel
	 * @return a JScrollpane with all table data
	 */
	private JScrollPane createTable(JPanel parentPanel) {
		this.table = new JTable(model);
		this.table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				JFrame frame = new JFrame();
				frame.setSize(STATSCARD_SIZE.width, STATSCARD_SIZE.height);
				frame.setResizable(false);
				frame.setUndecorated(true);
				frame.setVisible(true);
				frame.setLayout(new FlowLayout());
				frame.setLocation(
						(int) (parentPanel.getLocation().getX() + parentPanel.getSize().getWidth() / 2 - frame.getSize().getWidth() / 2),
						(int) (parentPanel.getLocation().getY() + parentPanel.getSize().getHeight() / 2 - frame.getSize().getHeight() / 2));

				JPanel layoutPane = new JPanel() {
					private static final long serialVersionUID = -7511174609023430339L;
					private final BufferedImage background = GraphicsUtil.resize(
							ImageLoader.getImage("resources/img/loginScreen/LoginBackground.jpg"), STATSCARD_SIZE.width,
							STATSCARD_SIZE.height);

					@Override
					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.drawImage(background, 0, 0, null);
					}
				};

				frame.setContentPane(layoutPane);
				layoutPane.setLayout(new BorderLayout());
				layoutPane.add(Box.createHorizontalStrut(30), BorderLayout.LINE_START);

				JLabel label = new JLabel();
				label.setForeground(Color.WHITE);
				label.setHorizontalAlignment(JLabel.RIGHT);
				label.setText("x ");
				label.setOpaque(false);
				label.setFont(new Font("Calibri", Font.PLAIN, 25));
				layoutPane.add(Box.createHorizontalGlue(), BorderLayout.NORTH);
				layoutPane.add(Box.createHorizontalStrut(30), BorderLayout.LINE_END);
				layoutPane.add(Box.createVerticalStrut(30), BorderLayout.PAGE_END);
				layoutPane.add(label, BorderLayout.NORTH);
				label.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(MouseEvent e) {
						frame.dispose();
					}

					@Override
					public void mousePressed(MouseEvent e) {
						locX = e.getX();
						locY = e.getY();

					}
				});

				MouseMotionAdapter dragListener = new MouseMotionAdapter() {

					@Override
					public void mouseDragged(MouseEvent e) {
						frame.setLocation((int) (frame.getLocation().getX() - locX + e.getX()),
								(int) (frame.getLocation().getY() - locY + e.getY()));
					}
				};

				frame.addMouseMotionListener(dragListener);
				label.addMouseMotionListener(dragListener);

				frame.addMouseListener(new MouseAdapter() {

					@Override
					public void mousePressed(MouseEvent e) {
						locX = e.getX();
						locY = e.getY();

					}
				});
				layoutPane.add(createStatisticsCard(
						(String) table.getValueAt(table.convertRowIndexToModel(table.rowAtPoint(e.getPoint())), 0),
						dragListener));

			}
		});
		this.initColumnData();
		table.setOpaque(false);
		((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class)).setOpaque(false);
		table.setForeground(Color.WHITE);
		table.setFocusable(false);
		table.setEnabled(false);
		table.setFont(fontTable);
		table.setRowHeight(CELL_ROW_HEIGHT);

		JScrollPane pane = new JScrollPane(table) {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				h.drawImage(ImageLoader.getImage("black_0.6"), 0, 0, this.getWidth(), this.getHeight(), null);
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
		header.setFont(fontHead);
		header.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		header.setReorderingAllowed(false);

		header.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				sortTableByColumn(table.convertColumnIndexToModel(table.columnAtPoint(e.getPoint())));
			}
		});

		return pane;

	}

	/**
	 * sorts the table by a column
	 * 
	 * @param colIndex
	 *            the column to sort the table by
	 */
	private void sortTableByColumn(int colIndex) {
		if (colIndex >= 0) {
			sortColumn = colIndex;

			switch (colIndex) {
			case 0:
				Arrays.sort(statistics, new Comparator<String[]>() {
					@Override
					public int compare(final String[] entry1, final String[] entry2) {
						final String cell1 = entry1[colIndex];
						final String cell2 = entry2[colIndex];
						return cell1.toLowerCase().compareTo(cell2.toLowerCase());
					}
				});
				break;
			case 1:
			case 2:
			case 4:
			case 5:
				Arrays.sort(statistics, new Comparator<String[]>() {
					@Override
					public int compare(final String[] entry1, final String[] entry2) {
						final int cell1 = Integer.parseInt(entry1[colIndex]);
						final int cell2 = Integer.parseInt(entry2[colIndex]);
						return cell2 > cell1 ? 1 : (cell1 == cell2 ? 0 : -1);
					}
				});
				break;
			case 3:
				Arrays.sort(statistics, new Comparator<String[]>() {
					@Override
					public int compare(final String[] entry1, final String[] entry2) {
						final double cell1 = Double.parseDouble(entry1[colIndex]);
						final double cell2 = Double.parseDouble(entry2[colIndex]);
						return cell2 > cell1 ? 1 : (cell1 == cell2 ? 0 : -1);
					}
				});
				break;
			case 6:
				Arrays.sort(statistics, new Comparator<String[]>() {
					@Override
					public int compare(final String[] entry1, final String[] entry2) {
						final long cell1 = Long.parseLong(entry1[colIndex]);
						final long cell2 = Long.parseLong(entry2[colIndex]);
						return cell2 > cell1 ? 1 : (cell1 == cell2 ? 0 : -1);
					}
				});
				break;
			default:
				break;
			}
			StatisticsBoard.this.setTableData(statistics);
		}
	}

	/** sets the focus to the search-bar */
	public void updateFocus() {
		this.jtf.requestFocus();
	}

}
