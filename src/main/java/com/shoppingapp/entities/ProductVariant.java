package com.shoppingapp.entities;

import java.util.ArrayList;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.shoppingapp.customAnnotations.ForeignKeyField;

public class ProductVariant {

	private Long variantId;
	
	@NotNull(message = "Variant name can't be null")
	@javax.validation.constraints.Size(min = 3,message = "Variant name should have length greater than equal to 3")
	private String name;
	
	@NotNull(message = "Item can't be null")
	@ForeignKeyField(name = "productItemId")
	private ProductItem item;
	
	private Size size;
	
	@NotNull(message = "Color can't be null")
	@ForeignKeyField(name = "colorId")
	private Color color;
	
	@Min(value = 1,message = "Price should have minimum value of 1")
	private int price;
	
	private String imageUrl;
	private ArrayList<String> images;
	
	public Long getVariantId() {
		return variantId;
	}
	public void setVariantId(Long variantId) {
		this.variantId = variantId;
	}
	public ProductItem getItem() {
		return item;
	}
	public void setItem(ProductItem item) {
		this.item = item;
	}
	public Size getSize() {
		return size;
	}
	public void setSize(Size size) {
		this.size = size;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	@Override
	public String toString() {
		return "ProductVariant [variantId=" + variantId + ", item=" + item + ", size=" + size + ", color=" + color
				+ ", price=" + price + ", imageUrl=" + imageUrl + "]";
	}
	public ArrayList<String> getImages() {
		return images;
	}
	public void setImages(ArrayList<String> images) {
		this.images = images;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
