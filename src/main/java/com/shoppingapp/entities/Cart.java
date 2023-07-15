package com.shoppingapp.entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import com.shoppingapp.customAnnotations.ForeignKeyField;

public class Cart {
      
	  private Long cartId;

	  @NotNull(message = "inventory field can't be null")
	  @ForeignKeyField(name = "inventoryId")
	  private Inventory inventory;
	  
	  private int count;
	  
	  public Long getCartId() {
			return cartId;
	  }
	  
	  public void setCartId(Long cartId) {
		this.cartId = cartId;
	  }
	  
	  public Inventory getInventory() {
			return inventory;
	  }
		
	  public void setInventory(Inventory inventory) {
			this.inventory = inventory;
	  }
		
	  public int getCount() {
			return count;
	  }
		
	  public void setCount(int count) {
			this.count = count;
	  }
	  
	  
}
