package com.shoppingapp.authUtils;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import com.shoppingapp.dbutils.Column;
import com.shoppingapp.dbutils.Criteria;
import com.shoppingapp.dbutils.DBAdapter;
import com.shoppingapp.dbutils.DataHolder;
import com.shoppingapp.dbutils.Row;
import com.shoppingapp.dbutils.SelectQuery;
import com.shoppingapp.dbutils.TableHolder;
import com.shoppingapp.dbutils.UpdateQuery;
import com.shoppingapp.entities.User;
import com.shoppingapp.utils.EncryptionUtil;
import com.shoppingapp.utils.ExceptionCause;
import com.shoppingapp.utils.RandomTokenGenerator;
import com.shoppingapp.utils.ThreadLocalUtil;

import jakarta.mail.PasswordAuthentication;

public class AuthUtil implements AuthUtilInterface {
	

	private DBAdapter adapter;
	private static final Logger logger=LogManager.getLogger(AuthUtil.class);
	private static String editableFields="firstname|lastname|phone";
	private static Map<String,String> dbFields=new HashMap<>();
	
	static {
		dbFields.put("firstname","firstname");
		dbFields.put("lastname","lastname");
		dbFields.put("password","password");
		dbFields.put("phone","phoneno");
	}
	
	public AuthUtil(DBAdapter adapter){
		this.adapter=adapter;
	}

	public String checkUserBy(String columname,String value) {
		
	    try {
			User user=getUserBy(columname,value);
	        if(user==null){
	           return NOT_EXIST;
	        }else {
	           return EXIST;
	        }
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return FAILED;
	}
	
	
	
    public String createVerifiedUser(User user) {
    	try {
			String check=checkUserBy("email",user.getEmail());
			if(check.equals(NOT_EXIST)) {
				String[] verifiedUserFields={"email","createdat","modifiedat","roleId"};
				TableHolder verifiedUsers=new TableHolder("verified_users",verifiedUserFields);

                //data
				Row row=new Row();
				Map<String,Object> columns=row.getColumns();
				row.getColumns().put("email",user.getEmail());
	            columns.put("createdat",new Date().getTime());
	            columns.put("modifiedat",new Date().getTime());
	            columns.put("roleId",Long.valueOf(1));
				
	            verifiedUsers.setRow(row);
				adapter.persistData(verifiedUsers);
			}
				
		    return CREATED;
    		
    	}catch (Exception e) {
			logger.log(Level.ERROR,ExceptionCause.getStackTrace(e));
		}
		return FAILED;
	}
	

	public boolean isAdmin(Long id) {
		User user = getUserBy("id", id);
		if(user!=null) {
			if(user.getRoleid()==2) {
				return true;
			}
		}
		return false;
	}
	
	public User getUserBy(String key,Object value) {

		try {
			SelectQuery sq=new SelectQuery("verified_users");
		    Criteria criteria=new Criteria(new Column("verified_users",key),value);
		    criteria.setComparator(Criteria.EQUAL);
	        sq.setCriteria(criteria);
	        DataHolder dh=adapter.executeQuery(sq);

	        TableHolder th=dh.getTable("verified_users");
	        if(th!=null) {
	        	if(th.getRows().size()==1 ) {
	        	    User user=new User();
	        		Row row=th.getRows().get(0);
	        		user.setEmail((String)row.get("email"));
	        		user.setUserid((Long)row.get("id"));
                    user.setPassword((String)row.get("password"));
	        		user.setPhone((String)row.get("phoneno"));
	        		user.setFirstname((String)row.get("firstname"));
	        		user.setLastname((String)row.get("lastname"));
	        		user.setRoleid((Long)row.get("roleId"));
	        		user.setSalt((String)row.get("salt"));   	
	        	    return user;
	        	}
	        }
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public User getAccountOfUser() throws Exception{
		User user=ThreadLocalUtil.currentUser.get();
		if(user==null) {
			throw new ExceptionCause("Unauthorized request!",HttpStatus.UNAUTHORIZED);
		}
		User dbUser=getUserBy("id",user.getUserid());
		User accountUser=new User();
		accountUser.setFirstname(dbUser.getFirstname());
		accountUser.setLastname(dbUser.getLastname());
		accountUser.setEmail(dbUser.getEmail());
		accountUser.setPhone(dbUser.getPhone());
		return accountUser;
	}
	
	public void updateField(String fieldName,String fieldValue)throws Exception {
		User user=ThreadLocalUtil.currentUser.get();
		if(user==null) {
			throw new ExceptionCause("Unauthorized request!",HttpStatus.UNAUTHORIZED);
		}
		
		if(fieldName==null || !Pattern.matches(editableFields, fieldName)) {
			throw new ExceptionCause("Invalid Input!",HttpStatus.BAD_REQUEST);
		}
		
		if("firstname".equals(fieldName) || "lastname".equals(fieldName)) {
			if(fieldValue==null || fieldValue.contains(" ") || (fieldValue.length()<1 && fieldValue.length()>20)) {
				throw new ExceptionCause("Invalid Input!",HttpStatus.BAD_REQUEST);
			}
		}
		
		if("phone".equals(fieldName)) {
			if(!Pattern.matches("[0-9]{10}", fieldValue)) {
				throw new ExceptionCause("Invalid Input!",HttpStatus.BAD_REQUEST);
			}
		}
		
		UpdateQuery uq=new UpdateQuery("verified_users");
		List<Column> fields=new ArrayList<>();
		fields.add(new Column("verified_users",dbFields.get(fieldName),fieldValue));
		uq.setFields(fields);
		Criteria criteria=new Criteria(new Column("verified_users","id"),user.getUserid());
		criteria.setComparator(Criteria.EQUAL);
		uq.setCriteria(criteria);
		adapter.updateData(uq);
		

	}
	
	public void closeConnection() {
		try {
		    adapter.closeConnection();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
