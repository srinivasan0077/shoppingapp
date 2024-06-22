package com.shoppingapp.controllers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.shoppingapp.dbutils.Criteria;
import com.shoppingapp.entities.BannerImage;
import com.shoppingapp.entities.Category;
import com.shoppingapp.entities.Color;
import com.shoppingapp.entities.GetInfo;
import com.shoppingapp.entities.Inventory;
import com.shoppingapp.entities.Product;
import com.shoppingapp.entities.ProductItem;
import com.shoppingapp.entities.ProductVariant;
import com.shoppingapp.entities.Relation;
import com.shoppingapp.entities.Response;
import com.shoppingapp.entities.Size;
import com.shoppingapp.entities.Topic;
import com.shoppingapp.entities.VariantImage;
import com.shoppingapp.productUtils.ProductManagementInterface;
import com.shoppingapp.utils.AWSS3StorageService;
import com.shoppingapp.utils.BeanFactoryWrapper;
import com.shoppingapp.utils.BeanValidator;
import com.shoppingapp.utils.EncryptionUtil;
import com.shoppingapp.utils.ExceptionCause;

import javax.validation.Valid;




@CrossOrigin(origins = {"http://localhost:3000","https://www.royall.in","https://royall.in"},allowCredentials = "true")
@Controller
public class ProductController {

	private static final Logger logger=LogManager.getLogger(ProductController.class);
	
	
	@RequestMapping(path={"/admin/category"},method=RequestMethod.GET)
	public String getCategory() {
		return "category";
	}
	
	@RequestMapping(path={"/admin/api/colors/list"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> getColors(@Valid @RequestBody GetInfo info,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
        ResponseEntity<Response> responseJSON;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getColors(info)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/admin/api/colors/{id}"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getColorById(@PathVariable Long id) {

        ResponseEntity<Response> responseJSON;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getColorById(id)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/admin/api/colors"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> createColor(@Valid @RequestBody Color color,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
        ResponseEntity<Response> responseJSON;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			util.createColor(color);
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,null),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/admin/api/colors/{id}"},method =RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Response> editColor(@PathVariable Long id,@Valid @RequestBody Color color,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
        ResponseEntity<Response> responseJSON;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			color.setColorId(id);
			util.editColor(color);
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,null),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/admin/api/colors/search"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> searchColorByName(@RequestParam String name) {
        ResponseEntity<Response> responseJSON;
		
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.searchColorByName(name)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	}
	
	
	@RequestMapping(path={"/admin/api/products/{id}/sizes"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getProductSizes(@PathVariable Long id) {
        ResponseEntity<Response> responseJSON;
		
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getSizesByProductId(id)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/admin/api/sizes/{id}"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getSizeById(@PathVariable Long id) {
		ResponseEntity<Response> responseJSON;
		
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			Size size=util.getSizeBy("id", id);
			if(size==null) {
				throw new Exception("Invalid Id passed");
			}
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,size),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/admin/api/sizes"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> createSize(@Valid @RequestBody Size size,BindingResult validationResult) {
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			if(validationResult.hasErrors()) {
				FieldError error=validationResult.getFieldError();
				return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
			}
			util.createSize(size);
			return new ResponseEntity<Response>(new Response("Successfully Created!",Response.SUCCESS,null),HttpStatus.OK);
			
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			return new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			return  new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
	
	}
	
	@RequestMapping(path={"/admin/api/sizes"},method =RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Response> putSize(@Valid @RequestBody Size size,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}

		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			if(size.getSizeId()==null) {
				throw new ExceptionCause("Cannot edit size without id!", HttpStatus.BAD_REQUEST);
			}
			util.editSize(size);
			return  new ResponseEntity<Response>(new Response("Successfully Edited!",Response.SUCCESS,null),HttpStatus.OK);
			
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			return new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			return  new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
	}
	
	@RequestMapping(path={"/admin/api/categories"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getCategories() {
		ResponseEntity<Response> responseJSON;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getCategories()),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/admin/api/categories/{id}"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getCategoryById(@PathVariable Long id) {
		
		ResponseEntity<Response> responseJSON;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			Category category=util.getCategoryBy("id",id,Criteria.EQUAL);
			if(category==null) {
				throw new ExceptionCause("Invalid Id passed",HttpStatus.BAD_REQUEST);
			}
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,category),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	
	}
	
	@RequestMapping(path={"/admin/api/categories"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> postCategories(@Valid @RequestBody Category category,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}
		
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		String result=util.createCategory(category);
		util.closeConnection();
		if(result.equals(ProductManagementInterface.CREATED)){
			return  new ResponseEntity<Response>(new Response("Successfully Created!",Response.SUCCESS,null),HttpStatus.OK);
		}
		else {
			if(result.equals(ProductManagementInterface.EXIST)) {
				return  new ResponseEntity<Response>(new Response("Category already exist!",Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
			}
			return  new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(path={"/admin/api/categories"},method =RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Response> putCategories(@Valid @RequestBody Category category,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}
		
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		String result=util.editCategory(category);
		util.closeConnection();
		if(result.equals(ProductManagementInterface.SUCCESS)){
			return  new ResponseEntity<Response>(new Response("Successfully Edited!",Response.SUCCESS,null),HttpStatus.OK);
		}
		else {
			if(result.equals(ProductManagementInterface.NOT_EXIST)) {
				return  new ResponseEntity<Response>(new Response("Category not exist!",Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
			}
			return  new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(path={"/admin/product"},method=RequestMethod.GET)
	public String getProduct() {
		return "product";
	}
	
	@RequestMapping(path={"/admin/api/products"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getProducts() {
		ResponseEntity<Response> responseJSON;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, util.getProducts()),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/admin/api/products/{id}"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getProductById(@PathVariable Long id) {
		ResponseEntity<Response> responseJSON;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			Product product=util.getProductBy("id",id);
			if(product==null) {
				throw new Exception("Invalid Id passed");
			}
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, product),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	}
	
	@RequestMapping(path={"/admin/api/products"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> postProducts(@Valid @RequestBody Product product,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			BeanValidator.checkForeignKeyField(product);
			util.createProduct(product);
			response= new ResponseEntity<Response>(new Response("Successfully Created!",Response.SUCCESS,null),HttpStatus.OK);
			
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch(Exception e){
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		return response;
	}
	
	@RequestMapping(path={"/admin/api/products"},method =RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Response> editProducts(@Valid @RequestBody Product product,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
           BeanValidator.checkForeignKeyField(product);
		   util.editProduct(product);
		   response=new ResponseEntity<Response>(new Response("Successfully Edited!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch(Exception e){
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		return response;
		
	}
	
	
	
	@RequestMapping(path={"/admin/productItem"},method=RequestMethod.GET)
	public String getProductItem() {
		return "productItem";
	}
	
	
	
	@RequestMapping(path={"/admin/api/items/list"},method=RequestMethod.POST,consumes = {"application/json;charset=utf-8"})
	@ResponseBody
	public ResponseEntity<Response> getProductItemList(@Valid @RequestBody GetInfo info,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getProductItems(info)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/items/{id}"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getProductItemById(@PathVariable Long id) {
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			response= new ResponseEntity<Response>(new Response("Get Item By ID Successful!",Response.SUCCESS,util.getProductItemBy("id", id)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/items/{id}/toggle"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> toggleItem(@PathVariable Long id) {
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			util.enableOrDisableProductItem(id);
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/items"},method=RequestMethod.POST,consumes = {"application/json;charset=utf-8"})
	@ResponseBody
	public ResponseEntity<Response> createProductItem(@Valid @RequestBody ProductItem item,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			 BeanValidator.checkForeignKeyField(item);
			 util.createProductItem(item);
			 response=new ResponseEntity<Response>(new Response("Successfully Created!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	
	@RequestMapping(path={"/admin/api/items"},method=RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Response> putProductItem(@Valid @RequestBody ProductItem item,BindingResult validationResult) {
		 
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			 BeanValidator.checkForeignKeyField(item);
			 util.putProductItem(item);
			 response=new ResponseEntity<Response>(new Response("Successfully Edited!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/items/{itemId}/variants/list"},method=RequestMethod.POST,consumes = {"application/json;charset=utf-8"})
	@ResponseBody
	public ResponseEntity<Response> getVariantList(@Valid @RequestBody GetInfo info,@PathVariable Long itemId,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			info.setFilterBy("item");
			info.setFilterValue(itemId);
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getProductVariants(info)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/variants/list"},method=RequestMethod.POST,consumes = {"application/json;charset=utf-8"})
	@ResponseBody
	public ResponseEntity<Response> searchVariants(@Valid @RequestBody GetInfo info,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getProductVariants(info)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/variants"},method=RequestMethod.POST,consumes = {"application/json;charset=utf-8"})
	@ResponseBody
	public ResponseEntity<Response> createVariant(@Valid @RequestBody ProductVariant variant,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			BeanValidator.checkForeignKeyField(variant);
            util.createProductVariant(variant);
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/variants/{id}"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getVariantById(@PathVariable Long id) {
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			response= new ResponseEntity<Response>(new Response("Get Item By ID Successful!",Response.SUCCESS,util.getProductVariantById(id)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/variants"},method=RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Response> putProductVariant(@Valid @RequestBody ProductVariant variant,BindingResult validationResult) {
		 
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			 BeanValidator.checkForeignKeyField(variant);
			 util.putProductVariant(variant);
			 response=new ResponseEntity<Response>(new Response("Successfully Edited!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/variants/{id}/toggle"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> toggleVariant(@PathVariable Long id) {
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			util.enableOrDisableProductVariant(id);
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/variants/{id}/images"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getVariantImages(@PathVariable Long id) {
		 
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, util.getVariantImagesByVariantId(id)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/variants/{id}/images"},method=RequestMethod.POST,consumes = {"multipart/form-data"})
	@ResponseBody
	public ResponseEntity<Response> addVariantImages(@PathVariable Long id,@RequestParam("imageInfo") String imageInfo
			,@RequestParam("image") MultipartFile file) {
		 
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		String fileToBeDeleted=null;
		boolean exception=false;
		
		try {
			 JSONObject imageInfoJson=new JSONObject(imageInfo);
			 imageInfoJson.put("variantId",id);
			 
			 validateImageUpload(imageInfoJson,file,util);
			 
			 //Write Image
			 //byte bytes[]=file.getBytes();
			
		     //String tomcatBase = System.getProperty("catalina.base")+"\\wtpwebapps\\shoppingapp\\resources\\img";
		     String hashedFileName=EncryptionUtil.getSHA1(id+":"+imageInfoJson.getString("name")+file.getOriginalFilename())+getExtension(file.getContentType());
			 //String filePath=tomcatBase+File.separator+hashedFileName;
			 //File newFile=new File(filePath);
			 //BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile));
			 //stream.write(bytes);
			 //stream.flush();
			 //stream.close();
		     String filePath=AWSS3StorageService.uploadFileAndGetURL(file, hashedFileName);
			 fileToBeDeleted=hashedFileName;
			 
			 System.out.println(hashedFileName);
			 
			 //Persist Image Info
			 VariantImage variantImageInfo=new VariantImage();
			 variantImageInfo.setName(imageInfoJson.getString("name"));
			 variantImageInfo.setOrd(imageInfoJson.getInt("ord"));
			 ProductVariant variant=new ProductVariant();
			 variant.setVariantId(id);
			 variantImageInfo.setVariant(variant);
			 variantImageInfo.setUrl(filePath);
			 variantImageInfo.setImageKey(hashedFileName);
			 util.createImageForVariants(variantImageInfo);
				
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
			exception=true;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
			exception=true;
		}finally {
			
			if(exception) {
				if(fileToBeDeleted!=null) {
					AWSS3StorageService.deleteFile(fileToBeDeleted);
				}
			}
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/images/{id}"},method=RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<Response> deleteVariantImages(@PathVariable Long id) {
		 
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			 util.deletImageById(id);
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/images/{id}"},method=RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Response> editVariantImages(@PathVariable Long id,@Valid @RequestBody VariantImage imageInfo,BindingResult validationResult) {
		 
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			 imageInfo.setImageId(id);
			 util.editImage(imageInfo);
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/images/{id}"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getVariantImageById(@PathVariable Long id) {
		 
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, util.getVariantImageById(id)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/inventories/{id}"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getVariantInventoryById(@PathVariable Long id) {
		 
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, util.getInventoryById(id)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/variants/{variantId}/inventories"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getVariantInventories(@PathVariable Long variantId) {
		 
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, util.getInventoriesByVariantId(variantId)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/inventories"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> createVariantInventories(@Valid @RequestBody Inventory inventory,BindingResult validationResult) {
		 
		
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			 BeanValidator.checkForeignKeyField(inventory);
			 util.createInventories(inventory);
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/inventories"},method=RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Response> editVariantInventories(@Valid @RequestBody Inventory inventory,BindingResult validationResult) {
		 
		
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			 BeanValidator.checkForeignKeyField(inventory);
			 util.editInventories(inventory);
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/inventories/{id}"},method=RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<Response> deleteVariantInventories(@PathVariable Long id) {
		 
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			 util.deleteInventories(id);
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	
	@RequestMapping(path={"/admin/listItems"},method=RequestMethod.GET)
	public String getProductItemListPage() {
		return "listItems";
	}
	
	@RequestMapping(path={"/admin/api/topics"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getTopics() {
		
		ResponseEntity<Response> responseJSON;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getTopics()),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
		}finally {
			util.closeConnection();
		}
		return responseJSON;
	
	}
	
	@RequestMapping(path={"/admin/api/topics"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> createTopic(@Valid @RequestBody Topic topic,BindingResult validationResult) {
		
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			 util.createTopic(topic);
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	
	}
	
	@RequestMapping(path={"/admin/api/topics/{id}"},method =RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Response> editTopic(@PathVariable Long id,@Valid @RequestBody Topic topic,BindingResult validationResult) {
		
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			 topic.setId(id);
			 util.editTopic(topic);
			 response=new ResponseEntity<Response>(new Response("Edit Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	
	}
	
	@RequestMapping(path={"/admin/api/topics/{id}/relations"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getTopicItemRelationsByTopicId(@PathVariable Long id) {
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			 response=new ResponseEntity<Response>(new Response("Edit Operation Successful!",Response.SUCCESS,util.getProductVariantsByTopicId(id)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	
	}
	
	@RequestMapping(path={"/admin/api/topics/{id}"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getTopicById(@PathVariable Long id) {
		
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			 response=new ResponseEntity<Response>(new Response("Edit Operation Successful!",Response.SUCCESS, util.getTopicById(id)),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	
	}
	
	@RequestMapping(path={"/admin/api/topic_variant_relation"},method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> addVariantToTopic(@Valid @RequestBody Relation relation,BindingResult validationResult) {
		
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			 util.addProductVariantToTopic(relation);
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	
	}
	
	@RequestMapping(path={"/admin/api/topic_variant_relation"},method =RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<Response> removeVariantFromTopic(@Valid @RequestBody Relation relation,BindingResult validationResult) {
		
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			 util.removeTopicVariantRelation(relation);
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	
	}
	
	//banners
	@RequestMapping(path={"/admin/api/banners"},method=RequestMethod.POST,consumes = {"multipart/form-data"})
	@ResponseBody
	public ResponseEntity<Response> addBannerImages(@RequestParam("imageInfo") String imageInfo
			,@RequestParam("image") MultipartFile file) {
		 
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		String fileToBeDeleted=null;
		boolean exception=false;
		try {
			 JSONObject imageInfoJson=new JSONObject(imageInfo);
			 
			 validateBannerImageUpload(imageInfoJson,file,util);
			 
			 //Write Image
			 //byte bytes[]=file.getBytes();
			
		     //String tomcatBase = System.getProperty("catalina.base")+"\\wtpwebapps\\shoppingapp\\resources\\img";
		     String hashedFileName=EncryptionUtil.getSHA1("banner"+":"+imageInfoJson.getString("name")+file.getOriginalFilename())+getExtension(file.getContentType());
			 //String filePath=tomcatBase+File.separator+hashedFileName;
			 //File newFile=new File(filePath);
			 //BufferedOutputStream stream = new BufferedOutputStream(
			//		new FileOutputStream(newFile));
			 //stream.write(bytes);
			 //stream.flush();
			 //stream.close();
			 //fileToBeDeleted=filePath;
			 
			 String imageUrl=AWSS3StorageService.uploadFileAndGetURL(file, hashedFileName);
		     fileToBeDeleted=hashedFileName;
			 //Persist Image Info
			 BannerImage bannerImage=new BannerImage();
			 bannerImage.setName(imageInfoJson.getString("name"));
			 bannerImage.setOrd(imageInfoJson.getInt("ord"));
			 bannerImage.setUrl(imageUrl);
             bannerImage.setImageKey(hashedFileName);
             
			 util.createBanner(bannerImage);
				
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
			exception=true;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
			exception=true;
		}finally {
			
			if(exception) {
				if(fileToBeDeleted!=null) {
					AWSS3StorageService.deleteFile(fileToBeDeleted);
				}
			}
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/banners"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getBannerImages() {
		 
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, util.getBannerImages()),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/banners/{id}"},method=RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<Response> deleteBannerImages(@PathVariable Long id) {
		 
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			 util.deleteBannerImageById(id);
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/banners/{id}"},method=RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Response> editBannerImage(@PathVariable Long id,@Valid @RequestBody BannerImage image,BindingResult validationResult) {
		 
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		
		try {
			 image.setImageId(id);
			 util.editBannerImage(image);
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());;
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/banners/{id}"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getBannerImageById(@PathVariable Long id) {
		 
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			 response=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS, util.getBannerImageById(id)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/orders/list"},method=RequestMethod.POST,consumes = {"application/json;charset=utf-8"})
	@ResponseBody
	public ResponseEntity<Response> getOrders(@Valid @RequestBody GetInfo info,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getOrders(info)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/orders/{id}"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getOrderById(@PathVariable Long id) {
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getOrderById(id)),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"/admin/api/orders/{id}/_close"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> closeOrder(@PathVariable Long id) {
		
		ResponseEntity<Response> response;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			util.closeOrder(id);
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,null),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	
	private String getExtension(String input) {
		String[] split=input.split("/");
		return "."+split[1];		
	}
	
	private void validateImageUpload(JSONObject imageInfo,MultipartFile image,ProductManagementInterface util) throws Exception {
		 if(imageInfo.getString("name")==null) {
			 throw new ExceptionCause("Image name cannot be null",HttpStatus.BAD_REQUEST);
		 }
		 
		 if(imageInfo.getString("name").strip().length()==0) {
			 throw new ExceptionCause("Image name is not valid",HttpStatus.BAD_REQUEST);
		 }
		 
		 if(util.getVariantImageByNameAndVariantId(imageInfo.getLong("variantId"), imageInfo.getString("name"))!=null) {
			 throw new ExceptionCause("Image name should be unique under a variant",HttpStatus.BAD_REQUEST);
		 }
		 
		 if(image==null || image.getBytes().length==0) {
			 throw new ExceptionCause("Attached Image not found!",HttpStatus.BAD_REQUEST);
		 }
			 
	}
	
	private void validateBannerImageUpload(JSONObject imageInfo,MultipartFile image,ProductManagementInterface util) throws Exception {
		 if(imageInfo.getString("name")==null) {
			 throw new ExceptionCause("Image name cannot be null",HttpStatus.BAD_REQUEST);
		 }
		 
		 if(imageInfo.getString("name").strip().length()==0) {
			 throw new ExceptionCause("Image name is not valid",HttpStatus.BAD_REQUEST);
		 }
		 
		 if(util.getBannerImageByName(imageInfo.getString("name"))!=null) {
			 throw new ExceptionCause("Image name should be unique",HttpStatus.BAD_REQUEST);
		 }
		 
		 if(image==null || image.getBytes().length==0) {
			 throw new ExceptionCause("Attached Image not found!",HttpStatus.BAD_REQUEST);
		 }
			 
	}
	
	
}
