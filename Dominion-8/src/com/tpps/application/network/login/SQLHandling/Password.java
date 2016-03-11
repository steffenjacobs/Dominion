package com.tpps.application.network.login.SQLHandling;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * 
 * @author jhuhn - Johannes Huhn
 * This class delivers all functionalities that is needed to create or validate an used account
 * This classes uses  hashing
 */
public class Password {
	
	private String plaintext;
	private String salt;
	private String hashedPassword;
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * creates salt & hashed password for a given plaintext
	 * @param plaintext password in clear characters
	 */
	public Password(String plaintext){
		this.plaintext = plaintext;
		byte[] saltAsByte = this.generateSalt();
		this.salt = String.format("%08x", new java.math.BigInteger(1, saltAsByte));
		System.out.println("salt: " + this.salt);
		try {
			this.hashedPassword = this.createHashedPassword();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * creates the hashed password for given plaintext and salt
	 * @param plaintext password in clear characters
	 * @param salt	
	 */
	public Password(String plaintext, String salt){
		this.plaintext = plaintext;
		this.salt = salt;
		try {
			this.hashedPassword = this.createHashedPassword();
		} catch (Exception e) {		
			e.printStackTrace();
		}
	}
//	
//	public Password(String plaintext){
//		this.plaintext = plaintext;
//	}
//	
//	public Password(String plaintext, byte[] salt){
//		this.plaintext = plaintext;
//		this.salt = salt;
//	}
//	
//	public Password(String plaintext, byte[] salt, byte[] hashedPassword){
//		this.plaintext = plaintext;
//		this.salt = salt;
//		this.hashedPassword = hashedPassword;
//	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * Creates random bytes requires to generate a salt
	 * @return a byte array of a random generated salt value
	 */
	public byte[] generateSalt(){
		return com.tpps.application.network.login.SQLHandling.Utilties.createRandomBytes(8);
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * This method generates with the plaintext and a salt a hashed value for security reasons
	 * @return a byte array of unique created hash
	 * @throws Exception
	 */
	public String createHashedPassword() throws Exception{
//		PBEKeySpec keySpec = new PBEKeySpec(this.plaintext.toCharArray(), this.salt, 1000, 256);	//1000 is Iterations, 256 is KeyLength
//		try {
//			SecretKeyFactory fac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//			return fac.generateSecret(keySpec).getEncoded();
//		} catch (NoSuchAlgorithmException e) {
//			throw new Exception("Error while creating a password: " + e.getMessage());					
//		} catch (InvalidKeySpecException e) {
//			throw new Exception("Error while creating a password: " + e.getMessage());
//		} finally {
//			keySpec.clearPassword();
//		}
		

		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.update(this.plaintext.getBytes("UTF-8")); // Change this to "UTF-16" if needed
		byte[] digest = md.digest();

		String res = String.format("%064x", new java.math.BigInteger(1, digest));
		return res;
		
//		MessageDigest md = MessageDigest.getInstance("SHA-256");
//		String text = "This is some text";
//
//		md.update(text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
//		byte[] digest = md.digest();
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * @param externalHashedPassword byte array that should be compared with the hashed value of this object
	 * @return true, if the hashes are equal, false, else
	 */
	public boolean SameHashedPassword(String externalHashedPassword){
		return this.salt.trim().equals(externalHashedPassword.trim()); 
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * @return a String representation of the password as a plaintext 
	 */
	public String getPlaintext() {
		return plaintext;
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * sets the plaintext (password)
	 * @param plaintext String representation of the password to set
	 */
	public void setPlaintext(String plaintext) {
		this.plaintext = plaintext;
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * @return a byte array representaion of the used salt
	 */
	public String getSalt() {
		return salt;
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * sets the salt
	 * @param salt to set
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * @return the hashed value of the password
	 */
	public String getHashedPassword() {
		return hashedPassword;
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * @param hashedPassword sets the hashed password
	 */
	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * @return a String representaion of the salt
	 */
	public String getSaltAsString(){
		return new String(this.salt);
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * used to write the hash in the database
	 * @return a String representation of the hashed value
	 */
	public String getHashedPasswordAsString(){
		return new String(this.hashedPassword);
	}	
}
