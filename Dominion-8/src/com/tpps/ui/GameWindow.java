package com.tpps.ui;

import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.tpps.ui.components.GFButton;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = -5389003835573453281L;

	public static void main(String[] args) throws IOException {
		new GameWindow();
	}

	Container c;
	JButton button;
	private GraphicFramework framework;

	public GameWindow() throws IOException {
		c = this.getContentPane();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(500, 300);
		this.setVisible(true);
		framework = new GraphicFramework();
		this.add(framework);
		BufferedImage im = ImageIO.read(getClass().getClassLoader().getResourceAsStream("ressources/img/gameObjects/testButton.png"));
		framework.addComponent(new GFButton(50, 50, 5, im, framework));

		this.revalidate();
		this.repaint();
	}

}
