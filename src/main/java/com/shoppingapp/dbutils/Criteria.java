package com.shoppingapp.dbutils;

import java.util.ArrayList;
import java.util.HashMap;


public class Criteria {

	public static HashMap<Integer,String> conditions=null;
	public static HashMap<Integer,String> comparators=null;
	private Column column;
	private ArrayList<Object> dataCollection=null;
	private Integer comparator=null;
	private Integer condition=null;
	private Criteria rightAndCriteria=null;
    private Criteria rightOrCriteria=null;
    private SelectQuery subQuery;
    
	public static Integer AND=0;
	public static Integer OR=1;
	public static Integer EQUAL=0;
	public static Integer NOTEQUAL=1;
	public static Integer IN=2;
	public static Integer NOTIN=3;
	public static Integer GREATER=4;
	public static Integer LESSER=5;
	public static Integer GREATEREQUAL=6;
	public static Integer LESSEREQUAL=7;
	public static Integer LIKE=8;
	public static Integer NOTLIKE=9;
	
	static {
		loadComparatorsAndConditions();
	}
	
	public Criteria(Column column,Object data) {
		this.column=column;
		this.dataCollection=new ArrayList<Object>();
		this.setData(data);
		
	}

	public Criteria() {
		this.dataCollection=new ArrayList<Object>();
	}
	
	public Criteria(Column column) {
		this.column=column;
		this.dataCollection=new ArrayList<Object>();
	}
	
	public Criteria(Column column,SelectQuery subquery) {
		this.column=column;
		this.subQuery=subquery;
	}
	
	public Column getColumn() {
		return column;
	}


	public void setColumn(Column column) {
		this.column = column;
	}


	public void setData(Object data) {
		this.dataCollection.add(data);
	}

	public ArrayList<Object> getDataCollection() {
		return dataCollection;
	}

	public void setDataCollection(ArrayList<Object> dataCollection) {
		this.dataCollection = dataCollection;
	}

	public Integer getComparator() {
		return comparator;
	}

	public void setComparator(Integer comparator) {
		this.comparator = comparator;
	}

	public Integer getCondition() {
		return condition;
	}

	public void setCondition(Integer condition) {
		this.condition = condition;
	}
	
	public void and(Criteria criteria) {
		rightOrCriteria=null;
		rightAndCriteria=criteria;
	}
	
	public void or(Criteria criteria) {
		rightOrCriteria=criteria;
		rightAndCriteria=null;
	}
	
	public static void loadComparatorsAndConditions() {
		
		comparators=new HashMap<Integer, String>();
		conditions=new HashMap<Integer, String>();
		String arr[]= {"=","!=","IN","NOT IN",">","<",">=","<=","like","not like"};
		String arr2[]= {"AND","OR"};
		for(int i=0;i<(arr.length>arr2.length?arr.length:arr2.length);i++) {
			if(i<arr.length)
			    comparators.put(i, arr[i]);
			if(i<arr2.length)
				conditions.put(i, arr2[i]);
		}
	}
	
	

	private String getCriteriaString() {
		StringBuilder criteriaString=new StringBuilder();
		criteriaString.append(this.column.getTableName()).append(".").append(this.column.getColumnName()).append(" ")
		.append(comparators.get(this.comparator)).append(" ");
		if(dataCollection!=null) {
		   if(dataCollection.size()==1) {
			      criteriaString.append("?");			 
		   }else {
			    criteriaString.append("(");
				for(int i=0;i<dataCollection.size();i++) {
					criteriaString.append("?");
					if(i!=dataCollection.size()-1) {
						criteriaString.append(",");
					}else {
						criteriaString.append(")");
					}
				}
		   }
		}
		
		if(subQuery!=null) {
			criteriaString.append("(");
			criteriaString.append(subQuery.getSelectQueryString());
			criteriaString.append(")");
		}
		
		return criteriaString.toString();
	}
	
	
	public String getCriteria() {
		StringBuilder currentCriteria=new StringBuilder("( ");
		if(rightAndCriteria!=null) {
			 currentCriteria.append(getCriteriaString()).append(" ")
			 .append(conditions.get(Criteria.AND)).append(" ").append(rightAndCriteria.getCriteria()).append(" )");
		}else if(rightOrCriteria!=null) {
			currentCriteria.append(getCriteriaString()).append(" ")
			.append(conditions.get(Criteria.OR)).append(" ").append(rightOrCriteria.getCriteria()).append(" )");
		}else {
			currentCriteria.append(getCriteriaString()).append(" )");
		}
		return currentCriteria.toString();
	}
	
	public ArrayList<Object> getValuesToBePlaced(){
		  ArrayList<Object> values=new ArrayList<Object>();
		  addValues(values);
		  return values;
	}
	
	public void addValues(ArrayList<Object> values){
		if(dataCollection!=null) {
		   if(dataCollection.size()==1) {
			     values.add(dataCollection.get(0));
	
		   }else {
				for(int i=0;i<dataCollection.size();i++) {
					values.add(dataCollection.get(i));	
				}
		   }
	    }
		
		if(subQuery!=null) {
			if(subQuery.getCriteria()!=null) {
				subQuery.getCriteria().addValues(values);
			}
		}
		
		if(rightAndCriteria!=null) {
		      rightAndCriteria.addValues(values);
		}else if(rightOrCriteria!=null) {
		      rightOrCriteria.addValues(values);
		}
	}
	
	@Override
	public String toString() {
		
		return this.getCriteria();
	}
	
	public static void main(String args[]) {
		Criteria criteria1=new Criteria(new Column("PRODUCT_VARIANT","name"), "shirt");
		criteria1.setComparator(Criteria.EQUAL);
		Criteria criteria2=new Criteria(new Column("PRODUCT_VARIANT","itemId"), 34);
		criteria2.setComparator(Criteria.EQUAL);
		criteria1.and(criteria2);
		Criteria criteria3=new Criteria(new Column("PRODUCT_VARIANT","itemId"), 44);
		criteria3.setComparator(Criteria.EQUAL);
		criteria3.or(criteria1);
		System.out.println(criteria3.getCriteria());
		System.out.println(criteria3.getValuesToBePlaced());
	}

	
}
