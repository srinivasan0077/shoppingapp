package com.shoppingapp.dbutils;

public class Join {

	private Column col1;
	private Column col2;
	private Integer joinType;
	public static Integer INNER_JOIN=0;
	public static Integer LEFT_JOIN=1;
	public static Integer RIGHT_JOIN=2;
	public static Integer FULL_JOIN=3;
	
	public Join(Column col1,Column col2,Integer joinType) {
		this.col1=col1;
		this.col2=col2;
		this.joinType=joinType;
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
		return this.getJoin()+" "+col2.getTableName()+" ON "+col1.getTableName()+"."+col1.getColumnName()+"="+
					col2.getTableName()+"."+col2.getColumnName();
	}

	@Override
	public String toString() {
		return this.getJoinString();
	}
	

	
}
