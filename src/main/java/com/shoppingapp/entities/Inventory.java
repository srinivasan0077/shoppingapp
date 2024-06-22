package com.shoppingapp.entities;

import javax.validation.constraints.NotNull;

import com.shoppingapp.customAnnotations.ForeignKeyField;

public class Inventory {

	private Long inventoryId;
	
	@NotNull(message = "Size can't be null")
	@ForeignKeyField(name = "sizeId")
	private Size size;
	
	@NotNull(message = "Product Variant can't be null")
	@ForeignKeyField(name = "variantId")
	private ProductVariant variant;
	
	private int orderedCount;
	
	
	private int availableStocks;
	
	public Long getInventoryId() {
		return inventoryId;
	}
	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}
	public Size getSize() {
		return size;
	}
	public void setSize(Size size) {
		this.size = size;
	}
	public ProductVariant getVariant() {
		return variant;
	}
	public void setVariant(ProductVariant variant) {
		this.variant = variant;
	}
	public int getAvailableStocks() {
		return availableStocks;
	}
	public void setAvailableStocks(int availableStocks) {
		this.availableStocks = availableStocks;
	}
	public int getOrderedCount() {
		return orderedCount;
	}
	public void setOrderedCount(int orderedCount) {
		this.orderedCount = orderedCount;
	}
	
	
}
