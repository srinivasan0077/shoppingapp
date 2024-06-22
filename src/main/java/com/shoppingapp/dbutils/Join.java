package com.shoppingapp.dbutils;

import java.util.ArrayList;

public class Join {

	private Column col1;
	private Column col2;
	private Integer joinType;
	private SelectQuery joinQuery;
	private String alias;
	public static Integer INNER_JOIN=0;
	public static Integer LEFT_JOIN=1;
	public static Integer RIGHT_JOIN=2;
	public static Integer FULL_JOIN=3;
	
	public Join(Column col1,Column col2,Integer joinType) {
		this.col1=col1;
		this.col2=col2;
		this.joinType=joinType;
	}
	
	public Join(Column col1,Column col2,String alias,Integer joinType) {
		this.col1=col1;
		this.col2=col2;
		this.joinType=joinType;
		this.alias=alias;
	}
	
	public Join(SelectQuery sq,Column col1,Column col2,String alias,Integer joinType) {
		this.joinQuery=sq;
		this.col1=col1;
		this.col2=col2;
		this.joinType=joinType;
		this.alias=alias;
	}

	private String getJoin() {
		switch (this.joinType) {
		case 0:
			return "INNER JOIN";
		case 1:
			return "LEFT JOIN";
		case 2:
			return "RIGHT JOIN";
		case 3:
			return "FULL JOIN";
		default:
			return null;
		}
	}
	
	public String getJoinString() {
		
		StringBuilder joinBuilder=new StringBuilder(this.getJoin());
		joinBuilder.append(" ");
		if(joinQuery!=null) {
			joinBuilder.append("(");
			joinBuilder.append(joinQuery.getSelectQueryString());
			joinBuilder.append(") AS ").append(alias);
			
		}else {
			joinBuilder.append(col2.getTableName());
		}
		joinBuilder.append(" ON ").append(col1.getTableName()).append(".").append(col1.getColumnName()).append("=");
		
		if(alias!=null) {
			joinBuilder.append(alias).append(".").append(col2.getColumnName()).toString();
		}else {
			joinBuilder.append(col2.getTableName()).append(".").append(col2.getColumnName()).toString();
		}
		return joinBuilder.toString();
	}
	
	public ArrayList<Object> getValuesToBeplaced(){
		ArrayList<Object> values=new ArrayList<>();
		if(joinQuery!=null) {
			if(joinQuery.getCriteria()!=null) {
				joinQuery.getCriteria().addValues(values);
			}
		}
		return values;
	}

	@Override
	public String toString() {
		return this.getJoinString();
	}
	

	
}
