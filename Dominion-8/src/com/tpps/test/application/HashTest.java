package com.tpps.test.application;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

/**
 * JUnit-Test for understanding MessageDigest-class
 * @author Steffen Jacobs
 *
 */
public class HashTest {

	/**
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void test() throws UnsupportedEncodingException, NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String text = "Fischi";

		md.update(text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
		byte[] digest = md.digest();

		String res = String.format("%064x", new java.math.BigInteger(1, digest));
		System.out.println(res);
		final String preCalculated = "480a032c0a49eeaccc43b40bab41281f91bc4eae226c9d6a97ea73729ea86496";
		
		assertEquals(preCalculated, res);
	}

}
