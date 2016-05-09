package com.tpps.ui.statisticsscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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
import com.tpps.technicalServices.network.login.SQLHandling.Ranking;
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
	private final Font font = new Font("Calibri", Font.PLAIN, 20);
	private final Font headFont = new Font("Arial Black", Font.BOLD, 14);
	private final Font fontSearch = new Font("Calibri", Font.PLAIN, 45);
	private JTable table;
	private DefaultTableModel model;
	private JTextField jtf;

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
		this.add(this.createTable(), BorderLayout.CENTER);
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

		header.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int index = table.convertColumnIndexToModel(table.columnAtPoint(e.getPoint()));
				if (index >= 0) {

					switch (index) {
					case 0:
						Arrays.sort(statistics, new Comparator<String[]>() {
							@Override
							public int compare(final String[] entry1, final String[] entry2) {
								final String cell1 = entry1[index];
								final String cell2 = entry2[index];
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
								final int cell1 = Integer.parseInt(entry1[index]);
								final int cell2 = Integer.parseInt(entry2[index]);
								return cell2 > cell1 ? 1 : (cell1 == cell2 ? 0 : -1);
							}
						});
						break;
					case 3:
						Arrays.sort(statistics, new Comparator<String[]>() {
							@Override
							public int compare(final String[] entry1, final String[] entry2) {
								final double cell1 = Double.parseDouble(entry1[index]);
								final double cell2 = Double.parseDouble(entry2[index]);
								return cell2 > cell1 ? 1 : (cell1 == cell2 ? 0 : -1);
							}
						});
						break;
					case 6:
						Arrays.sort(statistics, new Comparator<String[]>() {
							@Override
							public int compare(final String[] entry1, final String[] entry2) {
								final long cell1 = Long.parseLong(entry1[index]);
								final long cell2 = Long.parseLong(entry2[index]);
								System.out.println(cell2 + " - " + cell1);
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
		});

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

	/** sets the focus to the search-bar */
	public void updateFocus() {
		this.jtf.requestFocus();
	}

}
