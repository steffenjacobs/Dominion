package com.tpps.loginhandling;

/**
 * @author jhuhn - Johannes Huhn
 * This class represents one statistic which should contain one column in the database
 * All statistics will be created with the given class
 */
public class Statistic {
	
	private String enumvalue;
	private SQLType type;
	private String columnname;
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * Initializes the Object
	 * @param type enum type of the mysql Column like VARCHAR or INT
	 * @param columname String representation of the used header of one column
	 */
	public Statistic(SQLType type, String columname){
		this.type = type;
		this.setColumnname(columname);
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * Initializes the Object
	 * @param type enum type of the mysql Column like VARCHAR or INT
	 * @param value is needed to specify the length of a VARCHAR or the values of a FLOAT data type
	 * @param columname String representation of the used header of one column
	 */
	public Statistic(SQLType type, String value, String columname){
		this.type = type;
		this.enumvalue = value;
		this.setColumnname(columname);
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * @return a String representation the used value to specify VARCHAR and FLOAT
	 */
	public String getValue() {
		return enumvalue;
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * @param enumvalue sets the enum value, which is needed for FLOAT and VARCHAR
	 */
	public void setValue(String enumvalue) {
		this.enumvalue = enumvalue;
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * This method generates a String for MYSQL databse
	 * @return a full configured String representation of the used datatype in MYSQL syntax
	 */
	public String getTypeAsString() {
		if(enumvalue != null && (this.type == SQLType.VARCHAR)){
			return type + "(" + enumvalue + ")";
		}else if(enumvalue != null && (this.type == SQLType.FLOAT)){
			return type + "(" + enumvalue + ")";
		}else{
			return type.toString();
		}
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * @param type sets the enum type of the used MYSQL datatype
	 */
	public void setType(SQLType type) {
		this.type = type;
	}
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * @return a String representation of the columnname
	 */
	public String getColumnname() {
		return columnname;
	}

	/**
	 * @author jhuhn - Johannes Huhn
	 * @param columnname sets the name of the column in the database
	 */
	public void setColumnname(String columnname) {
		this.columnname = columnname;
	}
}
