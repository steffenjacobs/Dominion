package com.tpps.application.network.login.server;

import com.tpps.application.network.login.SQLHandling.Password;
import com.tpps.application.network.login.SQLHandling.SQLHandler;
import com.tpps.application.network.login.SQLHandling.SQLOperations;

public class testDBLogin {
	
	public static void main(String[] args) {
//		String hostname = "localhost";
//		String port = "3306";
//		String database = "accountmanager";
//		String user = "jojo";
//		String password = "password";
//		SQLHandler sql = new SQLHandler(hostname, port, user, password, database);
//		SQLOperations op = new SQLOperations(sql);
//		
//		String plaintext1 = "Schokolade";
//		String plaintext2 = "abcdef";
//		String plaintext3 = "yoloswag2k16";
//		
//		Password pw1 = new Password(plaintext1, new String("defsalt").getBytes());
//		Password pw2 = new Password(plaintext2, new String("defsalt").getBytes());
//		Password pw3 = new Password(plaintext3, new String("defsalt").getBytes());
//		
//		String hashedpw1 = pw1.getHashedPasswordAsString();
//		String hashedpw2 = pw2.getHashedPasswordAsString();
//		String hashedpw3 = pw3.getHashedPasswordAsString();
//		
//		System.out.println("hashedpw1: " + hashedpw1 + "\n" + "hashedpw2: " + hashedpw2 + "\n" + "hashedpw3: " + hashedpw3);
//		System.out.println("----------------------------");
//		
//		Password hw1 = new Password(hashedpw1);
//		String salt1 = hw1.getSaltAsString();
//		String fullhashed1 = hw1.getHashedPasswordAsString();
//		
//		Password hw2 = new Password(hashedpw2);
//		String salt2 = hw2.getSaltAsString();
//		String fullhashed2 = hw2.getHashedPasswordAsString();
//		
//		Password hw3 = new Password(hashedpw3);
//		String salt3 = hw3.getSaltAsString();
//		String fullhashed3 = hw3.getHashedPasswordAsString();
//		System.out.println("salt1: " + salt1 + "\n" + "salt2: " + salt2 + "\n" + "salt3: " + salt3);
//		System.out.println("d_hashedpw1: " + fullhashed1 + "\n" + "d_hashedpw2: " + fullhashed2 + "\n" + "d_hashedpw3: " + fullhashed3);
		
		//UPDATE accountdetails SET salt_hashed_pw = 'yolo' WHERE nickname = 'kevinS';
		//String salt = sql.getSaltForLogin(pac.getUsername());			
		//Password pw = new Password(pac.getHashedPW(), salt.getBytes());
		
	}
}
