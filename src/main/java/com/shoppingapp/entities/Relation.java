package com.shoppingapp.entities;

import javax.validation.constraints.NotNull;

public class Relation {

	@NotNull(message = "Field varaintId cannot be null")
	private Long variantId;
	@NotNull(message = "Field topicId cannot be null")
	private Long topicId;
	
	public Long getTopicId() {
		return topicId;
	}
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}
	public Long getVariantId() {
		return variantId;
	}
	public void setVariantId(Long variantId) {
		this.variantId = variantId;
	}
	
}
