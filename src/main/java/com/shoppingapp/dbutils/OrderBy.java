package com.shoppingapp.dbutils;

public class OrderBy {

	public static enum Order{
		ASC,
		DESC
	};
	private Column col;
	private Order ord; 
	
	public OrderBy(Column col,Order ord) {
		this.setCol(col);
		this.setOrd(ord);
	}

	public Column getCol() {
		return col;
	}

	public void setCol(Column col) {
		this.col = col;
	}

	public Order getOrd() {
		return ord;
	}

	public void setOrd(Order ord) {
		this.ord = ord;
	}
	
	public String constructOrderBy() {
		return "Order By "+col.getTableName()+"."+col.getColumnName()+" "+ord.toString();
	}
	 
}
