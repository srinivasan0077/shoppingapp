package com.shoppingapp.entities;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Color {

	private Long colorId;
	
	@NotNull(message = "'name' field can't be null!")
	@Size(min = 3,max =30,message = "'name' length should be between 3 and 30!")
	private String name;
	
	@Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",message = "'cssColor' is not valid")
	private String cssColor;
	
	public Color() {}
	
	public Color(Long colorId, String name) {
		super();
		this.colorId = colorId;
		this.name = name;
	}
	public Color(Long colorId, String name,String cssColor) {
		super();
		this.colorId = colorId;
		this.name = name;
		this.setCssColor(cssColor);
	}
	
    public static Map<String,String> classDbNameMapForSearch;
	
	static {
		classDbNameMapForSearch=new HashMap<>();
		classDbNameMapForSearch.put("colorId","id");
		classDbNameMapForSearch.put("name","name");
		classDbNameMapForSearch.put("cssColor","csscolor");
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
	public String getCssColor() {
		return cssColor;
	}
	public void setCssColor(String cssColor) {
		this.cssColor = cssColor;
	}
	
	
	
}
