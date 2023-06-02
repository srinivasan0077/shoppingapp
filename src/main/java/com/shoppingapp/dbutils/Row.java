package com.shoppingapp.dbutils;

import java.util.HashMap;
import java.util.Map;

public class Row {

     private Map<String, Object> columns=new HashMap<String,Object>();
     private Map<String,Map<Integer,Row>> childs=new HashMap<String, Map<Integer,Row>>();
     
	 public Object get(String key) {
		 return this.columns.get(key);
	 }
	 public Map<String, Object> getColumns() {
		return columns;
	 }
	 public void setColumns(HashMap<String, Object> columns) {
		this.columns = columns;
	 }
	public Map<String, Map<Integer, Row>> getChilds() {
		return childs;
	}
	public void setChilds(String tableName, Map<Integer, Row>childs) {
		this.childs.put(tableName, childs);
	}
	
     
}
