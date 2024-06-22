package com.shoppingapp.utils;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

public class AWSSimpleEmailService {
	 private static AmazonSimpleEmailService amazonSimpleEmailService;
	 private static String fromAddress="Royall Mail Service <mail@service.royall.in>";
	 private static final Logger logger=LogManager.getLogger(AWSSimpleEmailService.class);
	 
	 static {
		 AWSStaticCredentialsProvider credentials =
	                new AWSStaticCredentialsProvider(new BasicAWSCredentials(System.getProperty("aws_ses_username"),
	                        System.getProperty("aws_ses_password")));
		 amazonSimpleEmailService=AmazonSimpleEmailServiceClientBuilder.standard()
				    .withCredentials(credentials)
	                .withRegion(Regions.AP_SOUTH_1).build();
	 }
	 
	 public static boolean sendEmail(String toAddress,String subject,String html){
		 try {
			 SendEmailRequest sendEmailRequest = new SendEmailRequest()
	                 .withDestination(
	                         new Destination().withToAddresses(toAddress))
	                 .withMessage(new Message()
	                         .withBody(new Body().withHtml(
	                                 new Content().withCharset("UTF-8").withData(html)))
	                         .withSubject(new Content().withCharset("UTF-8").withData(subject)))
	                 .withSource(fromAddress);
	         amazonSimpleEmailService.sendEmail(sendEmailRequest);
	         
	         return true;
		 }catch (Exception e) {
			    logger.log(Level.ERROR,"Exception while sending mail to ["+toAddress+"]");
				logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
		}
		return false;
	 }
	 
	 public static boolean sendBulkEmail(List<String> toAddresses,String subject,String html){
		 try {
			 SendEmailRequest sendEmailRequest = new SendEmailRequest()
	                 .withDestination(
	                         new Destination().withToAddresses(toAddresses))
	                 .withMessage(new Message()
	                         .withBody(new Body().withHtml(
	                                 new Content().withCharset("UTF-8").withData(html)))
	                         .withSubject(new Content().withCharset("UTF-8").withData(subject)))
	                 .withSource(fromAddress);
	         amazonSimpleEmailService.sendEmail(sendEmailRequest);
	         
	         return true;
		 }catch (Exception e) {
			    logger.log(Level.ERROR,"Exception while sending mail to ["+toAddresses+"]");
				logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
		}
		return false;
	 }
	 
	 
}
