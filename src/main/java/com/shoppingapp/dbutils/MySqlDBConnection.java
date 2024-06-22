package com.shoppingapp.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shoppingapp.dbutils.interfaces.SqlDBConnection;
import com.shoppingapp.utils.ExceptionCause;


public class MySqlDBConnection implements SqlDBConnection {

	private static final Logger logger=LogManager.getLogger(MySqlDBConnection.class);
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
			e.printStackTrace();
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
		}
		
		return null;
	}
	
}
