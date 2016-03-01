package com.tpps.application.network.login.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.network.clientSession.client.SessionPacketSenderAPI;
import com.tpps.application.network.clientSession.client.SuperSessionClient;
import com.tpps.application.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.application.network.clientSession.server.SessionServer;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.SuperCallable;
import com.tpps.application.network.login.SQLHandling.Password;
import com.tpps.application.network.login.SQLHandling.SQLOperations;
import com.tpps.application.network.login.packets.PacketLoginCheckAnswer;
import com.tpps.application.network.login.packets.PacketLoginCheckRequest;
import com.tpps.application.network.login.packets.PacketRegisterAnswer;
import com.tpps.application.network.login.packets.PacketRegisterRequest;
import com.tpps.application.network.packet.Packet;

public class LoginPacketHandler extends PacketHandler{

	LoginServer server;
	SuperSessionClient sessionclient;
	private ConcurrentHashMap<String, Integer> waitingForSessionAnswer;
	
	public LoginPacketHandler() {
		try {
			waitingForSessionAnswer = new ConcurrentHashMap<>();
			sessionclient = new SuperSessionClient(new InetSocketAddress("127.0.0.1", SessionServer.getStandardPort()));
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}

	@Override
	public void handleReceivedPacket(int port, final Packet packet) {
		System.out.println("Server received packet: " + packet);
		switch(packet.getType()){
		case LOGIN_CHECK_REQUEST: //check username, if valid genereate SESSION ID and send to SessionServer
			PacketLoginCheckRequest pac = (PacketLoginCheckRequest) packet;
			String salt = SQLOperations.getSaltForLogin(pac.getUsername());

			System.out.println("salt aus db: " + salt);
			try {
				Password pw = new Password(pac.getHashedPW(), salt.getBytes());
				pw.createHashedPassword();
				String doublehashed = pw.getHashedPasswordAsString();
				waitingForSessionAnswer.put(pac.getUsername(), port);
				if(SQLOperations.rightDoubleHashedPassword(pac.getUsername(), doublehashed)){
					SessionPacketSenderAPI.sendGetRequest(sessionclient, pac.getUsername(), new SuperCallable<PacketSessionGetAnswer>() {						
						@Override
						public PacketSessionGetAnswer callMeMaybe(PacketSessionGetAnswer answer) {							
							PacketLoginCheckAnswer checkAnswer = new PacketLoginCheckAnswer(pac, true, answer.getLoginSessionID());
							try {
								server.sendMessage(waitingForSessionAnswer.remove(pac.getUsername()), checkAnswer);
							} catch (IOException e) {
								e.printStackTrace();
							}
							return null;
						}
					});
				}else{
					System.out.println("im in else, where calculated hash doesn't work");
					PacketLoginCheckAnswer answer = new PacketLoginCheckAnswer((PacketLoginCheckRequest) packet, false, null);
					server.sendMessage(port, answer);
				}
			} catch (Exception e) {			
				e.printStackTrace();
				PacketLoginCheckAnswer answer = new PacketLoginCheckAnswer((PacketLoginCheckRequest) packet, false, null);
				try {
					server.sendMessage(port, answer);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			break;
			
			//create user + automatisch login
		case LOGIN_REGISTER_REQUEST:
			System.out.println("I got a request to create an Account");
			PacketRegisterRequest castedPac = (PacketRegisterRequest) packet;
			System.out.println("In packet is: " + castedPac);
			String username = castedPac.getUsername();
			String email = castedPac.getEmail();
			String firsthashedpw = castedPac.getHashedPW();
			Password pw2 = new Password(firsthashedpw);
			String genereatedrandomsalt = pw2.getSaltAsString();
			String doublehashedpw = pw2.getHashedPasswordAsString();
		//	System.out.println("Information: " + username + " " + email + " " + firsthashedpw + " " + genereatedrandomsalt + " " + doublehashedpw);
			int state = SQLOperations.createAccount(username, email, doublehashedpw, genereatedrandomsalt);
			
			PacketRegisterAnswer pack = new PacketRegisterAnswer(castedPac, state, null);
			try {
				server.sendMessage(port, pack);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			System.out.println("finished creating an Account");
			break;
		default:break;
		}
	}
	
	public LoginServer getServer() {
		return server;
	}

	public void setServer(LoginServer server) {
		this.server = server;
	}
}
