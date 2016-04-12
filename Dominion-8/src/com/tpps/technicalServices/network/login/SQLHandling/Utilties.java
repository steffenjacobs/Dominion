package com.tpps.technicalServices.network.login.SQLHandling;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author jhuhn - Johannes Huhn
 * This class is for basic utilities
 */
public class Utilties {
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * byte range: -128 to 127
	 * @param arraylength length of the created array
	 * @return a bytearray with random byte entries
	 */
	public static byte[] createRandomBytes(int arraylength){
		byte[] random = new byte[arraylength];
		new Random().nextBytes(random);
		return random;
	}
	
	
	public static ArrayList<Statistic> createStatisticsList(){
		Statistic two = new Statistic(SQLType.INT, "wins");
		Statistic tree = new Statistic(SQLType.INT, "losses");
		Statistic four = new Statistic(SQLType.FLOAT, "4,2", "win_loss");
		Statistic five = new Statistic(SQLType.INT, "games_played");
		Statistic six = new Statistic(SQLType.TEXT, "rank");
		Statistic seven = new Statistic(SQLType.BIGINT, "playtime");
		Statistic eight = new Statistic(SQLType.VARCHAR, "200", "LAST_TIME_PLAYED");
		Statistic nine = new Statistic(SQLType.VARCHAR, "200", "LAST_TIME_WINS");
		ArrayList<Statistic> statistics = new ArrayList<Statistic>();
		statistics.add(two);
		statistics.add(tree);
		statistics.add(four);
		statistics.add(five);
		statistics.add(six);
		statistics.add(seven);
		statistics.add(eight);
		statistics.add(nine);
		return statistics;
	}
}
