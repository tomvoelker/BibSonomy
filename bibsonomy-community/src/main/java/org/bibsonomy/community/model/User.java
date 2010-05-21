package org.bibsonomy.community.model;

public class User extends org.bibsonomy.model.User {
	private static final long serialVersionUID = 1L;
	
	private double weight;
	
	public User(String userName) {
		super(userName);
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}
}
