package com.shoppingapp.dbutils;

import java.util.ArrayList;
import java.util.HashMap;


public class TableHolder {

	private HashMap<Integer,Row> rows=new HashMap<Integer,Row>();
	private String[] fieldNames;
	private String[] parentUniqueFields;
	private String tableName;
	private Integer pkFieldIndex;
	private String parentTableName;
	private String parentPkName;
	private ArrayList<TableHolder> childs=new ArrayList<TableHolder>();
    private HashMap<String,SelectQuery> subQueryFor=new HashMap<String,SelectQuery>();
    
	public TableHolder(String tableName,String fieldNames[]) {
		this.tableName=tableName;
		this.fieldNames=fieldNames;
		
	}
	
	public TableHolder() {
		
	}

	

	public void setRow(Row row) {
		rows.put(rows.size(), row);
	}
	
	public void setRows(HashMap<Integer,Row> rows) {
		this.rows=rows;
	}

	public HashMap<Integer,Row> getRows() {
		return rows;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getParentTableName() {
		return parentTableName;
	}

	public void setParentTableName(String parentTableName) {
		this.parentTableName = parentTableName;
	}

	public String getParentPkName() {
		return parentPkName;
	}

	public void setParentPkName(String parentPkName) {
		this.parentPkName = parentPkName;
	}

	public Integer getPkFieldIndex() {
		return pkFieldIndex;
	}

	public void setPkFieldIndex(Integer pkFieldIndex) {
		this.pkFieldIndex = pkFieldIndex;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	public String[] getParentUniqueFields() {
		return parentUniqueFields;
	}

	public void setParentUniqueFields(String[] parentUniqueFields) {
		this.parentUniqueFields = parentUniqueFields;
	}

	public ArrayList<TableHolder> getChilds() {
		return childs;
	}

	public void setChilds(ArrayList<TableHolder> childs) {
		this.childs = childs;
	}
	
	public void setChild(TableHolder th) {
		this.childs.add(th);
	}

	
	public void setSubQuery(String fieldName,SelectQuery query) {
		this.subQueryFor.put(fieldName, query);
	}
	
	public SelectQuery getSubQuery(String fieldName) {
		return this.subQueryFor.get(fieldName);
	}
	
	public boolean hasSubQuery(String fieldName) {
		return this.subQueryFor.containsKey(fieldName);
	}
	
	
}
