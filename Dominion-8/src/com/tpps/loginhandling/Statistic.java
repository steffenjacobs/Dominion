package com.tpps.loginhandling;

public class Statistic {
	
	private String enumvalue;
	private SQLType type;
	private String columnname;
	
	public Statistic(SQLType type, String columname){
		this.type = type;
		this.setColumnname(columname);
	}
	
	public Statistic(SQLType type, String value, String columname){
		this.type = type;
		this.enumvalue = value;
		this.setColumnname(columname);
	}

	public String getValue() {
		return enumvalue;
	}

	public void setValue(String enumvalue) {
		this.enumvalue = enumvalue;
	}

	public String getTypeAsString() {
		if(enumvalue != null && (this.type == SQLType.VARCHAR)){
			return type + "(" + enumvalue + ")";
		}else if(enumvalue != null && (this.type == SQLType.FLOAT)){
			return type + "(" + enumvalue + ")";
		}else{
			return type.toString();
		}
	}

	public void setType(SQLType type) {
		this.type = type;
	}

	public String getColumnname() {
		return columnname;
	}

	public void setColumnname(String columnname) {
		this.columnname = columnname;
	}
}
