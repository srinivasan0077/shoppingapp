package com.shoppingapp.entities;

import javax.validation.constraints.NotNull;

public class Size {

	private Long sizeId;
	
	@NotNull(message = "name of size can't be null")
	@javax.validation.constraints.Size(min = 1,message = "name of size must be greater than and equal to 1")
	private String name;
	
	@NotNull(message="order of size can't be null")
	private Integer order;
	
	private String description;
	
	@NotNull(message = "product can't be null")
	private Product product;
	
	public Size(Long sizeId, String name,int order,String description) {	
		super();
		this.sizeId = sizeId;
		this.name = name;
		this.order=order;
		this.description=description;
	}
	
	public Size() {}
	
	public Long getSizeId() {
		return sizeId;
	}
	
	public void setSizeId(Long sizeId) {
		this.sizeId = sizeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public String getDescription() {
		if(description!=null && description.trim().equals("")) {
			description=null;
		}
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	@Override
	public String toString() {
		return "Size [sizeId=" + sizeId + ", name=" + name + ", order=" + order + ", description=" + description + "]";
	}
	
}
