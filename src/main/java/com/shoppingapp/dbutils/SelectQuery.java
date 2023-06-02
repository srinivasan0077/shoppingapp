package com.shoppingapp.dbutils;

import java.util.ArrayList;


public class SelectQuery {

	private Criteria criteria=null;
	private ArrayList<Join> joinList=null;
	private ArrayList<Column> fields=null;
	private String tableName;
	private OrderBy orderBy;
	private Integer limit;
	
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
		String selectQueryString="select ";
		if(fields==null) {
			selectQueryString+="* ";
		}else {
			for(int i=0;i<fields.size();i++) {
				Column column=fields.get(i);
				selectQueryString+=column.getTableName()+"."+column.getColumnName();
				if(i!=fields.size()-1) {
					selectQueryString+=",";
				}
			}
		}
		selectQueryString+=" from "+this.tableName+" ";
		if(joinList!=null) {
			for(int i=0;i<joinList.size();i++) {
				selectQueryString+=joinList.get(i).getJoinString()+" ";
			}
		}
		
		if(criteria!=null) {
			selectQueryString+="where "+criteria.getCriteria()+" ";
		}
		
		if(orderBy!=null) {
			selectQueryString+=orderBy.constructOrderBy()+" ";
		}
		
		if(limit!=null) {
			selectQueryString+="LIMIT "+limit;
		}
		
		return selectQueryString;
	}
    
	public static void main(String args[]) {
		SelectQuery sq=new SelectQuery("users");
		ArrayList<Column> columns=new ArrayList<Column>();
		Column col1=new Column("users","id");
		Column col2=new Column("users","name");
		Column col3=new Column("sales","id");
		Column col4=new Column("sales","price");
		columns.add(col1);
		columns.add(col2);
		columns.add(col3);
		columns.add(col4);
		sq.setFields(columns);
		Join join = new Join(col1,new Column("sales","userid"),Join.LEFT_JOIN);
		sq.setJoin(join);
		
		Criteria criteria=new Criteria(new Column("users","username"),"seenu");
		criteria.setComparator(Criteria.NOTEQUAL);
		Criteria criteria2=new Criteria(new Column("users","id"),Long.valueOf(1000));
		criteria2.setComparator(Criteria.GREATEREQUAL);
		criteria.and(criteria2);
		sq.setCriteria(criteria);
		
		System.out.println(sq.getSelectQueryString());
	}

}
