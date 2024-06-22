package com.shoppingapp.entities;

import java.util.Date;
import java.util.List;

public class OrderEntity {

	private Long orderId;
	private User orderedBy;
	private Address shipTo;
	private Status statusCode;
	private String clientSecret;
	private Date date;
	private int totalPrice;
	private List<Inventory> inventories;
	
	public enum Status{
		WAITING,OPEN,COD,FAILED,CLOSED
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public User getOrderedBy() {
		return orderedBy;
	}

	public void setOrderedBy(User orderedBy) {
		this.orderedBy = orderedBy;
	}

	public Address getShipTo() {
		return shipTo;
	}

	public void setShipTo(Address shipTo) {
		this.shipTo = shipTo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Inventory> getInventories() {
		return inventories;
	}

	public void setInventories(List<Inventory> inventories) {
		this.inventories = inventories;
	}

	public Status getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Status statusCode) {
		this.statusCode = statusCode;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}		
	
	
}
