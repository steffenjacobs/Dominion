package com.tpps.application.network.login.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.network.clientSession.client.SessionPacketSenderAPI;
import com.tpps.application.network.clientSession.client.SessionClient;
import com.tpps.application.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.application.network.clientSession.server.SessionServer;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.SuperCallable;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.login.SQLHandling.Password;
import com.tpps.application.network.login.SQLHandling.SQLOperations;
import com.tpps.application.network.login.SQLHandling.SQLStatisticsHandler;
import com.tpps.application.network.login.packets.PacketLoginCheckAnswer;
import com.tpps.application.network.login.packets.PacketLoginCheckRequest;
import com.tpps.application.network.login.packets.PacketRegisterAnswer;
import com.tpps.application.network.login.packets.PacketRegisterRequest;

/**
 * This class delivers all functionalities that are used by the LoginServer
 * @author jhuhn - Johannes Huhn
 */
public class LoginPacketHandler extends PacketHandler{

	private LoginServer server;
	private SessionClient sessionclient;
	private ConcurrentHashMap<String, Integer> waitingForSessionAnswer;
	
	/**
	 * Initializes the LoginPacketHandler object, opens a connection to the Sessionserver with a sessionclient
	 * @author jhuhn - Johannes Huhn
	 */
	public LoginPacketHandler() {
		try {
			waitingForSessionAnswer = new ConcurrentHashMap<>();
			sessionclient = new SessionClient(new InetSocketAddress("127.0.0.1", SessionServer.getStandardPort()));
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}

	/**
	 * called when a packet from the LoginClient received,
	 * method to handle the received packet from the loginClient
	 * important to validate the loginrequest and sends a packet back to the loginClient
	 * important to validate the Accountcreation and sends a packet back to the loginClient
	 * @author jhuhn - Johannes Huhn
	 */
	@Override
	public void handleReceivedPacket(int port, final Packet packet) {
		System.out.println("Server received packet: ");
		switch(packet.getType()){
		case LOGIN_CHECK_REQUEST: //check username, if valid genereate SESSION ID and send to SessionServer
			PacketLoginCheckRequest pac = (PacketLoginCheckRequest) packet;
			System.out.println(pac.getUsername());
			
			String nickname = SQLOperations.getNicknameFromEmail(pac.getUsername());		
			String salt = SQLOperations.getSaltForLogin(nickname);
			

			//System.out.println("salt aus db: " + salt);
			try {
				Password pw = new Password(pac.getHashedPW(), salt);
				pw.createHashedPassword();
				String doublehashed = pw.getHashedPassword();
				waitingForSessionAnswer.put(nickname, port);
				if(SQLOperations.rightDoubleHashedPassword(nickname, doublehashed)){
					System.out.println("calculated hash match with hash out of the database");
					SessionPacketSenderAPI.sendGetRequest(sessionclient, nickname, new SuperCallable<PacketSessionGetAnswer>() {						
						@Override
						public PacketSessionGetAnswer callMeMaybe(PacketSessionGetAnswer answer) {							
							PacketLoginCheckAnswer checkAnswer = new PacketLoginCheckAnswer(pac, true, answer.getLoginSessionID());
							try {
								server.sendMessage(waitingForSessionAnswer.remove(nickname), checkAnswer);
							} catch (IOException e) {
								e.printStackTrace();
							}
							return null;
						}
					});
				}else{
					System.out.println("calculated hash doesn't match with hash out of the database");
					PacketLoginCheckAnswer answer = new PacketLoginCheckAnswer((PacketLoginCheckRequest) packet, false, null);
					server.sendMessage(port, answer);
				}
			} catch (Exception e) {			
			//	e.printStackTrace();
				PacketLoginCheckAnswer answer = new PacketLoginCheckAnswer((PacketLoginCheckRequest) packet, false, null);
				try {
					server.sendMessage(port, answer);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} finally{
				System.out.println("----------------------------");
			}
			break;
			
		case LOGIN_REGISTER_REQUEST: //create user
			System.out.println("Server got a request to create an Account");
			PacketRegisterRequest castedPac = (PacketRegisterRequest) packet;
			String username = castedPac.getUsername();
			String email = castedPac.getEmail();
			String firsthashedpw = castedPac.getHashedPW();
			Password pw2 = new Password(firsthashedpw);
			String genereatedrandomsalt = pw2.getSalt();
			String doublehashedpw = pw2.getHashedPassword();
			
			int state = SQLOperations.createAccount(username, email, doublehashedpw, genereatedrandomsalt);
			if(state == 1){
				SQLStatisticsHandler.insertRowForFirstLogin(username);
			}
			
			PacketRegisterAnswer pack = new PacketRegisterAnswer(castedPac, state, null);
			try {
				server.sendMessage(port, pack);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			System.out.println("finished a creating account procedure");
			System.out.println("----------------------------");
			break;
		default:break;
		}
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * @return the server object
	 */
	public LoginServer getServer() {
		return server;
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * @param server sets the server object
	 */
	public void setServer(LoginServer server) {
		this.server = server;
	}
}
