package com.shoppingapp.utils;

import com.shoppingapp.entities.User;

public class ThreadLocalUtil {

	public static ThreadLocal<User> currentUser=new ThreadLocal<>();
	
}
