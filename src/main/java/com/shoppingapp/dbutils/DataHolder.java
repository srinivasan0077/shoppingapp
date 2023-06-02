package com.shoppingapp.dbutils;

import java.util.HashMap;
import java.util.Map;


public class DataHolder {

	private HashMap<String, TableHolder> tableMap=new HashMap<String,TableHolder>();
	
	public TableHolder getTable(String name) {
		return this.tableMap.get(name);
	}
	
	public void putTable(String name,TableHolder table) {
		this.tableMap.put(name,table);
	}
	
	public Map<String,TableHolder> getTableMap() {
		return this.tableMap;
	}
}
