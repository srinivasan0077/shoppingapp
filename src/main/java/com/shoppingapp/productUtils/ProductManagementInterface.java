package com.shoppingapp.productUtils;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import com.shoppingapp.dbutils.Criteria;
import com.shoppingapp.entities.Category;
import com.shoppingapp.entities.Color;
import com.shoppingapp.entities.GetInfo;
import com.shoppingapp.entities.Inventory;
import com.shoppingapp.entities.Product;
import com.shoppingapp.entities.ProductItem;
import com.shoppingapp.entities.ProductVariant;
import com.shoppingapp.entities.Size;
import com.shoppingapp.entities.Topic;
import com.shoppingapp.entities.VariantImage;
import com.shoppingapp.utils.ExceptionCause;

public interface ProductManagementInterface {

	public String CREATED="CREATED";
	public String FAILED="FAILED";
	public String SUCCESS="SUCCESS";
	public String EXIST="EXIST";
	public String NOT_EXIST="NOT_EXIST";
	public String NOT_VERIFIED="NOT_VERIFIED";
	
	//Categories
	public String createCategories(ArrayList<Category> categories);
	public String createCategory(Category category);
	public Category getCategoryBy(String name,Object value,int comparator);
	public String editCategory(Category cat);
	public ArrayList<Category> getCategories();
	public ArrayList<Category> getCategoriesAndProducts();
	
	//Products
	public void createProducts(ArrayList<Product> products) throws ExceptionCause,Exception;
	public void createProduct(Product products) throws ExceptionCause,Exception;
	public void editProduct(Product product) throws ExceptionCause,Exception;
	public Product getProductBy(String name,Object value);
	public ArrayList<Product> getProducts() throws Exception;
	
	//Product Items
	public void createProductItems(ArrayList<ProductItem> productItems) throws Exception;
	public void createProductItem(ProductItem prodItem) throws Exception;
	public ProductItem getProductItemBy(String name,Object value);
	public ArrayList<ProductItem> getProductItems(GetInfo info) throws Exception;
	public void putProductItem(ProductItem prodItem) throws Exception;
	
	//variants
	public ArrayList<ProductVariant> getProductVariants(Long itemId,GetInfo info);
	public ProductVariant getProductVariantById(Long variantId);
	public void createProductVariants(ArrayList<ProductVariant> productVars) throws Exception;
	public ProductItem getProductItemAndVariantsBy(String name,Object value);
	public void createProductVariant(ProductVariant variant) throws Exception;
	public void putProductVariant(ProductVariant variant) throws Exception;
	
	//colors
	public ArrayList<Color> getColors();
	public ArrayList<Color> searchColorByName(String name);
	public Color getColorById(Long colorId);
	
	//sizes
	public ArrayList<Size> getSizesByProductId(Long productId) throws Exception;
	public Size getSizeBy(String param,Object value);
	public String editSize(Size size);
	public String createSize(Size size);
	
	//images
	public ArrayList<VariantImage> getVariantImagesByVariantId(Long variantId) throws Exception;
	public VariantImage getVariantImageById(Long imageId) throws Exception;
	public VariantImage getVariantImageByNameAndVariantId(Long variantId,String name) throws Exception;
	public void createImageForVariants(VariantImage imageInfo) throws Exception;
	public void editImage(VariantImage image) throws Exception;
	public void deletImageById(Long imageId) throws Exception;
	public void deleteImages(String imagePath);
	
	//inventories
	public ArrayList<Inventory> getInventoriesByVariantId(Long variantId) throws Exception;
	public Inventory getInventoryById(Long id)throws Exception;
	public void createInventories(Inventory inventory) throws Exception;
	public void editInventories(Inventory inventory) throws Exception;
	public void deleteInventories(Long inventoryId) throws Exception;
	
	//topics
	public void createTopic(Topic topic) throws Exception;
	public void editTopic(Topic topic) throws Exception;
	public ArrayList<Topic> getTopics() throws Exception;
	public Topic getTopicByUniqueCriteria(Criteria criteria) throws Exception;
	public boolean isTopicItemRelationExist(Long topicId,Long itemId) throws Exception;
	public void addProductItemToTopic(Long topicId,Long itemId) throws Exception;
	public void deleteTopicById(Long topicId) throws Exception;
	public void removeItemTopicRelation(Long topicId,Long itemId) throws Exception;
	
	
	//db connection related
	public void closeConnection();
		
	
}
