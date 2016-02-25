package com.tpps.loginhandling;
import java.util.Random;

public class Utilties {
	
	/**
	 * byte range: -128 to 127
	 * @param arraylength length of array
	 * @return a bytearray with random byte entries
	 */
	public static byte[] createRandomBytes(int arraylength){
		byte[] random = new byte[arraylength];
		new Random().nextBytes(random);
		return random;
	}
}
