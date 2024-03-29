package com.tpps.technicalServices.util;

import java.util.LinkedHashMap;

import jaco.mp3.player.MP3Player;

/**
 * 
 *
 */
public class MyAudioPlayer {

	private static MP3Player mp3, mp32, mp33, mp34;
	private static int lastVolume, lastSoundVolume;
	private static MP3Player mainMusicPlayer, gameMusicPlayer;
	private static LinkedHashMap<MP3Player, Integer> lastVolumes;

	/**
	 * initialisiert die Player und setzt lastVolume und lastSoundVolume
	 */
	public static void init() {
		lastVolumes = new LinkedHashMap<MP3Player, Integer>();
		// MyAudioPlayer.mp3 = new MP3Player(ClassLoader.getSystemResource(""));
		MyAudioPlayer.mp32 = new MP3Player(ClassLoader.getSystemResource("resources/sounds/Click.mp3"));
		MyAudioPlayer.mp34 = new MP3Player(ClassLoader.getSystemResource("resources/sounds/Victory.mp3"));

		MyAudioPlayer.mp33 = new MP3Player(ClassLoader.getSystemResource("resources/sounds/Cash.mp3"));
		MyAudioPlayer.mainMusicPlayer = new MP3Player(ClassLoader.getSystemResource("resources/sounds/lobby.mp3"));
		MyAudioPlayer.gameMusicPlayer = new MP3Player(ClassLoader.getSystemResource("resources/sounds/mainMusic.mp3"));
		lastVolumes.put(mainMusicPlayer, new Integer(55));
	}

	/**
	 * sets the volume of the main-menu-audio
	 * 
	 * @param value
	 *            the new volume
	 */
	public static void setMainMenuVolume(int value) {
		MyAudioPlayer.mainMusicPlayer.setVolume(value);
	}

	/**
	 * sets the volume of the game-audio
	 * 
	 * @param value
	 *            the new volume
	 */
	public static void setGameVolume(int value) {
		MyAudioPlayer.gameMusicPlayer.setVolume(value);
	}

	/**
	 * sets the volume of the effects-audio
	 * 
	 * @param value
	 *            the new volume
	 */
	public static void setEffectsVolume(int value) {
		MyAudioPlayer.mp32.setVolume(value);
		MyAudioPlayer.mp33.setVolume(value);
		MyAudioPlayer.mp34.setVolume(value);
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
	 * h�llt den Player an
	 */
	public static void stop() {
		MyAudioPlayer.mp3.stop();
		// mp3 = new
		// MP3Player(ClassLoader.getSystemResource("resources/music/SovietConnection.mp3"));
		// mp3.addMP3PlayerListener(new MyMP3PlayerListener());
		MyAudioPlayer.mp3.setRepeat(true);
	}

	public static void handleGameMusic(boolean play) {
		if (play) {
			if (!MyAudioPlayer.gameMusicPlayer.isPlaying()) {
				MyAudioPlayer.gameMusicPlayer.play();
				MyAudioPlayer.gameMusicPlayer.setRepeat(true);
			}
		} else {
			MyAudioPlayer.gameMusicPlayer.pause();
		}
	}

	/**
	 * @author jhuhn
	 * @param play
	 *            true: play lobby music, false: stop lobby music
	 */
	public static void handleMainMusic(boolean play) {
		if (play) {
			if (!MyAudioPlayer.mainMusicPlayer.isPlaying()) {
				MyAudioPlayer.mainMusicPlayer.play();
				MyAudioPlayer.mainMusicPlayer.setRepeat(true);
			}
		} else {
			MyAudioPlayer.mainMusicPlayer.pause();
		}
	}

	/**
	 * spielt einen zweiten Player ab der einen PunchSound abspielt
	 */

	public static void doClick() {
		MyAudioPlayer.mp32.play();
	}

	public static void doPunch() {
		MyAudioPlayer.mp32.play();
	}

	/**
	 * spielt einen dritten Player ab der einen CashSound abspielt
	 */
	public static void doCashSound() {
		MyAudioPlayer.mp33.play();
	}

	/**
	 * spielt einen victory sound
	 * 
	 * @author nagrawal
	 */

	public static void doVictorySound() {
		MyAudioPlayer.mp34.play();
	}

	/**
	 * setzt die lautst�rke runter f�r den gew�hlten Player
	 * 
	 * @param select
	 *            der zu w�hlenden Player ("mp3"/"mp32"/"mp33")
	 */
	public static void newTurnDown(MP3Player mp3Player) {
		if (mp3Player.getVolume() >= 5) {
			mp3Player.setVolume(mp3Player.getVolume() - 5);
			lastVolumes.put(mp3Player, new Integer(mp3Player.getVolume()));
		}
	}

	/**
	 * setzt die lautst�rke runter f�r den gew�hlten Player
	 * 
	 * @param select
	 *            der zu w�hlenden Player ("mp3"/"mp32"/"mp33")
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

	public static void newTurnUp(MP3Player mp3Player) {

		if (mp3Player.getVolume() <= 95) {
			mp3Player.setVolume(mp3Player.getVolume() + 5);
			lastVolumes.put(mp3Player, new Integer(mp3Player.getVolume()));
		}

	}

	/**
	 * setzt die lautst�rke hoch f�r den gew�hlten Player
	 * 
	 * @param select
	 *            der zu w�hlenden Player ("mp3"/"mp32"/"mp33")
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

	public static void newMute(MP3Player mp3player) {

		if (mp3player.getVolume() != 0) {
			mp3player.setVolume(0);
		} else {
			if (lastVolumes.get(mp3player) == 0) {
				lastVolumes.put(mp3player, 55);
			}
			MyAudioPlayer.mp3.setVolume(lastVolumes.get(mp3player));
		}
	}

	/**
	 * setzt die lautst�rke auf Null wenn sie ungleich Null ist wenn sie gleich
	 * Null ist setzt sie die Lautst�rke auf die letzte Lautst�rke ungleich Null
	 * f�r den gew�hlten Player
	 * 
	 * @param select
	 *            der zu w�hlenden Player ("mp3"/"mp32"/"mp33")
	 */
	public static void mute(String select) {
		switch (select) {
		case "mp3":
			if (MyAudioPlayer.mp3.getVolume() != 0) {
				MyAudioPlayer.mp3.setVolume(0);
			} else {
				if (MyAudioPlayer.lastVolume == 0) {
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
				if (MyAudioPlayer.lastSoundVolume == 0) {
					MyAudioPlayer.lastSoundVolume = 55;
				}
				MyAudioPlayer.mp32.setVolume(MyAudioPlayer.lastSoundVolume);
				MyAudioPlayer.mp33.setVolume(MyAudioPlayer.lastSoundVolume);
			}
			break;

		case "mainMusicPlayer":
			if (MyAudioPlayer.mainMusicPlayer.getVolume() != 0) {
				MyAudioPlayer.mainMusicPlayer.setVolume(0);
				MyAudioPlayer.mp32.setVolume(0);
				MyAudioPlayer.mp33.setVolume(0);
			} else {
				if (MyAudioPlayer.lastSoundVolume == 0) {
					MyAudioPlayer.lastSoundVolume = 55;
				}
				MyAudioPlayer.mainMusicPlayer.setVolume(MyAudioPlayer.lastSoundVolume);
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

	// private static class MyMP3PlayerListener implements MP3PlayerListener{
	//
	// @Override
	// public void onPlay(MP3Player player) {
	//
	//
	//
	// }
	//
	// @Override
	// public void onPause(MP3Player player) {
	//
	//
	//
	// }
	//
	// @Override
	// public void onStop(MP3Player player) {
	// //player.skipForward();
	// }
	//
	// @Override
	// public void onSetVolume(MP3Player player, int volume) {
	//
	//
	// }
	//
	// @Override
	// public void onSetShuffle(MP3Player player, boolean shuffle) {
	//
	//
	// }
	//
	// @Override
	// public void onSetRepeat(MP3Player player, boolean repeat) {
	//
	//
	// }
	//
	// }
}
