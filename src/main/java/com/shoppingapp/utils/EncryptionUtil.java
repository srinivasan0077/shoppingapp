package com.shoppingapp.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtil {
	
	    public static String getSHA( String input ) throws NoSuchAlgorithmException 
	    {   
	         
	        MessageDigest md = MessageDigest.getInstance( "SHA-256" ) ;   
	    
	        
	        return toHexString(md.digest( input.getBytes( StandardCharsets.UTF_8 ))) ;   
	    }  
	    
	    public static String toHexString( byte[ ] hash )  
	    {  
	       
	        BigInteger number = new BigInteger( 1, hash ) ;   
	         
	        StringBuilder hexString = new StringBuilder( number.toString( 16 ) ) ;   
	    
	        // Pad with leading zeros  
	        while ( hexString.length( ) < 32 )   
	        {   
	            hexString.insert( 0,  " 0 " ) ;   
	        }   
	        return hexString.toString( ) ;   
	    } 
	    
	    public static String getSHA1(String input) throws NoSuchAlgorithmException {
	    	 MessageDigest md = MessageDigest.getInstance( "SHA-1" ) ;      
		     return toHexString(md.digest( input.getBytes( StandardCharsets.UTF_8 ))) ;
	    }
	  
}
