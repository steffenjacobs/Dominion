package com.tpps.application.network.chat;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Votekick {

	private ConcurrentHashMap<String, Boolean> alreadyvoted;
	private ArrayList<String> notvotedyet;
	private int vote_yes;
	private int vote_no;
	private String usertogetkicked;
	
	public Votekick(ArrayList<String> notvotedyet, String usertogetkicked){
		alreadyvoted = new ConcurrentHashMap<String, Boolean>();
		this.notvotedyet = notvotedyet;
		this.usertogetkicked = usertogetkicked;
	}
	
	public void addVote(String user, boolean vote){
		alreadyvoted.put(user, vote);
		notvotedyet.remove(user);
		if(vote){
			vote_yes++;
		}else{
			vote_no++;
		}
	}
	
	public boolean fastEvaluateVote(){
		if((this.vote_yes + this.vote_no) <= notvotedyet.size()){	//the minority has voted => no kick
			return false;
		}
		if(this.vote_yes > this.vote_no){//go for kick
			return true;
		}else{
			return false;//no kick
		}
	}
	
	public boolean fullEvaluateVote(){
		int voteyes = 0;
		int voteno = 0;
		for (Entry<String, Boolean> entry : alreadyvoted.entrySet()) {
		    boolean vote = entry.getValue();
		    if(vote){
		    	voteyes++;
		    }else{
		    	voteno++;
		    }
		}
		if((voteyes + voteno) <= notvotedyet.size()){	//the minority has voted => no kick
			return false;
		}
		if(voteyes > voteno){//go for kick
			return true;
		}else{
			return false;//no kick
		}
	}
	
	public boolean checkIfUserVoted(String user){
		return !this.notvotedyet.contains(user);
	}
	
	public int getVote_no() {
		return vote_no;
	}
	public int getVote_yes() {
		return vote_yes;
	}
	
	public void setVote_yes(int vote_yes) {
		this.vote_yes = vote_yes;
	}
	
	public String getUsertogetkicked() {
		return usertogetkicked;
	}
	
	public ArrayList<String> getNotvotedyet() {
		return notvotedyet;
	}
}
