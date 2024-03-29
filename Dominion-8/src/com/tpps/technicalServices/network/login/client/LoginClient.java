package com.tpps.technicalServices.network.login.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.login.SQLHandling.Password;
import com.tpps.technicalServices.network.login.packets.PacketGetAllStatistics;
import com.tpps.technicalServices.network.login.packets.PacketLoginCheckAnswer;
import com.tpps.technicalServices.network.login.packets.PacketLoginCheckRequest;
import com.tpps.technicalServices.network.login.packets.PacketRegisterAnswer;
import com.tpps.technicalServices.network.login.packets.PacketRegisterRequest;
import com.tpps.ui.loginscreen.LoginGUIController;

/**
 * this class delivers specific methods to handle the login/account creation.
 * These methods are able to send or receive packets from the LoginServer
 * 
 * @author jhuhn - Johannes Huhn
 */
public class LoginClient extends PacketHandler {

	private Client c_login;
	private UUID sessionid;
	private String username;
	private SessionClient c_session;
	private LoginGUIController guicontroller;
	private boolean packetFlag;

//	private String usernamenewacc;
	private String plaintext;

	/**
	 * Initializes the object, create connection to the loginserver and
	 * sessionserver
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param guicontroller
	 *            controlls the overall gui for login
	 */
	public LoginClient(LoginGUIController guicontroller) {
		packetFlag = true;
		try {
			this.guicontroller = guicontroller;
			c_login = new Client(new InetSocketAddress(Addresses.getRemoteAddress(), 1338), this, false);
			c_session = new SessionClient(new InetSocketAddress(Addresses.getRemoteAddress(), SessionServer.getStandardPort()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * method to login into LoginServer, sends packet to LoginServer
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param nickname
	 *            a String representation of the username/acountname
	 * @param plaintext
	 *            a String representation of the password in plaintext
	 */
	public void handlelogin(String nickname, String plaintext) {
		GameLog.log(MsgType.INFO, "Packetflag: " + packetFlag);
		if(packetFlag){
			packetFlag = false;
			this.username = nickname;
			Password pw = new Password(plaintext, new String("defsalt")); // defsalt is a standardsalt
			try {
				String pwAsString = pw.getHashedPassword();
				PacketLoginCheckRequest check = new PacketLoginCheckRequest(nickname, pwAsString);
				c_login.sendMessage(check);
				
				GameLog.log(MsgType.INFO, "Sent accountinformation hashed to the login server");
			} catch (Exception e) {
				packetFlag = true;
				e.printStackTrace();
			}
		}
	}

	/**
	 * called when a packet from the LoginServer received, method to handle the
	 * received packet from the LoginServer important to get the state of a
	 * loginrequest important to get the state of an Accountcreation e.g
	 * nickname ok, Email already given
	 * 
	 * @author jhuhn - Johannes Huhn
	 */
	@Override
	public void handleReceivedPacket(int port, Packet answer) {
		GameLog.log(MsgType.NETWORK_INFO, "Client received an answer packet");
			switch (answer.getType()) {
			case LOGIN_CHECK_ANSWER:
				PacketLoginCheckAnswer check = (PacketLoginCheckAnswer) answer;
				guicontroller.getStateOfLoginRequest(check.getState());
				if (check.getState()==1) { // Anmeldung erfolgreich, pw richtig
					this.setSessionid(check.getSessionID());
					DominionController.getInstance().setSessionID(check.getSessionID());
					c_session.keepAlive(username, true);
				}
				packetFlag = true;
				GameLog.log(MsgType.INFO, "enabled");
				break;
			case LOGIN_REGISTER_ANSWER:
				PacketRegisterAnswer check2 = (PacketRegisterAnswer) answer;
				guicontroller.getStateOfAccountCreation(check2.getState(), this.username, this.plaintext);
				packetFlag = true;
				break;
			case GET_ALL_STATISTICS:
				this.handleAllStatistics((PacketGetAllStatistics) answer);
				break;
			default:
				break;
			}
		
	}
	
	private void handleAllStatistics(PacketGetAllStatistics packet){
		String[][] allStatistics = packet.getAllStatistics();
		GameLog.log(MsgType.STATISTICS,"received all statistics");
		DominionController.getInstance().loadStatisticsToGui(allStatistics);
	}

	/**
	 * method to create a new unique account this method sends
	 * accountinformation (nickname, password, email) to the LoginServer
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param username
	 *            a String representation of the desired nickname
	 * @param plaintext
	 *            a String representation of the desired password in plaintext
	 * @param email
	 *            a String representation of the desired email adress
	 */
	public void handleAccountCreation(String username, String plaintext, String email) {
		if(packetFlag){
			this.username = username;
			this.plaintext = plaintext;
			Password pw = new Password(plaintext, new String("defsalt"));
			PacketRegisterRequest packet = new PacketRegisterRequest(username, pw.getHashedPassword(), email);
			try {
				c_login.sendMessage(packet);
				packetFlag = false;
			} catch (IOException e) {
				e.printStackTrace();
				packetFlag = true;
			}
			System.out.println("client sent accountinformaion to server to create a new account");
		}
	}
	
	/**
	 * This method sends a request packet to get all statistics from the database
	 */
	public void sendPacketForAllStatistics(){
		try {
			this.c_login.sendMessage(new PacketGetAllStatistics());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * gets the sessionid
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @return the sessionid (UUID), if the loginrequest is valid, else null
	 */
	public UUID getSessionid() {
		return sessionid;
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * @param sessionid
	 *            sets the sessionid
	 */
	public void setSessionid(UUID sessionid) {
		this.sessionid = sessionid;
	}

	/**
	 * @return the client holding connection to the login server
	 */
	public Client getClient() {
		return c_login;
	}
	
	/**
	 * 
	 * @return a String representation of the used username (will be set after login)
	 */
	public String getUsername() {
		return username;
	}
}
