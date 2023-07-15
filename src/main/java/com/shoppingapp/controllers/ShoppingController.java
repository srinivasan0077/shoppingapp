package com.shoppingapp.controllers;

import java.util.List;

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

import com.shoppingapp.entities.Cart;
import com.shoppingapp.entities.GetInfo;
import com.shoppingapp.entities.Inventory;
import com.shoppingapp.entities.Response;
import com.shoppingapp.productUtils.ProductManagementInterface;
import com.shoppingapp.shopUtils.ShoppingUtilInterface;
import com.shoppingapp.utils.BeanFactoryWrapper;
import com.shoppingapp.utils.BeanValidator;
import com.shoppingapp.utils.ExceptionCause;

@CrossOrigin(origins = {"http://localhost:3000"},allowCredentials = "true")
@Controller
public class ShoppingController {
	private static final Logger logger=LogManager.getLogger(ShoppingController.class);
	
	@RequestMapping(path={"/public/api/topics_to_display"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getTopicsToDisplay(){
		ResponseEntity<Response> responseJSON;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getTopicsToDisplay()),HttpStatus.OK);
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
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,shopUtil.getVariantToView(variantId)),HttpStatus.OK);
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
	public ResponseEntity<Response> getHeaders(@PathVariable Long productId,@Valid @RequestBody GetInfo info,BindingResult validationResult){
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
	
	@RequestMapping(path={"/public/api/carts"},method =RequestMethod.POST)
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
	
	@RequestMapping(path={"/public/api/carts"},method =RequestMethod.GET)
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
	
	@RequestMapping(path={"/public/api/carts/_count"},method =RequestMethod.GET)
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
	public ResponseEntity<Response> getLocalCartDetails(List<Inventory> inventories){
		
		ResponseEntity<Response> response;
		ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
		try {
			if(inventories.size()>50) {
				throw new ExceptionCause("Local cart limit exceeded!",HttpStatus.BAD_REQUEST);
			}
			response= new ResponseEntity<Response>(new Response("Successfully added to cart!",Response.SUCCESS,shopUtil.getInventories(inventories)),HttpStatus.OK);
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
	
	
	
	
}
