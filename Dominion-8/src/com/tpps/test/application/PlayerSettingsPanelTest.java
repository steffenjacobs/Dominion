package com.tpps.test.application;

import static org.junit.Assert.*;

import org.junit.Test;

import com.tpps.ui.lobbyscreen.PlayerSettingsPanel;
import com.tpps.ui.lobbyscreen.SearchingField;

/**
 * created this small junit test, because the "insertPlayer" method had a rare
 * bug
 * 
 * @author jhuhn
 *
 */
public class PlayerSettingsPanelTest {
	
	PlayerSettingsPanel[] settingsPanelGui = new PlayerSettingsPanel[10];
	String[][] playerNames = new String[10][4];
	
	/**
	 * init arrays
	 */
	public void setup(){
		for (int i = 0; i < playerNames.length; i++) {	
			settingsPanelGui[i] = new PlayerSettingsPanel(true);
			for (int j = 0; j < 4; j++) {
				playerNames[i][j] = "testName_" + i + j;
				settingsPanelGui[i].insertPlayer(playerNames[i][j]);
			}			
		}
		System.out.println("finished setup");
	}

	/**
	 * compares the teststrings
	 */
	@Test
	public void test() {
		this.setup();
		for (int i = 0; i < playerNames.length; i++) {
			SearchingField[] fields = settingsPanelGui[i].getConnectedPlayers();
			for (int j = 0; j < fields.length; j++) {
				assertEquals(playerNames[i][j], fields[j].getText());
			}
		}
	}

}
