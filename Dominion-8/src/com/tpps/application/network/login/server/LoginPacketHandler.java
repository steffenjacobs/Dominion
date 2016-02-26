package com.tpps.application.network.login.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.login.SQLHandling.Password;
import com.tpps.application.network.login.SQLHandling.SQLOperations;
import com.tpps.application.network.login.packets.PacketLoginCheckAnswer;
import com.tpps.application.network.login.packets.PacketLoginCheckRequest;
import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;
import com.tpps.application.network.sessions.client.SessionPacketReceiverAPI;
import com.tpps.application.network.sessions.client.SessionPacketSenderAPI;
import com.tpps.application.network.sessions.packets.PacketSessionGetAnswer;

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
		Packet packet = PacketType.getPacket(bytes);
		switch(packet.getType()){
		case LOGIN_CHECK_REQUEST: //check username, if valid genereate SESSION ID
			PacketLoginCheckRequest pac = (PacketLoginCheckRequest) packet;
			String salt = sql.getSaltForLogin(pac.getUsername());			
			Password pw = new Password(pac.getHashedPW(), salt.getBytes());
			try {
				pw.createHashedPassword();
				String doublehashed = pw.getHashedPasswordAsString();
				//TODO: vergleiche doublehashed pw mit db boolean x
				boolean x = true;
				if(x){
					SessionPacketSenderAPI.sendGetRequest(sessionclient, pac.getUsername(), new Callable<Void>() {						
						@Override
						public Void call() throws Exception {
							PacketSessionGetAnswer answer = SessionPacketReceiverAPI.getGetAnswer(pac.getUsername());							
							PacketLoginCheckAnswer checkAnswer = new PacketLoginCheckAnswer(pac, true, answer.getLoginSessionID());
							server.sendMessage(port, PacketType.getBytes(checkAnswer));
							return null;
						}
					});
				}else{
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
