package com.shoppingapp.entities;

public class Color {

	private Long colorId;
	private String name;
	
	public Color(Long colorId, String name) {
		super();
		this.colorId = colorId;
		this.name = name;
	}
	public Long getColorId() {
		return colorId;
	}
	public void setColorId(Long colorId) {
		this.colorId = colorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Color [colorId=" + colorId + ", name=" + name + "]";
	}
	
	
	
}
