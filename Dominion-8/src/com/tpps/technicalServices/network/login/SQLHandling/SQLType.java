package com.tpps.technicalServices.network.login.SQLHandling;

/**
 * @author jhuhn - Johannes Huhn
 * This class represents one Datatype in the mysql database like VARCHAR or FLOAT
 */
public enum SQLType {
	VARCHAR,
	TEXT,
	INT,
	BIGINT,
	FLOAT,
	DATE,
	BOOLEAN;
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * overrides the toString() method to get the name of the enumtype
	 */
	public String toString(){
		return name();
	}
}
