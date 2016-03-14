package com.tpps.test.application.sqlstatements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

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
	
	public void addMoreWinsOrLosses(int amount, boolean win){
		for (int i = 0; i < amount; i++) {
			SQLStatisticsHandler.addWinOrLoss(testnickname, win);
		}
	}
	


	public double RoundTo2Decimals(double val) {
		val = Math.round(val * 100);
		val = val/100;
		return val;
	}


	
	public void testWinLossRatio(){
		double ratioSQL = SQLStatisticsHandler.getWinLossRatio(testnickname);
		int wins = SQLStatisticsHandler.getWins(testnickname);
		int losses = SQLStatisticsHandler.getLosses(testnickname);
		double ratioJ = RoundTo2Decimals(((double)wins / losses));
	//	assertTrue(ratioSQL == ratioJ);
	//	System.out.println("RATIO SQL: " + ratioSQL + " --- RATIO JAVA: " + ratioJ);
		
		SQLStatisticsHandler.addWinOrLoss(testnickname, true);
		SQLStatisticsHandler.addWinOrLoss(testnickname, false);
		
		double ratioSQL2 = SQLStatisticsHandler.getWinLossRatio(testnickname);
		int wins2 = SQLStatisticsHandler.getWins(testnickname);
		int losses2 = SQLStatisticsHandler.getLosses(testnickname);
		double ratioJ2 = RoundTo2Decimals(((double)wins2 / losses2));
	//	assertTrue(ratioSQL2 == ratioJ2);
	//	System.out.println("RATIO SQL: " + ratioSQL2 + " --- RATIO JAVA: " + ratioJ2);
	}
	
	public static void main(String[] args) {
		StatisticsTest t = new StatisticsTest();
		t.setup();
	//	t.testWins();
	//	t.testLosses();
	//	t.addMoreWinsOrLosses(200, true);
		t.testWinLossRatio();
	}
}
