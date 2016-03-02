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
import com.tpps.ui.loginscreen.LoginGUIController;

public class LoginClient extends PacketHandler {

	Client c_login;
	UUID sessionid;
	String usernamelogin;
	SessionClient c_session;
	LoginGUIController guicontroller;

	String usernamenewacc;
	String plaintext;

	public LoginClient(LoginGUIController guicontroller) {
		try {
			this.guicontroller = guicontroller;
			c_login = new Client(new InetSocketAddress("127.0.0.1", 1338), this);
			c_session = new SessionClient(new InetSocketAddress("127.0.0.1", 1337));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handlelogin(String nickname, String plaintext) {
		this.usernamelogin = nickname;
		Password pw = new Password(plaintext, new String("defsalt").getBytes()); // defsalt ist standardsalt
																					
		try {
			System.out.println("into handlelogin");
			// pw.createHashedPassword();
			String pwAsString = pw.getHashedPasswordAsString();
			PacketLoginCheckRequest check = new PacketLoginCheckRequest(nickname, pwAsString);
			c_login.sendMessage(check);
			System.out.println("ende handlelogin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleReceivedPacket(int port, Packet answer) {
		System.out.println("into hanleReceivedPacket");
		switch (answer.getType()) {
		case LOGIN_CHECK_ANSWER:
			PacketLoginCheckAnswer check = (PacketLoginCheckAnswer) answer;
			guicontroller.getStateOfLoginRequest(check.getState());
			if (check.getState()) { // Anmeldung erfolgreich, pw richtig
				this.sessionid = check.getSessionID();
				c_session.keepAlive(usernamelogin, true);
			}
			break;
		case LOGIN_REGISTER_ANSWER:
			PacketRegisterAnswer check2 = (PacketRegisterAnswer) answer;
			guicontroller.getStateOfAccountCreation(check2.getState(),this.usernamenewacc, this.plaintext);
		default:
			break;
		}
		System.out.println("ende hanleReceivedPacket");
	}

	// sends accountdetails to server to create new account
	public void handleAccountCreation(String username, String plaintext, String email) {
		this.usernamenewacc = username;
		this.plaintext = plaintext;
		Password pw = new Password(plaintext, new String("defsalt").getBytes());
		PacketRegisterRequest packet = new PacketRegisterRequest(username, pw.getHashedPasswordAsString(), email);
		try {
			c_login.sendMessage(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("client sent new Accountinformaion to server");
	}

	// public static void main(String[] args) {
	// new LoginClient().handlelogin("Alex44", "Schokolade");;
	// }

}
