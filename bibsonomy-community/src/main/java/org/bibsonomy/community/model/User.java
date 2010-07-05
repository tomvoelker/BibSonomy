package org.bibsonomy.community.model;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.community.util.Pair;

public class User extends org.bibsonomy.model.User {
	private static final long serialVersionUID = 1L;
	
	private double weight;
	
	private Map<Integer,Double>  communityAffiliation = new HashMap<Integer, Double>();
	
	public User() {
		super();
	}
	
	public User(String userName) {
		super(userName);
	}

	public User(org.bibsonomy.model.User user) {
		setName(user.getName());
	}

	public void setAffiliation(final Integer clusterId, final Double weight) {
		this.communityAffiliation.put(clusterId, weight);
	}

	public void unSetAffiliation(final Integer runId, final Integer clusterId, final Double weight) {
		this.communityAffiliation.remove(new Pair<Integer,Integer>(runId,clusterId));
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public void setCommunityAffiliation(Map<Integer,Double> communityAffiliation) {
		this.communityAffiliation = communityAffiliation;
	}

	public Map<Integer,Double> getCommunityAffiliation() {
		return communityAffiliation;
	}
}
