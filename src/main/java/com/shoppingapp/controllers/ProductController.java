package com.shoppingapp.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

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
import com.shoppingapp.entities.Category;
import com.shoppingapp.entities.Color;
import com.shoppingapp.entities.GetInfo;
import com.shoppingapp.entities.Inventory;
import com.shoppingapp.entities.Product;
import com.shoppingapp.entities.ProductItem;
import com.shoppingapp.entities.ProductVariant;
import com.shoppingapp.entities.Response;
import com.shoppingapp.entities.Size;
import com.shoppingapp.entities.Topic;
import com.shoppingapp.entities.VariantImage;
import com.shoppingapp.productUtils.ProductManagementInterface;
import com.shoppingapp.productUtils.ProductManagementUtil;
import com.shoppingapp.utils.BeanFactoryWrapper;
import com.shoppingapp.utils.BeanValidator;
import com.shoppingapp.utils.EncryptionUtil;
import com.shoppingapp.utils.ExceptionCause;

import javax.validation.Valid;




@CrossOrigin(origins = {"http://localhost:3000"},allowCredentials = "true")
@Controller
public class ProductController {

	private static final Logger logger=LogManager.getLogger(ProductManagementUtil.class);
	
	@RequestMapping(path={"/","/home"},method=RequestMethod.GET)
	public String getHome() {
		return "home";
	}
	
	@RequestMapping(path={"/admin/category"},method=RequestMethod.GET)
	public String getCategory() {
		return "category";
	}
	
	@RequestMapping(path={"/admin/api/colors"},method =RequestMethod.GET)
	@ResponseBody
	public ArrayList<Color> getColors() {
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		ArrayList<Color> colors=util.getColors();
		util.closeConnection();
		return colors;
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
	
	@RequestMapping(path={"/admin/api/sizes"},method =RequestMethod.GET)
	@ResponseBody
	public ArrayList<Size> getSizes(@RequestParam("input") String jsonInput) {
		ArrayList<Size> sizes=null;
		JSONObject jsonObject=new JSONObject(jsonInput);
		if(jsonObject.has("productId")) {
			ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
			try {
			    sizes=util.getSizesByProductId(jsonObject.getLong("productId"));
			}catch (Exception e) {
				logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			}finally {
			   util.closeConnection();
			}
		}
		return sizes;
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
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
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
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
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
			String result=util.createSize(size);
			if(result.equals(ProductManagementInterface.CREATED)) {
				return new ResponseEntity<Response>(new Response("Successfully Created!",Response.SUCCESS,null),HttpStatus.OK);
			}else {
				if(result.equals(ProductManagementInterface.EXIST)) {
					return  new ResponseEntity<Response>(new Response("Size already Exist!",Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
				}
				
			}
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
		}finally {
			util.closeConnection();
		}
		return  new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(path={"/admin/api/sizes"},method =RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Response> putSize(@Valid @RequestBody Size size,BindingResult validationResult) {
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			if(validationResult.hasErrors()) {
				FieldError error=validationResult.getFieldError();
				return  new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
			}
			if(size.getSizeId()==null) {
				return  new ResponseEntity<Response>(new Response("Cannot edit size without id!",Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
			}
			
			String result=util.editSize(size);
			
			if(result.equals(ProductManagementInterface.SUCCESS)) {
				return  new ResponseEntity<Response>(new Response("Successfully Edited!",Response.SUCCESS,null),HttpStatus.OK);
			}else {
				if(result.equals(ProductManagementInterface.NOT_EXIST)) {
					return  new ResponseEntity<Response>(new Response("Size not exist!",Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
				}
				
			}
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
		}finally {
			util.closeConnection();
		}
		return  new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(path={"/admin/api/categories"},method =RequestMethod.GET)
	@ResponseBody
	public ArrayList<Category> getCategories() {
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		ArrayList<Category> categories=util.getCategories();
		util.closeConnection();
		return categories;
	}
	
	@RequestMapping(path={"/admin/api/categories/{id}"},method =RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getCategoryById(@PathVariable Long id) {
		
		ResponseEntity<Response> responseJSON;
		ProductManagementInterface util=(ProductManagementInterface)BeanFactoryWrapper.getBeanFactory().getBean("productutil");
		try {
			Category category=util.getCategoryBy("id",id,Criteria.EQUAL);
			if(category==null) {
				throw new Exception("Invalid Id passed");
			}
			responseJSON=new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,category),HttpStatus.OK);
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			responseJSON=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),HttpStatus.BAD_REQUEST);
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
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getProductVariants(itemId, info)),HttpStatus.OK);
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
		String fileToBeDelted=null;
		boolean exception=false;
		
		try {
			 JSONObject imageInfoJson=new JSONObject(imageInfo);
			 imageInfoJson.put("variantId",id);
			 
			 validateImageUpload(imageInfoJson,file,util);
			 
			 //Write Image
			 byte bytes[]=file.getBytes();
			
		     String tomcatBase = System.getProperty("catalina.base")+"\\wtpwebapps\\shoppingapp\\resources\\img";
		     String hashedFileName=EncryptionUtil.getSHA1(id+imageInfoJson.getString("name")+file.getOriginalFilename())+getExtension(file.getContentType());
			 String filePath=tomcatBase+File.separator+hashedFileName;
			 File newFile=new File(filePath);
			 BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(newFile));
			 stream.write(bytes);
			 stream.flush();
			 stream.close();
			 fileToBeDelted=filePath;
			 
			 System.out.println(filePath);
			 
			 //Persist Image Info
			 VariantImage variantImageInfo=new VariantImage();
			 variantImageInfo.setName(imageInfoJson.getString("name"));
			 variantImageInfo.setOrd(imageInfoJson.getInt("ord"));
			 ProductVariant variant=new ProductVariant();
			 variant.setVariantId(id);
			 variantImageInfo.setVariant(variant);
			 variantImageInfo.setUrl(hashedFileName);
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
				if(fileToBeDelted!=null) {
					util.deleteImages(fileToBeDelted);
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
	
	@RequestMapping(path={"/admin/api/topics/{id}"},method =RequestMethod.POST)
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
	
	private String getExtension(String input) {
		String[] split=input.split("/");
		return "."+split[1];		
	}
	
	private void validateImageUpload(JSONObject imageInfo,MultipartFile image,ProductManagementInterface util) throws Exception {
		 if(imageInfo.getString("name")==null) {
			 throw new ExceptionCause("Image name cannot be null",HttpStatus.BAD_REQUEST);
		 }
		 
		 if(util.getVariantImageByNameAndVariantId(imageInfo.getLong("variantId"), imageInfo.getString("name"))!=null) {
			 throw new ExceptionCause("Image name should be unique under a variant",HttpStatus.BAD_REQUEST);
		 }
		 
		 if(image==null || image.getBytes().length==0) {
			 throw new ExceptionCause("Attached Image not found!",HttpStatus.BAD_REQUEST);
		 }
			 
	}
	
	
}
