package com.tpps.ui.statisticsscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.tpps.technicalServices.util.GraphicsUtil;

public class StatisticsBoard extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private static final int VERTICAL_STRUT = 30;
	private static final int HORIZONTAL_STRUT = 50;
	private static final float BLACK_TRANSPARENCY = 0.6F;
	private static final int CELL_ROW_HEIGHT = 50;
	private BufferedImage blackBeauty;
	private final Font font = new Font("Calibri", Font.PLAIN, 15);
	private final Font headFont = new Font("Arial Black", Font.BOLD, 14);
	
	Object rowData[][] = { { "Row1-Column1", "Row1-Column2", "Row1-Column3"}, { "Row2-Column1", "Row2-Column2", "Row2-Column3"} };
	Object columnNames[] = { "nickname", "wins", "losses", "w/l ratio", "total matches" , "rank", "playtime"};
	
	public StatisticsBoard() {
		rowData = this.initRowData();
		this.setVisible(true);
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		
		this.add(this.createTable(), BorderLayout.CENTER);
		this.add(Box.createVerticalStrut(VERTICAL_STRUT),BorderLayout.PAGE_END);
		this.add(Box.createVerticalStrut(VERTICAL_STRUT), BorderLayout.PAGE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_END);
	}
	
	private String[][] initRowData(){
		String[][] data = new String[1000][7];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				data[i][j] = "Row_" + i + " Column_" + j ;
			}
		}
		return data;
	}
	
	private JScrollPane createTable(){
		this.loadImage();
		JTable table = new JTable(rowData, columnNames){
			private static final long serialVersionUID = 1L;

//			@Override
//			public void paint(Graphics g) {
//				g.drawImage(blackBeauty, 0, 0, null);
//				super.paint(g);
//			}
			
//			@Override
//			public TableCellRenderer getCellRenderer(int row, int column) {
//				return renderer;	//returns custom cell renderererer
//			}
		};
		table.setOpaque(false);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
		table.setForeground(Color.WHITE);		
		table.setFocusable(false);
		table.setEnabled(false);
		table.setFont(font);
		table.setRowHeight(CELL_ROW_HEIGHT);		
		
		JScrollPane pane = new JScrollPane(table){

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
			}
		};
		pane.setOpaque(false);
		pane.setVisible(true);
		pane.getVerticalScrollBar().setOpaque(false);
		pane.getViewport().setOpaque(false);
		pane.setBorder(BorderFactory.createEmptyBorder());
		
		JTableHeader header = table.getTableHeader();
		header.setBackground(Color.BLACK);
		header.setForeground(Color.WHITE);
		header.setEnabled(true);
		header.setFont(headFont);
		header.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		header.setReorderingAllowed(false);
		
		return pane;
	}
	
	private void loadImage(){
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, BLACK_TRANSPARENCY);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
}
