package com.shoppingapp.dbutils;

import java.util.ArrayList;
import java.util.HashMap;

import com.shoppingapp.utils.BeanValidator;


public class Criteria {

	public static HashMap<Integer,String> conditions=null;
	public static HashMap<Integer,String> comparators=null;
	private Column column;
	private ArrayList<Object> dataCollection=null;
	private Integer comparator=null;
	private Integer condition=null;
	private Criteria rightAndCriteria=null;
    private Criteria rightOrCriteria=null;
    
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

		String criteriaString=this.column.getTableName()+"."+this.column.getColumnName()+" "+
				comparators.get(this.comparator)+" ";
		if(dataCollection!=null) {
		   if(dataCollection.size()==1) {

			     return criteriaString+"?";
				
//				  Object data=dataCollection.get(0); 
//				  if(data instanceof String) { 
//					  return criteriaString+"\""+SanitizeString.escapeQuotes((String)data)+"\""; }else
//				  if(data instanceof Long) { 
//					  return criteriaString+((Long)data); 
//				  }else if(data instanceof Integer) { 
//					  return criteriaString+((Integer)data); 
//			      }else if(data instanceof Boolean) {
//			    	  return criteriaString+((Boolean)data); 
//			      }
				 
		   }else {
			    criteriaString+="(";
				for(int i=0;i<dataCollection.size();i++) {
					
					/*
					 * if(dataCollection.get(i) instanceof String) {
					 * criteriaString+="\""+SanitizeString.escapeQuotes((String)dataCollection.get(i
					 * ))+"\""; }else if(dataCollection.get(i) instanceof Long) {
					 * criteriaString+=(Long)dataCollection.get(i); }else if(dataCollection.get(i)
					 * instanceof Integer) { criteriaString+=(Integer)dataCollection.get(i); }else
					 * if(dataCollection.get(i) instanceof Boolean) {
					 * criteriaString+=(Boolean)dataCollection.get(i); }else
					 * if(dataCollection.get(i) instanceof Character){
					 * criteriaString+="\""+(Character)dataCollection.get(i)+"\""; }
					 */
					criteriaString+="?";
					if(i!=dataCollection.size()-1) {
						criteriaString+=",";
					}else {
						criteriaString+=")";
					}
				}
				return criteriaString;
		   }
		}
		
		return criteriaString;
	}
	
	
	public String getCriteria() {
		String currentCriteria="( ";
		if(rightAndCriteria!=null) {
			 currentCriteria+=getCriteriaString()+" "
		+conditions.get(Criteria.AND)+" "+rightAndCriteria.getCriteria()+" )";
		}else if(rightOrCriteria!=null) {
			currentCriteria+=getCriteriaString()+" "
						+conditions.get(Criteria.OR)+" "+rightOrCriteria.getCriteria()+" )";
		}else {
			currentCriteria+=getCriteriaString()+" )";
		}
		return currentCriteria;
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
