package com.shoppingapp.shopUtils;

import java.util.ArrayList;
import java.util.List;

import com.shoppingapp.entities.Address;
import com.shoppingapp.entities.BannerImage;
import com.shoppingapp.entities.Cart;
import com.shoppingapp.entities.GetInfo;
import com.shoppingapp.entities.Inventory;
import com.shoppingapp.entities.OrderEntity;
import com.shoppingapp.entities.Product;
import com.shoppingapp.entities.ProductVariant;
import com.shoppingapp.entities.Topic;
import com.shoppingapp.entities.OrderEntity.Status;


public interface ShoppingUtilInterface {
 
	public ArrayList<Topic> getTopicsToDisplay(GetInfo info) throws Exception;
	public void addItemToCart(Cart cart) throws Exception;
	public void removeItemFromCart(Cart cart) throws Exception;
	public int getCartCount() throws Exception;
	public List<Cart> getCartOfUser() throws Exception;
	public List<Inventory> getInventories(List<Inventory> inventories) throws Exception;
	public ProductVariant getVariantToView(Long variantId) throws Exception;
	public List<ProductVariant> getVariantsByItemId(Long itemId) throws Exception;
	public List<BannerImage> getBanners()throws Exception;
	public List<Product> getHeaders()throws Exception;
	public Product getHeaderById(Long id)throws Exception;
	public ArrayList<ProductVariant> getProductVariants(GetInfo info,Long productId)throws Exception;
	public OrderEntity placeOrder(Address address,boolean COD) throws Exception;
	public List<Address> getAddressesOfUser() throws Exception;
	public void confirmOrder(Long orderId)throws Exception;
	public OrderEntity getOrderByIdAndUserId(Long id,Long userId)throws Exception;
	public OrderEntity getOrderByIdAndStatus(Long id,Status status)throws Exception;
	public List<OrderEntity> getOrdersByUserId(GetInfo info)throws Exception;
	public OrderEntity getOrderById(Long id)throws Exception;
	public void closeConnection();
}
