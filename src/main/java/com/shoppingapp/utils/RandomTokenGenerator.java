package com.shoppingapp.utils;

public class RandomTokenGenerator {

	 private static String AlphaNumericString;
	 private static String NumericString;
	 
	 static {
		 AlphaNumericString="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
		         + "0123456789"
		         + "abcdefghijklmnopqrstuvxyz";
		 NumericString="0123456789";
	 }
	 
	 public static String getAlphaNumericString(int n)
	 {
		  StringBuilder sb = new StringBuilder(n);
		 
		  for (int i = 0; i < n; i++) {
		      int index = (int)(AlphaNumericString.length()* Math.random());
			  sb.append(AlphaNumericString.charAt(index));
		  
	      }
		  return sb.toString();
	 }
	 
	 public static String getOTPString(int n) {
		 
		  StringBuilder sb = new StringBuilder(n);
		 
		  for (int i = 0; i < n; i++) {
		      int index = (int)(NumericString.length()* Math.random());
			  sb.append(NumericString.charAt(index));
		  
	      }
		  return sb.toString();
	 }
}
