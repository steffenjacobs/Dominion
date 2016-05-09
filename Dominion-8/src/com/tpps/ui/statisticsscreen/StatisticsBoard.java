package com.tpps.ui.statisticsscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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
import com.tpps.technicalServices.util.ImageLoader;
import com.tpps.technicalServices.util.MathUtil;

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
					MathUtil.getTimeString(Long.valueOf((String) model.getValueAt(i, this.model.getColumnCount() - 1))), i,
					this.model.getColumnCount() - 1);
			this.model.setValueAt(Ranking.getRankByScore((String) this.model.getValueAt(i, 0),
					Integer.parseInt((String) this.model.getValueAt(i, 5))), i, 5);
		}
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
	 * @return the current column by which the table is sorted
	 */
	int getSortColumn(){
		return sortColumn;
	}
	
	/**
	 * @return all loaded statistics
	 */
	String[][] getStatistics(){
		return statistics;
	}
	
	/**
	 * @return the current displayed table
	 */
	JTable getTable(){
		return this.table;
	}

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
				new StatsCard(e, StatisticsBoard.this);

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
	void sortTableByColumn(int colIndex) {
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
