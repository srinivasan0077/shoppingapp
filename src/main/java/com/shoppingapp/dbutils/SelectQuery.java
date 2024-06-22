package com.shoppingapp.dbutils;

import java.util.ArrayList;


public class SelectQuery {

	private Criteria criteria=null;
	private ArrayList<Join> joinList=null;
	private ArrayList<Column> fields=null;
	private String tableName;
	private OrderBy orderBy;
	private Integer limit;
	private boolean useDistinct=false;
	
	
	public boolean isUseDistinct() {
		return useDistinct;
	}

	public void setUseDistinct(boolean useDistinct) {
		this.useDistinct = useDistinct;
	}

	public OrderBy getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
	}

	public SelectQuery(String tableName) {
		this.tableName=tableName;
	}

	public ArrayList<Column> getFields() {
		return fields;
	}

	public void setFields(ArrayList<Column> fields) {
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
	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	

	public String getSelectQueryString() {
		StringBuilder selectQueryString=new StringBuilder("select ");
		if(useDistinct) {
			selectQueryString.append("DISTINCT ");
		}
		
		if(fields==null) {
			selectQueryString.append("* ");
		}else {
			for(int i=0;i<fields.size();i++) {
				Column column=fields.get(i);
			    selectQueryString.append(column.getTableName()).append(".").append(column.getColumnName());
				if(i!=fields.size()-1) {
					selectQueryString.append(",");
				}
			}
		}
		selectQueryString.append(" from ").append(this.tableName).append(" ");
		if(joinList!=null) {
			for(int i=0;i<joinList.size();i++) {
				selectQueryString.append(joinList.get(i).getJoinString()).append(" ");
			}
		}
		
		if(criteria!=null) {
			selectQueryString.append("where ").append(criteria.getCriteria()).append(" ");
		}
		
		if(orderBy!=null) {
			selectQueryString.append(orderBy.constructOrderBy()).append(" ");
		}
		
		if(limit!=null) {
			selectQueryString.append("LIMIT ").append(limit);
		}
		
		return selectQueryString.toString();
	}
	
	public ArrayList<Object> getJoinValuesToBePlaced(){
		ArrayList<Object> values=new ArrayList<>();
		if(joinList!=null) {
			for(int i=0;i<joinList.size();i++) {
				values.addAll(joinList.get(i).getValuesToBeplaced());
			}
		}
		return values;
	}
	
    

}
