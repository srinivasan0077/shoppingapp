package com.shoppingapp.dbutils;

import java.util.ArrayList;
import java.util.List;

public class DeleteQuery {

	private String tableName;
	private List<Join> joinList=null;
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
	public void setJoin(Join join) {
		joinList=new ArrayList<Join>();
		joinList.add(join);
	}
	
	public void addJoin(Join join) {
		joinList.add(join);
	}
	
	public String getDeleteQuery() {
		StringBuilder query=new StringBuilder("delete from ").append(tableName).append(" ");

		if(joinList!=null) {
			for(int i=0;i<joinList.size();i++) {
				query.append(joinList.get(i).getJoinString()).append(" ");
			}
		}
		
		if(criteria!=null) {
			query.append("where ").append(criteria.getCriteria());
		}
		return query.toString();
	}
	
}
