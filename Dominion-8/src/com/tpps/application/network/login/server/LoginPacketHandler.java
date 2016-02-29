package com.tpps.application.network.login.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.tpps.application.network.clientSession.client.SessionPacketSenderAPI;
import com.tpps.application.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.SuperCallable;
import com.tpps.application.network.login.SQLHandling.Password;
import com.tpps.application.network.login.SQLHandling.SQLOperations;
import com.tpps.application.network.login.packets.PacketLoginCheckAnswer;
import com.tpps.application.network.login.packets.PacketLoginCheckRequest;
import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;

public class LoginPacketHandler extends PacketHandler{

	SQLOperations sql;
	LoginServer server;
	Client sessionclient;
	
	public LoginPacketHandler(SQLOperations sql) {
		this.sql = sql;
		try {
			sessionclient = new Client(new InetSocketAddress("127.0.0.1", 1337), this);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}

	@Override
	public void handleReceivedPacket(int port, byte[] bytes) {
		// TODO Auto-generated method stub
		System.out.println("Server received packet: ");
		Packet packet = PacketType.getPacket(bytes);
		System.out.println("Server received" + packet);
		switch(packet.getType()){
		case LOGIN_CHECK_REQUEST: //check username, if valid genereate SESSION ID
			PacketLoginCheckRequest pac = (PacketLoginCheckRequest) packet;
			System.out.println("yolo holo : " +pac);
			String salt = sql.getSaltForLogin(pac.getUsername());			
			Password pw = new Password(pac.getHashedPW(), salt.getBytes());
			try {
				pw.createHashedPassword();
				String doublehashed = pw.getHashedPasswordAsString();
				if(sql.rightDoubleHashedPassword(pac.getUsername(), doublehashed)){
					System.out.println("im in if");
					SessionPacketSenderAPI.sendGetRequest(sessionclient, pac.getUsername(), new SuperCallable<PacketSessionGetAnswer>() {						
						@Override
						public PacketSessionGetAnswer call(PacketSessionGetAnswer answer) {							
							PacketLoginCheckAnswer checkAnswer = new PacketLoginCheckAnswer(pac, true, answer.getLoginSessionID());
							try {
							//	System.out.println("super callable");
								server.sendMessage(port, PacketType.getBytes(checkAnswer));
							} catch (IOException e) {
								e.printStackTrace();
							}
							return null;
						}
					});
				}else{
					System.out.println("im in else");
					PacketLoginCheckAnswer answer = new PacketLoginCheckAnswer((PacketLoginCheckRequest) packet, false, null);
					server.sendMessage(port, PacketType.getBytes(answer));
				}
			} catch (Exception e) {			
				e.printStackTrace();
			}
			break;
		case LOGIN_REGISTER_REQUEST:  //create user + automatisch login
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
