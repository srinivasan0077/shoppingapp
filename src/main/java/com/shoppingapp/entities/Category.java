package com.shoppingapp.entities;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Category {

	private Long productTypeId;
	
	@NotNull(message = "Category name can't be null!")
	@Size(min = 3,max =30,message = "Category Name's length should be between 3 and 30!")
	private String productTypeName;
	
	@Size(max =255,message = "Category description's length cannot exceed 255!")
	private String description;

	private Long createdAt;

	private Long modifiedAt;

	private ArrayList<Product> products;
	
	public Category(String productTypeName, String description) {
		super();
		this.productTypeName = productTypeName;
		this.description = description;
	}
	
	public Category() {};
	public Long getProductTypeId() {
		return productTypeId;
	}
	public void setProductTypeId(Long productTypeId) {
		this.productTypeId = productTypeId;
	}
	public String getProductTypeName() {
		return productTypeName;
	}
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
	public Long getModifiedAt() {
		return modifiedAt;
	}
	public void setModifiedAt(Long modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	public ArrayList<Product> getProducts() {
		return products;
	}
	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "Category [productTypeId=" + productTypeId + ", productTypeName=" + productTypeName + ", description="
				+ description + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + "]";
	}
	
	
	
}
