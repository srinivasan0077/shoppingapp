package com.shoppingapp.shopUtils;

import java.util.ArrayList;
import java.util.List;

import com.shoppingapp.entities.BannerImage;
import com.shoppingapp.entities.Cart;
import com.shoppingapp.entities.GetInfo;
import com.shoppingapp.entities.Inventory;
import com.shoppingapp.entities.Product;
import com.shoppingapp.entities.ProductVariant;
import com.shoppingapp.entities.Topic;
import com.shoppingapp.entities.User;

public interface ShoppingUtilInterface {
 
	public ArrayList<Topic> getTopicsToDisplay() throws Exception;
	public void addItemToCart(Cart cart) throws Exception;
	public int getCartCount() throws Exception;
	public List<Cart> getCartOfUser() throws Exception;
	public List<Inventory> getInventories(List<Inventory> inventories) throws Exception;
	public ProductVariant getVariantToView(Long variantId) throws Exception;
	public List<ProductVariant> getVariantsByItemId(Long itemId) throws Exception;
	public List<BannerImage> getBanners()throws Exception;
	public List<Product> getHeaders()throws Exception;
	public Product getHeaderById(Long id)throws Exception;
	public ArrayList<ProductVariant> getProductVariants(GetInfo info,Long productId)throws Exception;
	public void closeConnection();
}
