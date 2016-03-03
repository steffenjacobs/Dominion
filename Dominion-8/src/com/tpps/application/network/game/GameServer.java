package com.tpps.application.network.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.tpps.application.game.GameController;
import com.tpps.application.game.Player;
import com.tpps.application.network.core.Server;

/** @author ladler - Lukas Adler */
public class GameServer extends Server{
	
	private GameController gameController;
	
	public GameServer() throws IOException{
		super(new InetSocketAddress("0.0.0.0", 1339), new ServerGamePacketHandler());
		((ServerGamePacketHandler)super.getHandler()).setServer(this);
		this.gameController = new GameController();
//		has to be removed
		this.gameController.setActivePlayer(new Player());
		setConsoleInput();		
	}
	
	public static void main(String[] args) {
		try {
			new GameServer();		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	public GameController getGameController() {
		return this.gameController;
	}

	/**
	 * sets up the console-input
	 * 
	 * @author sjacobs - Steffen Jacobs
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
		while (true) {			
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
					System.out.println("------------------------------------");
				} else {
					System.out.println("Bad command: " + line);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Bad syntax.");
			}
		}
		scanInput.close();
	}
	
}