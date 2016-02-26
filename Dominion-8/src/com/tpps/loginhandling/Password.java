package com.tpps.loginhandling;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Password {
	
	private String plaintext;
	private byte[] salt;
	private byte[] hashedPassword;
	
	/**
	 * creates salt & hashed password for a given plaintext
	 * @param plaintext password in clear characters
	 */
	public Password(String plaintext){
		this.plaintext = plaintext;
		this.salt = this.generateSalt();
		try {
			this.hashedPassword = this.createHashedPassword();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * creates the hashed password for given plaintext and salt
	 * @param plaintext password in clear characters
	 * @param salt	
	 */
	public Password(String plaintext, byte[] salt){
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

//	public Password(String plaintext, byte[] salt){
//		this.plaintext = plaintext;
//		this.salt = salt;
//	}

//	public Password(String plaintext, byte[] salt, byte[] hashedPassword){
//		this.plaintext = plaintext;
//		this.salt = salt;
//		this.hashedPassword = hashedPassword;
//	}
	
	public byte[] generateSalt(){
		return com.tpps.loginhandling.Utilties.createRandomBytes(8);
	}
	
	public byte[] createHashedPassword() throws Exception{
		PBEKeySpec keySpec = new PBEKeySpec(this.plaintext.toCharArray(), this.salt, 1000, 256);	//TODO: 1000 is Iterations, 256 is KeyLength
		try {
			SecretKeyFactory fac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			return fac.generateSecret(keySpec).getEncoded();
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("Error while creating a password: " + e.getMessage());					
		} catch (InvalidKeySpecException e) {
			throw new Exception("Error while creating a password: " + e.getMessage());
		} finally {
			keySpec.clearPassword();
		}
	}
	
	public boolean SameHashedPassword(byte[] externalHashedPassword){
		return Arrays.equals(this.hashedPassword, externalHashedPassword); 
	}

	public String getPlaintext() {
		return plaintext;
	}

	public void setPlaintext(String plaintext) {
		this.plaintext = plaintext;
	}

	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	public byte[] getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(byte[] hashedPassword) {
		this.hashedPassword = hashedPassword;
	}
	
	public String getSaltAsString(){		
		return new String(this.salt);
	}
	
	public String getHashedPasswordAsString(){
		return new String(this.hashedPassword);
	}	
}
