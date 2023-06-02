package com.shoppingapp.utils;


import java.util.ArrayList;
import java.util.List;

import com.shoppingapp.entities.Mail;

import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;




public class MailServiceUtil {
	
	private static ApiClient defaultClient;
	private static ApiKeyAuth apiKey;
	private static SendSmtpEmailSender sender;
	
	static {
		
		 sender=new SendSmtpEmailSender();
		 sender.setName(ApplicationProperties.getProperty("sender_name"));
		 sender.setEmail(ApplicationProperties.getProperty("sender_email"));
		 defaultClient = Configuration.getDefaultApiClient();
         apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
	     apiKey.setApiKey(ApplicationProperties.getProperty("email_service_key"));
	}
	
	public static boolean send(Mail mail){  
		try {
	        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
	        SendSmtpEmail emailContainer=constructSmtpEmail(mail);
			apiInstance.sendTransacEmail(emailContainer);
			return true;
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
        
	}  

	private static SendSmtpEmail constructSmtpEmail(Mail mail) {
		SendSmtpEmail emailContainer=new SendSmtpEmail();
		
		List<SendSmtpEmailTo> receivers=new ArrayList<SendSmtpEmailTo>();
        List<String> receiverString=mail.getTo();
        for(int i=0;i<receiverString.size();i++) {
        	SendSmtpEmailTo receiver=new SendSmtpEmailTo();
        	receiver.setEmail(receiverString.get(i));
        	receivers.add(receiver);
        }
		
        emailContainer.setSender(sender);
        emailContainer.setTo(receivers);
        emailContainer.setSubject(mail.getSubject());
        emailContainer.setTextContent(mail.getMessage());
        
		return emailContainer;
	}
	
	public static void main(String [] args){  
		List<String> tos=new ArrayList<String>();
        tos.add("seenumass3000@gmail.com");
		MailServiceUtil.send(new Mail(tos,"Otp to verify email","890292"));
		 
    }  
}
