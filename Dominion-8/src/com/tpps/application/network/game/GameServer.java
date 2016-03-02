package com.tpps.application.network.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.tpps.application.network.core.Server;

/** @author sjacobs - Steffen Jacobs */
public class GameServer extends Server{
	public GameServer() throws IOException{
		super(new InetSocketAddress("localhost", 1339), new ServerGamePacketHandler());
		((ServerGamePacketHandler)super.getHandler()).setServer(this);
		setConsoleInput();
	}
	public static void main(String[] args) {
		try {
			new GameServer();		
		} catch (IOException e) {
			e.printStackTrace();
		}
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