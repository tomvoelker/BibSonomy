package org.bibsonomy.community.model;

public class Tag extends org.bibsonomy.model.Tag {
	private static final long serialVersionUID = 1L;
	
	private double weight;
	
	private int topicId = 0;
	
	
	public Tag(String tagName) {
		super(tagName);
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public int getTopicId() {
		return topicId;
	}

}
