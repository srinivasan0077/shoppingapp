package com.shoppingapp.authUtils;



import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

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

public class AuthUtil implements AuthUtilInterface {
	

	private DBAdapter adapter;
	
	public AuthUtil(DBAdapter adapter){
		this.adapter=adapter;
	}

	public String checkUser(String email) {
		
	    try {
			User user=getUserBy("email",email);
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
	
	
	public String createUser(User user) {
		
		try {
			String check=checkUser(user.getEmail());
			if(check.equals(NOT_EXIST)) {
				
				//Configurations
				
				String[] verifiedUserFields={"email","salt",
						"password","firstname","lastname","createdat","modifiedat","roleId"};

				TableHolder verifiedUsers=new TableHolder("verified_users",verifiedUserFields);

                //data
				Row row=new Row();
				Map<String,Object> columns=row.getColumns();
				String salt=RandomTokenGenerator.getAlphaNumericString(20);
	            String password=EncryptionUtil.getSHA(salt+user.getPassword());
				row.getColumns().put("email",user.getEmail());
				columns.put("salt",salt);
	            columns.put("password",password);
	            columns.put("firstname",user.getFirstname());
	            columns.put("lastname",user.getLastname());
	            columns.put("createdat",new Date().getTime());
	            columns.put("modifiedat",new Date().getTime());
	            columns.put("roleId",Long.valueOf(1));
				
	            verifiedUsers.setRow(row);
				adapter.persistData(verifiedUsers);
				return CREATED;
			}else {
				return EXIST;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return FAILED;
	}

	public void updateUser(User user) throws Exception {

			UpdateQuery uq=new UpdateQuery("verified_users");
			ArrayList<Column> cols=new ArrayList<Column>();
			String salt=RandomTokenGenerator.getAlphaNumericString(20);
            String password=EncryptionUtil.getSHA(salt+user.getPassword());
			cols.add(new Column("verified_users","firstname",user.getFirstname()));
			cols.add(new Column("verified_users","lastname",user.getLastname()));
			cols.add(new Column("verified_users","password",password));
			cols.add(new Column("verified_users","modifiedat",new Date().getTime()));
			cols.add(new Column("verified_users","salt",salt));
			
			uq.setFields(cols);
			Criteria criteria=new Criteria(new Column("verified_users","id"),user.getUserid());
			criteria.setComparator(Criteria.EQUAL);
			uq.setCriteria(criteria);
			adapter.updateData(uq);
		
	}
	
	public void authenticate(User user) throws Exception {
		User authenticatedUser=getUserBy("email",user.getEmail());
		if(authenticatedUser==null) {
			throw new ExceptionCause("User email '"+user.getEmail()+"' not exist!",HttpStatus.BAD_REQUEST);
		}
			
		String password=EncryptionUtil.getSHA(authenticatedUser.getSalt()+user.getPassword());
		if(!password.equals(authenticatedUser.getPassword())) {
			throw new ExceptionCause("Authentication failed for user with email '"+user.getEmail()+"'!",HttpStatus.BAD_REQUEST);
		}
		
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
	
	public void closeConnection() {
		try {
		    adapter.closeConnection();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
