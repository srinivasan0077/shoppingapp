package com.shoppingapp.dbutils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.mysql.cj.MysqlType;
import com.shoppingapp.dbutils.interfaces.SqlDBConnection;

public class DBAdapter {

	private Connection conn;
	
	public DBAdapter(SqlDBConnection conObj) {
		conn=conObj.getConnection();
	}
	
	public DataHolder executeQuery(SelectQuery query) throws SQLException {
		DataHolder dh=new DataHolder();
		String selectQueryString=query.getSelectQueryString();
		System.out.println(selectQueryString);
		PreparedStatement st=conn.prepareStatement(selectQueryString);
		
		
		ArrayList<Object> joinValues=query.getJoinValuesToBePlaced();
		for(int i=0;i<joinValues.size();i++) {
			  Object data=joinValues.get(i);
			  convertAndSetObject(st,i+1,data);
		}
		
		
		if(query.getCriteria()!=null) {
			ArrayList<Object> values=query.getCriteria().getValuesToBePlaced();
			for(int i=0;i<values.size();i++) {
				  Object data=values.get(i);
				  convertAndSetObject(st,i+1,data);
			}
		}
		
		if(query.getCriteria()!=null) {
			ArrayList<Object> values=query.getCriteria().getValuesToBePlaced();
			for(int i=0;i<values.size();i++) {
				  Object data=values.get(i);
				  convertAndSetObject(st,i+1,data);
			}
		}
	    ResultSet rs=st.executeQuery();
	    int rowTracker=0;
	    ResultSetMetaData metadata=rs.getMetaData();
	    
	    while(rs.next()) {
	    	for(int i=0;i<metadata.getColumnCount();i++) {
	    		
	    		Column col=new Column(metadata.getTableName(i+1),metadata.getColumnName(i+1));

	    		if(dh.getTable(col.getTableName())!=null) {
	    			TableHolder th=dh.getTable(col.getTableName());
	    			if(th.getRows().get(rowTracker)!=null) {
	    				Row row=th.getRows().get(rowTracker);
	    				row.getColumns().put(col.getColumnName(),rs.getObject(i+1));
	    			}else {
	    				Row row=new Row();
	    				row.getColumns().put(col.getColumnName(),rs.getObject(i+1));
	    				th.getRows().put(rowTracker, row);
	    			}
	    		}else {
	    			TableHolder th=new TableHolder();
	    			Row row=new Row();
	    			row.getColumns().put(col.getColumnName(),rs.getObject(i+1));
    				th.getRows().put(rowTracker, row);
    				dh.putTable(col.getTableName(), th);
    				
	    		}
	    	}
	    	rowTracker+=1;
	    }
	    return dh;
	}
	
	public String constructInsertQuery(TableHolder th) {
		if(th.getParentTableName()==null) {
			StringBuilder insertQuery=new StringBuilder("insert into ").append(th.getTableName()).append("(");
			StringBuilder values=new StringBuilder(" values(");
			String[] fieldNames=th.getFieldNames();
			for(int i=0;i<fieldNames.length;i++) {
				insertQuery.append(fieldNames[i]);
				values.append("?");
				if(i!=fieldNames.length-1) {
					insertQuery.append(",");
					values.append(",");
				}
			}
			insertQuery.append(")").append(values).append(");");
			
			return insertQuery.toString();
		}else {
			StringBuilder insertQuery=new StringBuilder("insert into ").append(th.getTableName()).append("(");
			StringBuilder selectQuery=new StringBuilder(" select ");
			String[] fieldNames=th.getFieldNames();
			String[] parentUniqueFields=th.getParentUniqueFields();
			for(int i=0;i<fieldNames.length;i++) {
				insertQuery.append(fieldNames[i]);
				if(i!=fieldNames.length-1) {
					insertQuery.append(",");
				}else {
					insertQuery.append(") ");
				}
				
				if(i==th.getPkFieldIndex()) {
					selectQuery.append(th.getParentPkName());
				}else {
					selectQuery.append("?");
				}
				if(i!=fieldNames.length-1) {
					selectQuery.append(",");
				}
			}
			selectQuery.append(" from ").append(th.getParentTableName()).append(" where ");
			for(int i=0;i<parentUniqueFields.length;i++) {
				String fieldName=parentUniqueFields[i];
				if(th.hasSubQuery(parentUniqueFields[i])) {
					selectQuery.append(fieldName).append("=(").append(th.getSubQuery(fieldName).getSelectQueryString()).append(")");
				}else {
				 selectQuery.append(parentUniqueFields[i]).append("=?");
				}
				if((i+1)<parentUniqueFields.length) {
					selectQuery.append(" and ");
				}
			}
			insertQuery.append(selectQuery).append(";");
			return insertQuery.toString();
		}
		
		
	}
	
	public void createPreparedStatements(ArrayList<TableHolder> ths,LinkedHashMap<String, PreparedStatement> psts,Row parentRow) throws SQLException {
		
		for(int n=0;n<ths.size();n++) {
			TableHolder th=ths.get(n);
		 Map<Integer,Row> rowMap;
		 if(th.getParentTableName()==null) {
			 rowMap=th.getRows();
		 }else {
			 rowMap=parentRow.getChilds().get(th.getTableName());
		 }
		 
		 String[] fields=th.getFieldNames();
		 PreparedStatement pt;
		 
		 if(psts.get(th.getTableName())==null) {
			 System.out.println(constructInsertQuery(th));
			 pt=conn.prepareStatement(constructInsertQuery(th));
			 psts.put(th.getTableName(), pt);
		 }else {
			 pt=psts.get(th.getTableName());
		 }
		 
		 for(int i=0;i<rowMap.size();i++) {

			 Row row=rowMap.get(i);
			 int temp=0,tracker=0;
			 for(int j=0;j<fields.length;j++) {
				 if(th.getPkFieldIndex()!=null && j==th.getPkFieldIndex()) {
					 continue;
				 }
				 Object obj=row.get(fields[j]);
				 convertAndSetObject(pt,tracker+1,obj);
				 tracker+=1;
				 temp=tracker;
			 }
			 if(th.getParentTableName()!=null) {
				 if(parentRow!=null) {
					 String[] parentUniqueFields=th.getParentUniqueFields();
					 for(int j=0;j<parentUniqueFields.length;j++) {
						 if(th.hasSubQuery(parentUniqueFields[j])) {
							   if(th.getSubQuery(parentUniqueFields[j]).getCriteria()!=null) {
									ArrayList<Object> values=th.getSubQuery(parentUniqueFields[j]).getCriteria().getValuesToBePlaced();
									for(int k=0;k<values.size();k++) {
										  Object data=values.get(k);
										  convertAndSetObject(pt,temp+1,data);
										  temp+=1;
									}
							    }
							    
							    continue;
						 }		 
						 Object obj=parentRow.get(parentUniqueFields[j]);
						 convertAndSetObject(pt,temp+1,obj);
						 temp+=1;
						 
					 }
				 }
			 }
			 if(th.getChilds()!=null && row.getChilds().size()!=0) {
				 
				   createPreparedStatements(th.getChilds(), psts,row);
				 
			 }
			 pt.addBatch();
		 }	
		 
		
		}
		
		
	}
	
	public void persistData(TableHolder th) throws Exception {

		ArrayList<TableHolder> ths=new ArrayList<TableHolder>();
		ths.add(th);
		LinkedHashMap<String, PreparedStatement> psts=new LinkedHashMap<String, PreparedStatement>();
		createPreparedStatements(ths, psts,null);
		beginTxn();
		try {
			for(String key : psts.keySet()) {
				PreparedStatement pt=psts.get(key);
				pt.executeBatch();
				
			}
			commitTxn();
		}catch (Exception e) {
			// TODO: handle exception
			revertTxn();
			throw e;
		}
		
		
	}
	
	public void beginTxn() throws SQLException {
		conn.setAutoCommit(false);
	}
	
	public void commitTxn() throws SQLException {
		conn.commit();
	}
	
	public void revertTxn() throws SQLException {
		conn.rollback();
	}
	
	private void convertAndSetObject(PreparedStatement st,int index,Object val) throws SQLException {
		if(val instanceof String) { 
			  st.setString(index, (String)val);
		  }else if(val instanceof Long) { 
			  st.setLong(index, (Long)val);
		  }else if(val instanceof Integer) { 
			  st.setInt(index, (Integer)val); 
	      }else if(val instanceof Boolean) {
	    	  st.setBoolean(index, (Boolean)val); 
	      }else if(val instanceof Character){
			   st.setString(index, String.valueOf(val));
		  }else if(val instanceof Date) {
			   st.setDate(index, new java.sql.Date(((Date)val).getTime()));
		  }else {
			  st.setNull(index,MysqlType.FIELD_TYPE_NULL);
		  }
	}
	
	public int updateData(UpdateQuery uq) throws SQLException{
		
		PreparedStatement st = conn.prepareStatement(uq.getUpdateQueryString());
		int temp=0;
		if(uq.getFields()!=null) {
			ArrayList<Column> fields=uq.getFields();
			for(int i=0;i<fields.size();i++) {
				  Object data=fields.get(i).getValue();
				  convertAndSetObject(st,temp+1,data);
				  temp+=1;
			}
		}
		
		if(uq.getCriteria()!=null) {
			ArrayList<Object> values=uq.getCriteria().getValuesToBePlaced();
			for(int i=0;i<values.size();i++) {
				  Object data=values.get(i);
				  convertAndSetObject(st,temp+1,data);
				  temp+=1;
			}
		}
		
		return st.executeUpdate();
	}
	
	public int deleteData(DeleteQuery deletequery) throws SQLException {
		 PreparedStatement st = conn.prepareStatement(deletequery.getDeleteQuery());
		 if(deletequery.getCriteria()!=null) {
			 ArrayList<Object> values=deletequery.getCriteria().getValuesToBePlaced();
			 for(int i=0;i<values.size();i++) {
				  Object data=values.get(i);
				  convertAndSetObject(st,i+1,data);
			}
		 }
		 
		 return st.executeUpdate();
	}
	
/**
	public static void main(String args[]) {
		ClassPathXmlApplicationContext factory=new ClassPathXmlApplicationContext("config/spring.xml");
		DBAdapter adpter=(DBAdapter)factory.getBean("dbadapter");
		try {
			
			SelectQuery sq=new SelectQuery("emailacc");
		
			Join join = new Join(new Column("emailacc","uid"),new Column("sales","userid"),Join.INNER_JOIN);
			sq.setJoin(join);
			System.out.println(sq.getSelectQueryString());
			DataHolder dh=adpter.executeQuery(sq);
			HashMap<Integer,Row> rows=dh.getTable("emailacc").getRows();
			HashMap<Integer,Row> rows2=dh.getTable("sales").getRows();
			for(int i=0;i<rows.size();i++) {
				Row row=rows.get(i);
				System.out.println((String)row.get("emailid"));
				System.out.println((Long)row.get("uid"));
			}
			for(int i=0;i<rows2.size();i++) {
				Row row=rows2.get(i);
				System.out.println((Long)row.get("price"));
				System.out.println((Long)row.get("userid"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			factory.close();
			
			try {
				adpter.closeConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	}
	public static void main(String args[]) {
		ClassPathXmlApplicationContext factory=new ClassPathXmlApplicationContext("config/spring.xml");
		DBAdapter adpter=(DBAdapter)factory.getBean("dbadapter");
		try {
			String[] fieldName= {"emailid","username"};
			TableHolder th=new TableHolder();
			HashMap<Integer, Row> parentrowMap=new HashMap<Integer, Row>();			
			Row row=new Row();
			Row roww=new Row();
			row.getColumns().put("emailid","unique6@gmail.com");
			row.getColumns().put("username","unique6");
			roww.getColumns().put("emailid","unique7@gmail.com");
			roww.getColumns().put("username","unique7");
			HashMap<Integer, Row> rowMap=new HashMap<Integer, Row>();
			Row row2=new Row();
			row2.getColumns().put("price", 5000);
			rowMap.put(0, row2);
			HashMap<Integer, Row> rowMap2=new HashMap<Integer, Row>();
			Row row3=new Row();
			row3.getColumns().put("product", "tshirt");
			rowMap2.put(0, row3);
			
			row.getChilds().put("newtable",rowMap2);
			row.getChilds().put("sales",rowMap);
			
			parentrowMap.put(0, row);
			parentrowMap.put(1, roww);
			th.setRows(parentrowMap);
			th.setTableName("emailacc");
			th.setFieldNames(fieldName);
			
			ArrayList<TableHolder> childs=new ArrayList<TableHolder>(); 
			TableHolder childth=new TableHolder();
			String[] fieldName2={"userid","price"};
			String[] parentUnique= {"emailid"};
			childth.setFieldNames(fieldName2);
			childth.setParentUniqueFields(parentUnique);
			childth.setParentPkName("uid");
			childth.setParentTableName("emailacc");
			childth.setPkFieldIndex(0);
			childth.setTableName("sales");
			childs.add(childth);
			
			
			TableHolder childth2=new TableHolder();
			String[] fieldName3={"userid","product"};
			String[] parentUnique2= {"emailid"};
			childth2.setFieldNames(fieldName3);
			childth2.setParentUniqueFields(parentUnique2);
			childth2.setParentPkName("uid");
			childth2.setParentTableName("emailacc");
			childth2.setPkFieldIndex(0);
			childth2.setTableName("newtable");
			childs.add(childth2);
			th.setChilds(childs);
			adpter.persistData(th);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			factory.close();
			
			try {
				adpter.closeConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	}**/
	
	public void closeConnection() throws SQLException {
		conn.close();
	}
}
