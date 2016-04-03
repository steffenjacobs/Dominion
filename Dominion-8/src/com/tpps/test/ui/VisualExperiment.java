package com.tpps.test.ui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class VisualExperiment extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea jtext;
	private JScrollPane pain;

	public static void main(String[] args) throws InterruptedException {
		new VisualExperiment();
	}

	public VisualExperiment() throws InterruptedException {
		final int WIDTH = 1280, HEIGHT = 720;

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(WIDTH, HEIGHT);
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setVisible(true);

		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

		JPanel gayPanel = new JPanel();

		gayPanel.setLayout(new GridLayout(1, 1));

		jtext = new JTextArea();

		pain = new JScrollPane(jtext);
		gayPanel.add(pain);

		this.add(gayPanel);

		this.revalidate();

		new Thread(() -> {
			for (int i = 0; i < 10000; i++) {
				VisualExperiment.this.appendChat("TestString " + i + "\n");
			}
		}).start();
	}

	public synchronized void appendChat(String str) {
		this.jtext.append(str);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.pain.getVerticalScrollBar().setValue(this.pain.getVerticalScrollBar().getMaximum());
	}
}