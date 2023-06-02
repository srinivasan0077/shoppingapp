package com.shoppingapp.dbutils;

public class Column {

	private String tableName;
	private String columnName;
	private Object value;
	
	public Column(String tableName, String columnName) {
		this.tableName = tableName;
		this.columnName = columnName;
	}
	
	public Column(String tableName, String columnName,Object value) {
		this.tableName = tableName;
		this.columnName = columnName;
		this.value=value;
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	
}
