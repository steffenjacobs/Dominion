package com.tpps.technicalServices.util;

import jaco.mp3.player.MP3Player;
public class MyAudioPlayer {

	private static MP3Player mp3, mp32, mp33;
	private static int lastVolume, lastSoundVolume;

	
	/**
	 * initialisiert die Player und setzt lastVolume und lastSoundVolume
	 */
	public static void init() {		
//		MyAudioPlayer.mp3 = new MP3Player(ClassLoader.getSystemResource(""));
		System.out.println(ClassLoader.getSystemResource("resources"));
		MyAudioPlayer.mp32 = new MP3Player(
				ClassLoader.getSystemResource("resources/sounds/Click.mp3"));
//		MyAudioPlayer.mp33 = new MP3Player(ClassLoader.getSystemResource(""));		
		MyAudioPlayer.lastVolume = 55;
		MyAudioPlayer.lastSoundVolume = 55;
	}

	/**
	 * startet die HintergrundMusik und setzt den Player auf wiederholen
	 */
	public static void play() {
		if (!MyAudioPlayer.mp3.isPlaying()) {				
			MyAudioPlayer.mp3.play();			
			MyAudioPlayer.mp3.setRepeat(true);
		}
	}

	/**
	 * pausiert den Player
	 */
	public static void pause() {
		MyAudioPlayer.mp3.pause();

	}
	
	/**
	 * hällt den Player an
	 */
	public static void stop(){		
		MyAudioPlayer.mp3.stop();			
//		mp3 = new MP3Player(ClassLoader.getSystemResource("resources/music/SovietConnection.mp3"));
//		mp3.addMP3PlayerListener(new MyMP3PlayerListener());
		MyAudioPlayer.mp3.setRepeat(true);
				
	}

	/**
	 * spielt einen zweiten Player ab der einen PunchSound abspielt
	 */
	public static void doClick() {
		MyAudioPlayer.mp32.play();		
	}
	
	/**
	 * spielt einen dritten Player ab der einen CashSound abspielt
	 */
	public static void doCashSound(){
		MyAudioPlayer.mp33.play();
	}

	/**
	 * setzt die lautstärke runter für den gewählten Player
	 * @param select der zu wählenden Player ("mp3"/"mp32"/"mp33")
	 */
	public static void turnDown(String select) {
		switch (select) {
		case "mp3":
			if (MyAudioPlayer.mp3.getVolume() >= 5) {
				MyAudioPlayer.mp3.setVolume(MyAudioPlayer.mp3.getVolume() - 5);
				MyAudioPlayer.lastVolume = MyAudioPlayer.mp3.getVolume();
			}
			break;
		case "mp32":
			if (MyAudioPlayer.mp32.getVolume() >= 5) {
				MyAudioPlayer.mp32.setVolume(MyAudioPlayer.mp32.getVolume() - 5);
				MyAudioPlayer.mp33.setVolume(MyAudioPlayer.mp33.getVolume() - 5);
				MyAudioPlayer.lastSoundVolume = MyAudioPlayer.mp32.getVolume();
			}
			break;
		}
	}

	
	/**
	 * setzt die lautstärke hoch für den gewählten Player
	 * @param select der zu wählenden Player ("mp3"/"mp32"/"mp33")
	 */
	public static void turnUp(String select) {
		switch (select) {
		case "mp3":
			if (MyAudioPlayer.mp3.getVolume() <= 95) {
				MyAudioPlayer.mp3.setVolume(MyAudioPlayer.mp3.getVolume() + 5);
				MyAudioPlayer.lastVolume = MyAudioPlayer.mp3.getVolume();
			}
			break;
		case "mp32":
			if (MyAudioPlayer.mp32.getVolume() <= 95) {
				MyAudioPlayer.mp32.setVolume(MyAudioPlayer.mp32.getVolume() + 5);
				MyAudioPlayer.mp33.setVolume(MyAudioPlayer.mp33.getVolume() + 5);
				MyAudioPlayer.lastSoundVolume = MyAudioPlayer.mp32.getVolume();
			}
			break;
		}
	}
	
	/**
	 * setzt die lautstärke auf Null wenn sie ungleich Null ist wenn sie gleich Null ist
	 * setzt sie die Lautstärke auf die letzte Lautstärke ungleich Null für den gewählten Player
	 * @param select der zu wählenden Player ("mp3"/"mp32"/"mp33")
	 */
	public static void mute(String select) {
		switch (select) {
		case "mp3":
			if (MyAudioPlayer.mp3.getVolume() != 0) {
				MyAudioPlayer.mp3.setVolume(0);
			} else {
				if (MyAudioPlayer.lastVolume == 0){
					MyAudioPlayer.lastVolume = 55;
				}
				MyAudioPlayer.mp3.setVolume(MyAudioPlayer.lastVolume);
			}
			break;
		case "mp32":
			if (MyAudioPlayer.mp32.getVolume() != 0) {
				MyAudioPlayer.mp32.setVolume(0);
				MyAudioPlayer.mp33.setVolume(0);
			} else {
				if (MyAudioPlayer.lastSoundVolume == 0){
					MyAudioPlayer.lastSoundVolume = 55;
				}
				MyAudioPlayer.mp32.setVolume(MyAudioPlayer.lastSoundVolume);
				MyAudioPlayer.mp33.setVolume(MyAudioPlayer.lastSoundVolume);
			}
			break;
		}
	}

	public static int getVolume(String select) {
		switch (select) {
		case "mp3":
			return MyAudioPlayer.mp3.getVolume();
		case "mp32":
			return MyAudioPlayer.mp32.getVolume();
		default:
			return -1;
		}
	}
	
//	private static class MyMP3PlayerListener implements MP3PlayerListener{
//
//		@Override
//		public void onPlay(MP3Player player) {
//
//
//			
//		}
//
//		@Override
//		public void onPause(MP3Player player) {
//
//
//			
//		}
//
//		@Override
//		public void onStop(MP3Player player) {
//			//player.skipForward();			
//		}
//
//		@Override
//		public void onSetVolume(MP3Player player, int volume) {
//			
//			
//		}
//
//		@Override
//		public void onSetShuffle(MP3Player player, boolean shuffle) {
//			
//			
//		}
//
//		@Override
//		public void onSetRepeat(MP3Player player, boolean repeat) {
//			
//			
//		}
//		
//	}
}
