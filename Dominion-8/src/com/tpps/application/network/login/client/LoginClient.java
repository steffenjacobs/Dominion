package com.tpps.application.network.login.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.login.SQLHandling.Password;
import com.tpps.application.network.login.packets.PacketLoginCheckAnswer;
import com.tpps.application.network.login.packets.PacketLoginCheckRequest;
import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;
import com.tpps.application.network.sessions.client.Client;
import com.tpps.application.network.sessions.client.SessionClient;

public class LoginClient extends PacketHandler {

	Client c;
	UUID sessionid;
	String username;
	
	public LoginClient() {
		try {
			c = new Client(new InetSocketAddress("127.0.0.1", 1338), this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handlelogin(String nickname, String plaintext){
		//TODO get nickname und plaintext from gui
		this.username = nickname;
		Password pw = new Password(plaintext, new String("defsalt").getBytes()); //defsalt ist standartsalt
		try {
			pw.createHashedPassword();
			String pwAsString = pw.getHashedPasswordAsString();
			PacketLoginCheckRequest check = new PacketLoginCheckRequest(nickname, pwAsString);
			c.sendMessage(PacketType.getBytes(check));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	//TODO: create Account
	public void handleReceivedPacket(int port, byte[] bytes) {		
		Packet answer = PacketType.getPacket(bytes);
		switch(answer.getType()){
		case LOGIN_CHECK_ANSWER: 
			PacketLoginCheckAnswer check = (PacketLoginCheckAnswer) answer;
			if(check.getState()){
				this.sessionid = check.getSessionID();
				SessionClient.keepAlive(c, username, true);
			}else{
				//TODO: Access Denied, PW falsch 
			}
			break;
		default: break;
		}
	}

	public static void main(String[] args) {
		new LoginClient();
	}

}
