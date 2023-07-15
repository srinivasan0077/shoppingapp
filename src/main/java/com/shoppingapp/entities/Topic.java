package com.shoppingapp.entities;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Topic {

	    private Long id;
	    
	    @NotNull(message = "Topic name cannot be null")
	    @Size(min = 3,message = "Topic name's length should be greater than equal to 3")
	    private String name;
	    
	    @NotNull(message = "'active' field cannot be null")
	    private Boolean active;
	    
	    private ArrayList<ProductVariant> variants;
	    
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Boolean getActive() {
			return active;
		}
		public void setActive(Boolean active) {
			this.active = active;
		}
		public ArrayList<ProductVariant> getVariants() {
			return variants;
		}
		public void setVariants(ArrayList<ProductVariant> variants) {
			this.variants = variants;
		}
	   
	   
}
