package com.tpps.application.network.chat;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Votekick {

	private ConcurrentHashMap<String, Boolean> alreadyvoted;
	private ArrayList<String> notvotedyet;
	private int vote_yes;
	private int vote_no;
	
	public Votekick(){
		alreadyvoted = new ConcurrentHashMap<String, Boolean>();
		notvotedyet = new ArrayList<String>();
	}
	
	public Votekick(ArrayList<String> notvotedyet){
		alreadyvoted = new ConcurrentHashMap<String, Boolean>();
		this.notvotedyet = notvotedyet;
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
	
	
	public int getVote_no() {
		return vote_no;
	}
	public int getVote_yes() {
		return vote_yes;
	}
}
