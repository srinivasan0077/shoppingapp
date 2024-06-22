package com.shoppingapp.dbutils;

import java.util.ArrayList;
import java.util.List;

public class UpdateQuery {
	private Criteria criteria=null;
	private List<Join> joinList=null;
	private List<Column> fields=null;
	private String tableName;
	
	public UpdateQuery(String tableName) {
		this.tableName=tableName;
	}

	public List<Column> getFields() {
		return fields;
	}

	public void setFields(List<Column> fields) {
		this.fields = fields;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setCriteria(Criteria criteria) {
		this.criteria=criteria;
	}
	
	public Criteria getCriteria() {
		return criteria;
	}
	
	public void setJoin(Join join) {
		joinList=new ArrayList<Join>();
		joinList.add(join);
	}
	
	public void addJoin(Join join) {
		joinList.add(join);
	}
	
	public String getUpdateQueryString() {
		StringBuilder updateQueryString=new StringBuilder("update ").append(tableName).append(" ");
		
		if(joinList!=null) {
			for(int i=0;i<joinList.size();i++) {
				updateQueryString.append(joinList.get(i).getJoinString()).append(" ");
			}
		}
		updateQueryString.append("set ");
		for(int i=0;i<fields.size();i++) {
			Column column=fields.get(i);
			updateQueryString.append(column.getTableName()).append(".").append(column.getColumnName()).append("=?");
			if(i!=fields.size()-1) {
				updateQueryString.append(",");
			}
		}

		updateQueryString.append(" ");
		
		if(criteria!=null) {
			updateQueryString.append("where ").append(criteria.getCriteria()).append(" ");
		}
		
		updateQueryString.append(";");
		return updateQueryString.toString();
	}
	

	
}
