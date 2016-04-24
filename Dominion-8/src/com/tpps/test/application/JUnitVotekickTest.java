package com.tpps.test.application;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.tpps.technicalServices.network.chat.server.Votekick;

/**
 * This JUnit tests the votekick logic
 * 
 * @author jhuhn
 *
 */
public class JUnitVotekickTest {

	private static final String get_kicked = "get_kicked";
	private static final String user1 = "user1";
	private static final String user2 = "user2";
	private static final String user3 = "user3";
	
	private Votekick vk;
		
	/**
	 * This method creates a Votekick instance, "user1" votes automatically
	 * "yes", because he started the vote
	 * 
	 * @author jhuhn
	 * @return a prepared Votekick instance
	 */
	public Votekick setupVotekick() {
		ArrayList<String> notvotedyet = new ArrayList<String>();
		notvotedyet.add(user1);
		notvotedyet.add(user2);
		notvotedyet.add(user3);
		notvotedyet.add(get_kicked);
		vk = new Votekick(notvotedyet, get_kicked, user1);
		//user1 votes automatically
		return vk;
	}
	
	/**
	 * This method tests the votekick logic. The comments below shows the
	 * votekick conditions
	 */
	@Test
	public void test() {
		//all vote yes
		vk = this.setupVotekick();
		vk.addVote(get_kicked, true);
		vk.addVote(user2, true);
		vk.addVote(user3, true);
		assertTrue(vk.fastEvaluateVote());
		
		//3yes 	1no
		vk = this.setupVotekick();
		vk.addVote(get_kicked, true);
		vk.addVote(user2, true);
		vk.addVote(user3, false);
		assertTrue(vk.fastEvaluateVote());
		
		//2yes 2no
		vk = this.setupVotekick();
		vk.addVote(get_kicked, true);
		vk.addVote(user2, false);
		vk.addVote(user3, false);
		assertTrue(!vk.fastEvaluateVote());		
		
		//1yes 3no
		vk = this.setupVotekick();
		vk.addVote(get_kicked, false);
		vk.addVote(user2, false);
		vk.addVote(user3, false);
		assertTrue(!vk.fastEvaluateVote());	
		
		// 3yes
		vk = this.setupVotekick();
		vk.addVote(get_kicked, true);
		vk.addVote(user2, true);		
		assertTrue(vk.fastEvaluateVote());
		
		//2yes 1no
		vk = this.setupVotekick();
		vk.addVote(get_kicked, true);
		vk.addVote(user2, false);
		assertTrue(vk.fastEvaluateVote());
		
		//1yes 2no
		vk = this.setupVotekick();
		vk.addVote(get_kicked, false);
		vk.addVote(user2, false);
		assertTrue(!vk.fastEvaluateVote());	
		
		//2yes
		vk = this.setupVotekick();
		vk.addVote(user2, false);
		assertTrue(!vk.fastEvaluateVote());	//no kick, cause minority voted
	}

}
