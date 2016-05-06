package com.tpps.technicalServices.network.chat.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class has implemented the whole votekick logic
 * 
 * @author jhuhn
 */
public class Votekick {

	private ConcurrentHashMap<String, Boolean> alreadyvoted;
	private ArrayList<String> notvotedyet;
	private int vote_yes;
	private int vote_no;
	private String usertogetkicked;
	
	/**
	 * initializes the votekick instance
	 * 
	 * @author jhuhn
	 * @param notvotedyet
	 *            an ArrayList of all users, who hasn't voted yet
	 * @param usertogetkicked
	 *            String representation of the user who should get kicked
	 * @param userstartedvotekick
	 *            String representation of the user who started the votekick
	 */
	public Votekick(ArrayList<String> notvotedyet, String usertogetkicked, String userstartedvotekick){
		alreadyvoted = new ConcurrentHashMap<String, Boolean>();
		this.notvotedyet = notvotedyet;
		this.usertogetkicked = usertogetkicked;
		this.alreadyvoted.put(userstartedvotekick, true);
		this.vote_yes++;
	}
	
	/**
	 * This method adds a vote, it is called when a user types in '/vote y' or
	 * '/vote n' in the chat
	 * 
	 * @author jhuhn
	 * @param user
	 *            String representation of the user who votes
	 * @param vote
	 *            boolean representation of the users decision (true: go for
	 *            kick, false: let him in chatroom)
	 */
	public void addVote(String user, boolean vote){
		alreadyvoted.put(user, vote);
		notvotedyet.remove(user);
		if(vote){
			vote_yes++;
		}else{
			vote_no++;
		}
	}
	
	/**
	 * This method evaluates the whole votekick results
	 * 
	 * @author jhuhn
	 * @return true, if the 'usertogetkicked' user should get kicked, false
	 *         elese
	 */
	public boolean fastEvaluateVote() {
		if ((this.vote_yes + this.vote_no) <= notvotedyet.size()) { // the
																	// minority
																	// has voted
																	// => no kick
			return false;
		}
		if(this.vote_yes > this.vote_no){//go for kick
			return true;
		}else{
			return false;//no kick
		}
	}
	
	/**
	 * This method puts all results from this instance in a beautiful String. It
	 * is called when the user types in '/show votekickresults'.
	 * 
	 * @author jhuhn
	 * @return a beatuiful String of all results
	 */
	public String printResults(){
		StringBuffer buf = new StringBuffer("Voted: \n");
		for (Entry<String, Boolean> entry : alreadyvoted.entrySet()) {
			buf.append(entry.getKey() + " voted with " + entry.getValue() + "\n");
		}
		buf.append("Voted not: \n");
		for (Iterator<String> iterator = notvotedyet.iterator(); iterator.hasNext();) {
			 buf.append(iterator.next() + "\n");
			
		}
		buf.append("Votekick result: " + this.fastEvaluateVote());
		return buf.toString();
	}
	
	/**
	 * This method checks if a user already voted
	 * 
	 * @author jhuhn
	 * @param user
	 *            String representation of the user to check
	 * @return true if the user voted already, false else
	 */
	public boolean checkIfUserVoted(String user){
		return !this.notvotedyet.contains(user);
	}
	
	/**
	 * @author jhuhn
	 * @return a String representation of the user to get kicked
	 */
	public String getUsertogetkicked() {
		return usertogetkicked;
	}
	
	/**
	 * @author jhuhn
	 * @return an ArrayList of users that haven't been voted yet
	 */
	public ArrayList<String> getNotvotedyet() {
		return notvotedyet;
	}
}
