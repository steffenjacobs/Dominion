package com.tpps.application.network.login.SQLHandling;
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
}
