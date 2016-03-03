package com.tpps.application.network.login.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

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

/**
 * this class delivers specific methods to handle the login/account creation.
 * These methods are able to send or receive packets from the LoginServer
 * @author jhuhn - Johannes Huhn
 */
public class LoginClient extends PacketHandler {

	private Client c_login;
	private UUID sessionid;
	private String usernamelogin;
	private SessionClient c_session;
	private LoginGUIController guicontroller;

	private String usernamenewacc;
	private String plaintext;

	/**
	 * Initializes the object, create connection to the loginserver and sessionserver
	 * @author jhuhn - Johannes Huhn
	 * @param guicontroller controlls the overall gui for login
	 */
	public LoginClient(LoginGUIController guicontroller) {
		try {
			this.guicontroller = guicontroller;
			c_login = new Client(new InetSocketAddress("127.0.0.1", 1338), this);
			c_session = new SessionClient(new InetSocketAddress("127.0.0.1", 1337));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * method to login into LoginServer, sends packet to LoginServer
	 * @author jhuhn - Johannes Huhn
	 * @param nickname a String representation of the username/acountname
	 * @param plaintext a String representation of the password in plaintext
	 */
	public void handlelogin(String nickname, String plaintext) {
		this.usernamelogin = nickname;
		Password pw = new Password(plaintext, new String("defsalt").getBytes()); // defsalt is a standardsalt
																					
		try {
			String pwAsString = pw.getHashedPasswordAsString();
			PacketLoginCheckRequest check = new PacketLoginCheckRequest(nickname, pwAsString);
			c_login.sendMessage(check);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * called when a packet from the LoginServer received,
	 * method to handle the received packet from the LoginServer
	 * important to get the state of a loginrequest
	 * important to get the state of an Accountcreation e.g nickname ok, Email already given
	 * @author jhuhn - Johannes Huhn
	 */
	@Override
	public void handleReceivedPacket(int port, Packet answer) {
		System.out.println("Client received an answer packet");
		switch (answer.getType()) {
		case LOGIN_CHECK_ANSWER:
			PacketLoginCheckAnswer check = (PacketLoginCheckAnswer) answer;
			guicontroller.getStateOfLoginRequest(check.getState());
			if (check.getState()) { // Anmeldung erfolgreich, pw richtig
				this.setSessionid(check.getSessionID());
				c_session.keepAlive(usernamelogin, true);
			}
			break;
		case LOGIN_REGISTER_ANSWER:
			PacketRegisterAnswer check2 = (PacketRegisterAnswer) answer;
			guicontroller.getStateOfAccountCreation(check2.getState(),this.usernamenewacc, this.plaintext);
		default:
			break;
		}
	}

	/**
	 * method to create a new unique account
	 * this method sends accountinformation (nickname, password, email) to the LoginServer
	 * @author jhuhn - Johannes Huhn
	 * @param username a String representation of the desired nickname
	 * @param plaintext a String representation of the desired password in plaintext
	 * @param email a String representation of the desired email adress
	 */
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
		System.out.println("client sent accountinformaion to server to create a new account");
	}

	/**
	 * gets the sessionid
	 * @author jhuhn - Johannes Huhn
	 * @return the sessionid (UUID), if the loginrequest is valid, else null
	 */
	public UUID getSessionid() {
		return sessionid;
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * @param sessionid sets the sessionid
	 */
	public void setSessionid(UUID sessionid) {
		this.sessionid = sessionid;
	}
}
