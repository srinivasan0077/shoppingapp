package com.shoppingapp.entities;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


public class GetInfo {

	@Min(value = 1)
	@Max(value = 100)
	private Integer range;
	
	private Long paginationKey;
	
	public GetInfo(Integer range,Long paginationKey) {
		
		this.range=range;
		this.paginationKey=paginationKey;
		
		
	}
	
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
	
	
}
