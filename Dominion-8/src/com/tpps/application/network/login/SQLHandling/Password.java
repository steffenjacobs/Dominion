package com.tpps.application.network.login.SQLHandling;

import java.security.MessageDigest;

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
	 * @return a String of unique created hash in UTF-8
	 * @throws Exception
	 */
	public String createHashedPassword() throws Exception{
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		//String pw = this.plaintext + this.salt;
		md.update((this.plaintext + this.salt).getBytes("UTF-8")); // Change this to "UTF-16" if needed
		byte[] digest = md.digest();

		return String.format("%064x", new java.math.BigInteger(1, digest));
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * @param externalHashedPassword a String that should be compared with the hashed value of this object
	 * @return true, if the hashes are equal, false else
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
	 * @return a String representaion of the used salt
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
}
