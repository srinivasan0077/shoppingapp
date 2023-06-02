package com.shoppingapp.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.shoppingapp.dbutils.interfaces.SqlDBConnection;


public class MySqlDBConnection implements SqlDBConnection {

	private String url;
	private String username;
	private String password;
	
	public MySqlDBConnection(String className,String url,String username,String password) throws ClassNotFoundException {
		Class.forName(className);
		this.url=url;
		this.username=username;
		this.password=password;
	}
	
	public Connection getConnection() {
		
		try {
			Connection conn=DriverManager.getConnection(url, username,password);
			return conn;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
}
