package com.shoppingapp.entities;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.shoppingapp.customAnnotations.ForeignKeyField;

public class ProductItem {

	private Long productItemId;
	
	@NotNull(message = "Item name can't be null")
	@Size(min = 3,message = "Item name should have length greater than equal to 3")
	private String productItemName;
	
	@NotNull(message = "Product can't be null")
	@ForeignKeyField(name = "productId")
	private Product product;
	
	@Size(max =255,message = "Item description's length cannot exceed 255!")
	private String description;
	
	private Long createdAt;
	private Long modifiedAt;
	private Boolean isActive=false;
	
	private ArrayList<ProductVariant> variants=new ArrayList<ProductVariant>();
	
	public ProductItem(String productItemName,String description) {
		super();
		this.productItemName = productItemName;
		this.description = description;
	}
	
	public ProductItem() {}
	public Long getProductItemId() {
		return productItemId;
	}
	public void setProductItemId(Long productItemId) {
		this.productItemId = productItemId;
	}
	public String getProductItemName() {
		return productItemName;
	}
	public void setProductItemName(String productItemName) {
		this.productItemName = productItemName;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
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

	public ArrayList<ProductVariant> getVariants() {
		return variants;
	}
	public void setVariants(ArrayList<ProductVariant> variants) {
		this.variants = variants;
	}
	@Override
	public String toString() {
		return "ProductItem [productItemId=" + productItemId + ", productItemName=" + productItemName + ", product="
				+ product + ", description=" + description + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt
				+ ", variants=" + variants + "]";
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	
	
	
}
