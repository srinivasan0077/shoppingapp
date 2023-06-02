package com.shoppingapp.utils;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.springframework.util.ResourceUtils;

public class ApplicationProperties {

	private static Properties properties;
	
	static {
		properties=new Properties();
		try {
			System.out.println("hello");
			properties.load(new FileReader(ResourceUtils.getFile(
				      "classpath:properties/application.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String key) {
	   return properties.getProperty(key);
	}
	
	public static void main(String args[]) {
		System.out.println(ApplicationProperties.getProperty("email_service_key"));
	}
}
