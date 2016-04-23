package com.tpps.technicalServices.network.chat.server;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ColorPool {
	
	public ConcurrentHashMap<String, Color> globalChat = new ConcurrentHashMap<String, Color>();
	
	public Color selectRandomColor(){
		switch(getRandomNumber(8, 1)){
		case 1: return Color.BLUE;
		case 2: return Color.YELLOW;
		case 3: return Color.CYAN;
		case 4: return Color.MAGENTA;
		case 5: return Color.ORANGE;
		case 6: return new Color(0, 255, 255); //babyblue
		case 7: return new Color(128, 255, 0); //interesting green
		case 8: return new Color(192, 192, 192); //lightgrey
		}
		return Color.WHITE;
	}
	
	/**
	 * including min and max
	 * @param max
	 * @param min
	 * @return
	 */
	public static int getRandomNumber(int max, int min){
		return new Random().nextInt(max - min + 1) + min;
	}
	
	public void addUserToGlobalChat_COLOR(String user){
		this.globalChat.putIfAbsent(user, this.selectRandomColor());
	}
	
	public void deleteUserFromGlobalChat_COLOR(String user){
		this.globalChat.remove(user);
	}
	
	public Color getUserColor(String user){
		if(this.globalChat.containsKey(user)){
			return this.globalChat.get(user);
		}else{
			this.addUserToGlobalChat_COLOR(user);
			return this.globalChat.get(user);
		}
	}
}
