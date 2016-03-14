package com.tpps.test.application.sqlstatements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tpps.application.network.login.SQLHandling.SQLHandler;
import com.tpps.application.network.login.SQLHandling.SQLOperations;
import com.tpps.application.network.login.SQLHandling.SQLStatisticsHandler;
import com.tpps.application.network.login.SQLHandling.Utilties;

public class StatisticsTest {

	private static final String testnickname = "test";
	
	public void setup(){
		String hostname = "localhost";
		String port = "3306";
		String database = "accountmanager";
		String user = "jojo";
		String password = "password";
		SQLHandler.init(hostname, port, user, password, database);
		
		if (!SQLOperations.checkTable("accountdetails")) {
			SQLOperations.createAccountdetailsTable();
		}
		if(!SQLOperations.checkTable("statistics")){
			SQLStatisticsHandler.createStatisticsTable(Utilties.createStatisticsList());
		}
		
		try {
			PreparedStatement stmt = SQLHandler.getConnection().prepareStatement("SELECT * FROM statistics WHERE nickname = '" +testnickname + "'");
			ResultSet rs = stmt.executeQuery();
			if(rs.next() == false){
				SQLStatisticsHandler.insertRowForFirstLogin(testnickname);
			}
		} catch (SQLException e) {		
			e.printStackTrace();
		}
	}
	
	public void testWins(){
		int actualwins = SQLStatisticsHandler.getWins(testnickname);
		SQLStatisticsHandler.addWinOrLoss(testnickname, true);
		int newwins = SQLStatisticsHandler.getWins(testnickname);
		assertEquals(actualwins, newwins -1);		
	}
	
	public void testLosses(){
		int actuallosses = SQLStatisticsHandler.getLosses(testnickname);
		SQLStatisticsHandler.addWinOrLoss(testnickname, false);
		int newlosses = SQLStatisticsHandler.getLosses(testnickname);
		assertEquals(actuallosses, newlosses -1);
	}
	
	public void addMoreWinOrLosses(int amount, boolean win){
		for (int i = 0; i < amount; i++) {
			SQLStatisticsHandler.addWinOrLoss(testnickname, win);
		}
	}
	
	public static void main(String[] args) {
		StatisticsTest t = new StatisticsTest();
		t.setup();
	//	t.testWins();
	//	t.testLosses();
		t.addMoreWinOrLosses(200, true);
	}
}
