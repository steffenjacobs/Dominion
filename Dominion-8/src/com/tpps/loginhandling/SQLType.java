package com.tpps.loginhandling;

public enum SQLType {
	VARCHAR,
	TEXT,
	INT,
	FLOAT,
	DATE;
	
	public String toString(){
		return name();
	}
}
