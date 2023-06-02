package com.shoppingapp.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.http.HttpStatus;

import com.shoppingapp.customAnnotations.ForeignKeyField;




public class BeanValidator {

	//Use this class to clean up string
	
	public static void setNullForEmptyString(Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Field fields[]=obj.getClass().getDeclaredFields();
		
		for(int i=0;i<fields.length;i++) {
			Field field=fields[i];
			if(field.getType().equals(String.class)) {
	            String setMethodName="set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
	            String getMethodName="get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
	            Method getMethod=obj.getClass().getMethod(getMethodName,new Class[0]);
	            Class param[]=new Class[1];
                param[0]=String.class;
                Method setMethod=obj.getClass().getMethod(setMethodName,param);
                String value=(String)getMethod.invoke(obj,new Object[0]);
				if(value!=null) {
					if(value.trim().length()==0) {
						Object setVals[]=new Object[1];
						setVals[0]=(String)null;
						setMethod.invoke(obj,setVals);
					}
				}
			}
		}
	}
	
	public static void checkForeignKeyField(Object obj) throws Exception {
		
		Field fields[]=obj.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++) {
			Field field=fields[i];
			if(field.isAnnotationPresent(ForeignKeyField.class)) {
	            ForeignKeyField foreignFieldInfo=field.getAnnotation(ForeignKeyField.class);
				String getFieldMethodName="get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
				Method getFieldMethod=obj.getClass().getMethod(getFieldMethodName,new Class[0]);
				
				Object fkObject=getFieldMethod.invoke(obj,new Object[0]);
				
				if(fkObject==null) {
					return;
				}
				
				Field fkField=fkObject.getClass().getDeclaredField(foreignFieldInfo.name());
				String getfkFieldMethodName="get" + fkField.getName().toUpperCase().charAt(0) + fkField.getName().substring(1);
				Method getfkFieldMethod=fkObject.getClass().getMethod(getfkFieldMethodName,new Class[0]);
				Object fkFieldValue=getfkFieldMethod.invoke(fkObject,new Object[0]);
				if(fkFieldValue==null) {
					throw new ExceptionCause(fkField.getName()+" under "+field.getName()+" cannot be null",HttpStatus.BAD_REQUEST);
				}
			}
		}
	}
	
	
}
