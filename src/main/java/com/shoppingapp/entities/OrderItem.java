package com.shoppingapp.entities;

public class OrderItem {

	private Long orderItemId;
	private Long orderId;
	private ProductItem productItem;
	private int quantity;
	private int itemStatusCode;
	
	public Long getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public ProductItem getProductItem() {
		return productItem;
	}
	public void setProductItem(ProductItem productItem) {
		this.productItem = productItem;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getItemStatusCode() {
		return itemStatusCode;
	}
	public void setItemStatusCode(int itemStatusCode) {
		this.itemStatusCode = itemStatusCode;
	}
	
	
}
