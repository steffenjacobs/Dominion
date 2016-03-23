package com.tpps.ui.statisticsscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.tpps.technicalServices.util.GraphicsUtil;

public class StatisticsBoard extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private static final int VERTICAL_STRUT = 30;
	private static final int HORIZONTAL_STRUT = 50;
	private static final float BLACK_TRANSPARENCY = 0.6F;
	private BufferedImage blackBeauty;
	
	Object rowData[][] = { { "Row1-Column1", "Row1-Column2", "Row1-Column3"}, { "Row2-Column1", "Row2-Column2", "Row2-Column3"} };
	Object columnNames[] = { "Column One", "Column Two", "Column Three"};
	
	public StatisticsBoard() {
		this.setVisible(true);
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		
		this.add(this.createTable(), BorderLayout.CENTER);
		this.add(Box.createVerticalStrut(VERTICAL_STRUT),BorderLayout.PAGE_END);
		this.add(Box.createVerticalStrut(VERTICAL_STRUT), BorderLayout.PAGE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_END);
	}
	
	private JTable createTable(){
		this.loadImage();
		JTable table = new JTable(rowData, columnNames){
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
			}
		};
		table.setOpaque(false);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
		table.setForeground(Color.WHITE);
		table.setFocusable(false);

//		JTable in scrollpane, set opaque false ;)		
//		scrollPane.setOpaque(false);
//		scrollPane.getViewport().setOpaque(false);
		return table;
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
