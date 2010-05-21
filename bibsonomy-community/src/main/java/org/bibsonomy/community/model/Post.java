package org.bibsonomy.community.model;

import org.bibsonomy.model.Resource;

public class Post<R extends Resource> extends org.bibsonomy.model.Post<R>{
	private static final long serialVersionUID = 1L;
	
	private int contentType;
	
	private Integer communityId;

	private double weight;

	public Post() {
		super();
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public int getContentType() {
		return contentType;
	}

	public void setCommunityId(Integer communityId) {
		this.communityId = communityId;
	}

	public Integer getCommunityId() {
		return communityId;
	}
}
