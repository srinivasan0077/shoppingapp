package com.shoppingapp.dbutils;

import java.util.ArrayList;

public class IQMetaInfo {
	
	private ArrayList<String> parentUniqueFields;
	private ArrayList<String> uniqueFields;
	private String tableName;
	private String pkName;
	private Integer pkFieldIndex;
	private String parentTableName;
	private String parentPkName;
	private String childIQMetaInfo;
	
	public ArrayList<String> getParentUniqueFields() {
		return parentUniqueFields;
	}
	public void setParentUniqueFields(ArrayList<String> parentUniqueFields) {
		this.parentUniqueFields = parentUniqueFields;
	}
	public ArrayList<String> getUniqueFields() {
		return uniqueFields;
	}
	public void setUniqueFields(ArrayList<String> uniqueFields) {
		this.uniqueFields = uniqueFields;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPkName() {
		return pkName;
	}
	public void setPkName(String pkName) {
		this.pkName = pkName;
	}
	public Integer getPkFieldIndex() {
		return pkFieldIndex;
	}
	public void setPkFieldIndex(Integer pkFieldIndex) {
		this.pkFieldIndex = pkFieldIndex;
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
	public String getChildIQMetaInfo() {
		return childIQMetaInfo;
	}
	public void setChildIQMetaInfo(String childIQMetaInfo) {
		this.childIQMetaInfo = childIQMetaInfo;
	}
	
	
}
