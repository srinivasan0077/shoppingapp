package com.shoppingapp.entities;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;



public class GetInfo {

	@Min(value = 1)
	@Max(value = 100)
	private Integer range;
	private Long paginationKey;
	
	@Size(min = 1,message = "Filter By should atleast have length of 1.")
	private String filterBy;
	
	private Object filterValue;
	
	
	public GetInfo(Integer range,Long paginationKey) {
		
		this.range=range;
		this.paginationKey=paginationKey;
			
	}
	
	public GetInfo() {}
	
	public Integer getRange() {
		return range;
	}
	public void setRange(Integer range) {
		this.range = range;
	}
	public Long getPaginationKey() {
		return paginationKey;
	}
	public void setPaginationKey(Long paginationKey) {
		this.paginationKey = paginationKey;
	}

	public String getFilterBy() {
		return filterBy;
	}

	public void setFilterBy(String filterBy) {
		this.filterBy = filterBy;
	}

	public Object getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(Object filterValue) {
		this.filterValue = filterValue;
	}
	
	
}
