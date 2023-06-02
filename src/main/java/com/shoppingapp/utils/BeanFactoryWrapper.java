package com.shoppingapp.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanFactoryWrapper {

	public static ApplicationContext factory;
	
	static {
		factory=new ClassPathXmlApplicationContext("config/spring.xml");
	}
	
	public static ApplicationContext getBeanFactory() {
		  return factory;
	}
}
