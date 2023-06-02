package com.shoppingapp.dbutils;

import java.util.ArrayList;

public class UpdateQuery {
	private Criteria criteria=null;
	private ArrayList<Join> joinList=null;
	private ArrayList<Column> fields=null;
	private String tableName;
	
	public UpdateQuery(String tableName) {
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
	
	public String getUpdateQueryString() {
		String updateQueryString="update "+tableName+" ";
		
		if(joinList!=null) {
			for(int i=0;i<joinList.size();i++) {
				updateQueryString+=joinList.get(i).getJoinString()+" ";
			}
		}
		updateQueryString+="set ";
		for(int i=0;i<fields.size();i++) {
			Column column=fields.get(i);
			updateQueryString+=column.getTableName()+"."+column.getColumnName()+"=?";
			if(i!=fields.size()-1) {
				updateQueryString+=",";
			}
		}

		updateQueryString+=" ";
		
		if(criteria!=null) {
			updateQueryString+="where "+criteria.getCriteria()+" ";
		}
		
		updateQueryString+=";";
		return updateQueryString;
	}
	
	public static void main(String args[]) {
		UpdateQuery sq=new UpdateQuery("users");
		ArrayList<Column> columns=new ArrayList<Column>();
		Column col1=new Column("users","id",22);
		Column col2=new Column("users","name","seenu");
		Column col3=new Column("sales","id",102);
		Column col4=new Column("sales","price",100);
		columns.add(col1);
		columns.add(col2);
		columns.add(col3);
		columns.add(col4);
		sq.setFields(columns);
		Join join = new Join(col1,new Column("sales","userid"),Join.LEFT_JOIN);
		sq.setJoin(join);
		Criteria criteria=new Criteria(new Column("users","username"),"seenu");
		criteria.setComparator(Criteria.NOTEQUAL);
		sq.setCriteria(criteria);
		Criteria criteria2=new Criteria(new Column("users","id"),Long.valueOf(1000));
		criteria2.setComparator(Criteria.GREATEREQUAL);
	    criteria.and(criteria2);
		
		System.out.println(sq.getUpdateQueryString());
	}

	
}
