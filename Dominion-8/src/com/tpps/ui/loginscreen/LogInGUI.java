package com.tpps.ui.loginscreen;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

//TODO: underline Header, set Background, set transparent, map 'RETURN' key to Login
public class LogInGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private Container c;
	private JButton execute;
	private JButton cancel;
	
	private JTextField userinfo;
	private JPasswordField passwordbox;
	private JLabel[] description;
	private JLabel header;
	private JPanel[] panels;
	private Font smallfont;
	
	public LogInGUI(){	
		width = 500;
		height = 280;
		importFont();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		this.init();
		this.createpanel1();
		this.createpanel2();
		this.createpanel3();
		this.createpanel4();
		this.revalidate();
	}
	private void init(){
		c = this.getContentPane();
		c.setBackground(Color.white);
		c.setLayout(new GridLayout(4, 1,0,30));
		this.setSize(width, height);
		this.setLocationRelativeTo(null);
		this.setTitle("Log in !");
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		smallfont = new Font("Calibri", Font.PLAIN, 19);
		
		description = new JLabel[2];
		panels = new JPanel[4];
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new JPanel(new FlowLayout());			
		}
	}
	
	//not working why? Path checking!!
	private void importFont(){
		try {
		     GraphicsEnvironment ge = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
//		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/font/xenippa1.TTF")));
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File( System.getProperty("user.dir"), "src/resources/font/xenippa1.TTF")));
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	private void createpanel1(){
		header = new JLabel();
		header.setText("Type in Userinformation");	
		header.setFont(new Font("xenippa1", Font.PLAIN, 20));
		
		panels[0].add(header);
		panels[0].setOpaque(false);
	//	panels[0].setBorder(BorderFactory.createLineBorder(Color.GREEN, 4));
		c.add(panels[0]);
	}
	
	private void createpanel2(){
		panels[1].setLayout(new GridLayout(1, 2));
		
		description[0] = new JLabel("Accountname: ");
		description[0].setFont(smallfont);
		description[0].setHorizontalAlignment(JLabel.CENTER);
		userinfo = new JTextField();
		userinfo.setFont(smallfont);
		userinfo.setOpaque(false);
		panels[1].add(description[0]);
		panels[1].add(userinfo);
		panels[1].setOpaque(false);
	//	panels[1].setBorder(BorderFactory.createLineBorder(Color.RED, 4));
		c.add(panels[1]);
		panels[0].revalidate();
	}
	
	private void createpanel3(){
		panels[2].setLayout(new GridLayout(1, 2));
		
		description[1] = new JLabel("Password: ");
		description[1].setFont(smallfont);
		description[1].setHorizontalAlignment(JLabel.CENTER);
		passwordbox = new JPasswordField();
		passwordbox.setOpaque(false);
		panels[2].add(description[1]);
		panels[2].add(passwordbox);
		panels[2].setOpaque(false);
	//	panels[2].setBorder(BorderFactory.createLineBorder(Color.CYAN, 4));
		c.add(panels[2]);
		panels[2].revalidate();		
	}
	
	private void createpanel4(){
		panels[3].setLayout(new FlowLayout(FlowLayout.CENTER, 80, 0));
	//	panels[3].setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
		
		execute = new JButton("Log In");
		execute.setPreferredSize(new Dimension(100,30));
		cancel = new JButton("Cancel");
		cancel.setPreferredSize(new Dimension(100,30));
		panels[3].add(execute);
		panels[3].add(cancel);
		panels[3].setOpaque(false);
		c.add(panels[3]);
		panels[3].revalidate();
	}
	
	public static void main(String[] args)  {		
		new LogInGUI();
	}
}
