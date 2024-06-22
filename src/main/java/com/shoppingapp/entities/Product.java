package com.shoppingapp.entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.shoppingapp.customAnnotations.ForeignKeyField;

public class Product {

	private Long productId;
	
	@NotNull(message = "Product name can't be null")
	@Size(min = 3,message = "Product name should have length greater than equal to 3")
	private String productName;
	
	@NotNull(message = "Category can't be null")
	@ForeignKeyField(name = "productTypeId")
	private Category productType;
	
	@Size(max =255,message = "Product description's length cannot exceed 255!")
	private String description;
	
	private boolean isHeader=false;
	
	private Long createdAt;
	private Long modifiedAt;
	
	public Product() {}
	
	public Product(String productName, String description) {
		super();
		this.productName = productName;
		this.description = description;
	}

	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Category getProductType() {
		return productType;
	}
	public void setProductType(Category productType) {
		this.productType = productType;
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

	public boolean getIsHeader() {
		return isHeader;
	}

	public void setIsHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}
	
	@Override
	public String toString() {
		return "Product [productId=" + productId + ", productName=" + productName + ", productType=" + productType
				+ ", description=" + description + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + "]";
	}

	
}
