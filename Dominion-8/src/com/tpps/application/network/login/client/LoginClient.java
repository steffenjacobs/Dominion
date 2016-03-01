package com.tpps.application.network.login.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import javax.swing.JOptionPane;

import com.tpps.application.network.clientSession.client.SessionClient;
import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.login.SQLHandling.Password;
import com.tpps.application.network.login.packets.PacketLoginCheckAnswer;
import com.tpps.application.network.login.packets.PacketLoginCheckRequest;
import com.tpps.application.network.login.packets.PacketRegisterAnswer;
import com.tpps.application.network.login.packets.PacketRegisterRequest;
import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;

public class LoginClient extends PacketHandler {

	Client c_login;
	UUID sessionid;
	String username;
	Client c_session;
	
	public LoginClient() {
		try {
			c_login = new Client(new InetSocketAddress("127.0.0.1", 1338), this);
			c_session = new Client(new InetSocketAddress("127.0.0.1", 1337), this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handlelogin(String nickname, String plaintext){
		this.username = nickname;
		Password pw = new Password(plaintext, new String("defsalt").getBytes()); //defsalt ist standartsalt
		try {
			System.out.println("into handlelogin");
		//	pw.createHashedPassword();
			String pwAsString = pw.getHashedPasswordAsString();
			PacketLoginCheckRequest check = new PacketLoginCheckRequest(nickname, pwAsString);
			c_login.sendMessage(PacketType.getBytes(check));
			System.out.println("ende handlelogin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleReceivedPacket(int port, byte[] bytes) {
		System.out.println("into hanleReceivedPacket");
		Packet answer = PacketType.getPacket(bytes);
		switch(answer.getType()){
		case LOGIN_CHECK_ANSWER: 
			PacketLoginCheckAnswer check = (PacketLoginCheckAnswer) answer;
			if(check.getState()){	//Anmeldung erfolgreich, pw richtig
				this.sessionid = check.getSessionID();
				SessionClient.keepAlive(c_session, username, true);
				JOptionPane.showMessageDialog(null, "You logged in successfully", "Login", JOptionPane.INFORMATION_MESSAGE);
			}else{//Anmeldung fehlgeschlagen, PW falsch
				JOptionPane.showMessageDialog(null, "Wrong Password or nickname", "Login", JOptionPane.ERROR_MESSAGE);
			}
			break;
		case LOGIN_REGISTER_ANSWER:
			PacketRegisterAnswer check2 = (PacketRegisterAnswer) answer;
			if(check2.getState() == 1){
				JOptionPane.showMessageDialog(null, "Account created succesfully", "Create Account", JOptionPane.INFORMATION_MESSAGE);
			}else if(check2.getState() == 2){
				JOptionPane.showMessageDialog(null, "Nickname already in use", "Create Account", JOptionPane.ERROR_MESSAGE);
			}else{
				JOptionPane.showMessageDialog(null, "EMAIL already in use", "Create Account", JOptionPane.ERROR_MESSAGE);
			}
		default: break;
		}
		System.out.println("ende hanleReceivedPacket");
	}
	
	//sends accountdetails to server to create new account
	public void handleAccountCreation(String username, String plaintext, String email ){
		Password pw = new Password(plaintext, new String("defsalt").getBytes());
		 PacketRegisterRequest packet = new PacketRegisterRequest(username, pw.getHashedPasswordAsString(), email);
		 try {
			c_login.sendMessage(PacketType.getBytes(packet));
		} catch (IOException e) {		
			e.printStackTrace();
		}
		 System.out.println("client sent new Accountinformaion to server");
	}
	

//	public static void main(String[] args) {
//		new LoginClient().handlelogin("Alex44", "Schokolade");;
//	}

}
