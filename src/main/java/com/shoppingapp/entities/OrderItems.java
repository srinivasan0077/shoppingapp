package com.shoppingapp.entities;

import javax.validation.constraints.NotNull;

public class OrderItems {

	@NotNull(message = "Invalid Request!")
	private Inventory inventory;
	
	@NotNull(message = "Invalid Request!")
	private int count;

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
