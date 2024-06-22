package com.shoppingapp.paymentservice;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shoppingapp.utils.ExceptionCause;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;


public class StripePaymentUtil {

	private static final Logger logger=LogManager.getLogger(StripePaymentUtil.class);
	
	static {
		Stripe.apiKey=System.getProperty("stripe_secret_key");
	}
	
	public static PaymentIntent createPaymentIntent(Long orderId,int price,boolean isCart) throws StripeException {
		PaymentIntentCreateParams params=PaymentIntentCreateParams.builder()
				.setCurrency("INR")
				.setAmount(Long.valueOf(price))
				.putMetadata("orderId",String.valueOf(orderId))
				.putMetadata("isCart",String.valueOf(isCart))
				.addPaymentMethodType("card")
				.build();
		
		return PaymentIntent.create(params);
	} 
	
	public static PaymentStatus getPaymentStatus(String paymentId){
		try {
			PaymentIntent intent=PaymentIntent.retrieve(paymentId);
			StripePaymentUtil.PaymentStatus status=new StripePaymentUtil().new PaymentStatus();
			if(intent!=null) {
				status.setStatus(intent.getStatus());
				status.setIsCart(Boolean.valueOf(intent.getMetadata().get("isCart")));
				return status;
			}
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
		}
		return null;
	}
	
	public class PaymentStatus{
		private String status;
		private boolean isCart;
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public boolean getIsCart() {
			return isCart;
		}
		public void setIsCart(boolean isCart) {
			this.isCart = isCart;
		}
		
	}

}
