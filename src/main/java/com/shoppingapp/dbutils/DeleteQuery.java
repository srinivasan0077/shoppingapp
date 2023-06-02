package com.shoppingapp.dbutils;

public class DeleteQuery {

	private String tableName;
	private Criteria criteria;
	
	public DeleteQuery(String tablename) {
		this.tableName=tablename;
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	public String getDeleteQuery() {
		String query="delete from "+tableName;
		if(criteria!=null) {
			query+=" where "+criteria.getCriteria();
		}
		return query;
	}
	
}
