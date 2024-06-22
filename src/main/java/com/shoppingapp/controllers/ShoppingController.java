package com.shoppingapp.controllers;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.shoppingapp.entities.Address;
import com.shoppingapp.entities.Cart;
import com.shoppingapp.entities.GetInfo;
import com.shoppingapp.entities.Inventory;

import com.shoppingapp.entities.ProductVariant;
import com.shoppingapp.paymentservice.StripePaymentUtil;
import com.shoppingapp.paymentservice.StripePaymentUtil.PaymentStatus;
import com.shoppingapp.entities.Response;

import com.shoppingapp.shopUtils.ShoppingUtilInterface;

import com.shoppingapp.utils.BeanFactoryWrapper;
import com.shoppingapp.utils.BeanValidator;
import com.shoppingapp.utils.ExceptionCause;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;


@CrossOrigin(origins = {"http://localhost:3000","https://www.royall.in","https://royall.in"},allowCredentials = "true")
@Controller
public class ShoppingController {
	private static final Logger logger=LogManager.getLogger(ShoppingController.class);
	private static final String orderConfirmationEndpointSecret=System.getProperty("endpointSecret");
	
	
	@RequestMapping(path={"/public/api/topics_to_display"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> getTopicsToDisplay(@Valid @RequestBody GetInfo info,BindingResult validationResult){
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> responseJSON;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getTopicsToDisplay(info)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			shopUtil.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/public/api/variants/{variantId}"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getVariantToView(@PathVariable Long variantId){
		ResponseEntity<Response> responseJSON;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			ProductVariant variant=shopUtil.getVariantToView(variantId);
			if(variant==null) {
				throw new ExceptionCause("Variant Not Found!", HttpStatus.NOT_FOUND);
			}
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getVariantToView(variantId)),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			shopUtil.closeConnection();
		}
		return responseJSON;
	}
	

	@RequestMapping(path={"/public/api/items/{itemId}/variants"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getVariantsByItemId(@PathVariable Long itemId){
		ResponseEntity<Response> responseJSON;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getVariantsByItemId(itemId)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			shopUtil.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/public/api/banners"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getVariantsByItemId(){
		ResponseEntity<Response> responseJSON;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getBanners()),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			shopUtil.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/public/api/headers"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getHeaders(){
		ResponseEntity<Response> responseJSON;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getHeaders()),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			shopUtil.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/public/api/headers/{id}"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getHeaderById(@PathVariable Long id){
		ResponseEntity<Response> responseJSON;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getHeaderById(id)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			shopUtil.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/public/api/products/{productId}/variants"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> getVariants(@PathVariable Long productId,@Valid @RequestBody GetInfo info,BindingResult validationResult){
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getProductVariants(info, productId)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/public/api/variants"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> getVariants(@Valid @RequestBody GetInfo info,BindingResult validationResult){
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getProductVariants(info, null)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/auth/api/carts"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> addToCart(@Valid @RequestBody Cart cart,BindingResult validationResult){
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			BeanValidator.checkForeignKeyField(cart);
			shopUtil.addItemToCart(cart);
			response= new ResponseEntity<Response>(new Response("Successfully added to cart!",Response.SUCCESS,null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/auth/api/carts"},method =RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<Response> removeFromCart(@Valid @RequestBody Cart cart,BindingResult validationResult){
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			BeanValidator.checkForeignKeyField(cart);
			shopUtil.removeItemFromCart(cart);
			response= new ResponseEntity<Response>(new Response("Successfully added to cart!",Response.SUCCESS,null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/auth/api/carts"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getCart(){
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			response= new ResponseEntity<Response>(new Response("Successfully added to cart!",Response.SUCCESS,shopUtil.getCartOfUser()),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/auth/api/carts/_count"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getCartCount(){
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			response= new ResponseEntity<Response>(new Response("Successfully added to cart!",Response.SUCCESS,null,shopUtil.getCartCount()),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/public/api/carts/_local"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> getLocalCartDetails(@RequestBody ArrayList<Inventory> inventories){
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			if(inventories.size()>50) {
				throw new ExceptionCause("Local cart limit exceeded!",HttpStatus.BAD_REQUEST);
			}
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getInventories(inventories)),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/auth/api/orders"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> placeOrders(@Valid @RequestBody Address address,BindingResult validationResult,HttpServletRequest request,HttpSession session){
			
		if(address.getAddressId()==null && validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.placeOrder(address,false)),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/auth/api/orders/_cod"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> placeCODOrders(@Valid @RequestBody Address address,BindingResult validationResult,HttpServletRequest request,HttpSession session){
			
		if(address.getAddressId()==null && validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.placeOrder(address,true)),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/auth/api/orders/list"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> getOrdersOfUser(@Valid @RequestBody GetInfo info,BindingResult validationResult){
			
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getOrdersByUserId(info)),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/auth/api/addresses"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getAddressesUsedByUser(){
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getAddressesOfUser()),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/public/api/webhook"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> orderConfirmation(@RequestBody String json,HttpServletRequest request){
	
        Event event = null;

        try {
            event = ApiResource.GSON.fromJson(json, Event.class);
        } catch (Exception e) {
            logger.log(Level.ERROR,"Webhook error while parsing basic request.");
            logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
            return new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
        }
        
        String sigHeader = request.getHeader("Stripe-Signature");
        if(orderConfirmationEndpointSecret != null && sigHeader != null) {

            try {
                event = Webhook.constructEvent(
                    json, sigHeader,orderConfirmationEndpointSecret
                );
            } catch (SignatureVerificationException e) {
                // Invalid signature
                logger.log(Level.ERROR,"Webhook error while validating signature.");
                logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
                return new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
            }
        }
        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            logger.log(Level.ERROR,"Deserialization failed, probably due to an API version mismatch.");
            return new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,null),HttpStatus.OK);
           
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
                try {
                	Map<String, String> metadata=paymentIntent.getMetadata();
                	shopUtil.confirmOrder(Long.valueOf(metadata.get("orderId")));
                	logger.log(Level.INFO,"Payment for " + paymentIntent.getAmount() + " succeeded.");
                }catch (Exception e) {
        			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
        		}
                shopUtil.closeConnection();
                break;
            default:
            	logger.log(Level.INFO,"Unhandled event type: " + event.getType());
            break;
        }

        return new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,null),HttpStatus.OK);
	}
	
	@RequestMapping(path={"/auth/api/orders/{id}"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getOrderById(@PathVariable Long id){
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getOrderById(id)),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			shopUtil.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/auth/api/payment/{id}"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> confirmOrder(@PathVariable String id){
		
		ResponseEntity<Response> response;

		try {
			PaymentStatus status=StripePaymentUtil.getPaymentStatus(id);
			if(status!=null) {
			    response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,status),HttpStatus.OK);
			}else {
				response= new ResponseEntity<Response>(new Response("Invalid Request!",Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
			}
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
        return response;
	}
	
	
}
