package com.tpps.technicalServices.network.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.game.GameController;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.core.ServerConnectionThread;

/** @author ladler - Lukas Adler */
public class GameServer extends Server{
	
	
	private static int CLIENT_ID;

	private GameController gameController;
	private static GameServer instance;
	private boolean flag;
	
	public GameServer() throws IOException{
		super(new InetSocketAddress("0.0.0.0", 1339), new ServerGamePacketHandler());
		((ServerGamePacketHandler)super.getHandler()).setServer(this);
		this.gameController = new GameController();
		this.flag = true;
		instance = this;
		setConsoleInput();		
	}
	
	
	/**
	 * 
	 * @return an instance of the GameServer
	 */
	public static GameServer getInstance() {
		return instance;
	}
	
	public void newGame() {
		this.flag = false;		
		this.clients = new ConcurrentHashMap<>();
		this.gameController = new GameController();
		this.flag = true;
		setConsoleInput();
	}



	public static int getCLIENT_ID() {
		return CLIENT_ID++;
	}
	
	public static void main(String[] args) {
		try {
			new GameServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	public synchronized GameController getGameController() {
		return this.gameController;
	}

	/**
	 * sets up the console-input
	 * 
	 * @author Steffen Jacobs
	 */
	private void setConsoleInput() {
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("* * * * * Dominion Game Server - Team ++; * * * * *");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println();
		System.out.println("Enter 'help' to see all available commands.");
		System.out.println();
		
		String line = null;
		Scanner scanInput = new Scanner(System.in);
		while (flag) {			
			line = scanInput.nextLine();
			try {
				if (line.equals("exit")) {
					System.exit(0);
					break;
				} else if (line.startsWith("help")) {
					System.out.println("-------- Available Commands --------");
//					
					System.out.println("help");
					System.out.println("exit");
					System.out.println("newGame");
					System.out.println("------------------------------------");
				} else if (line.startsWith("newGame")){
					System.out.println("Starting a new game please connect again to the server.");
					
					newGame();
					
					
				}else {
					System.out.println("Bad command: " + line);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Bad syntax.");
			}
		}
		scanInput.close();
	}
	
}