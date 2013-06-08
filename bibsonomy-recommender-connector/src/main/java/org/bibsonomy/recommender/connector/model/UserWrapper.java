package org.bibsonomy.recommender.connector.model;

import org.bibsonomy.model.User;

import recommender.core.interfaces.model.RecommendationUser;

public class UserWrapper implements RecommendationUser{

	private User user;
	
	public UserWrapper(User user) {
		this.user = user;
	}
	
	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return user.getOpenID();
	}
	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(String id) {
		this.user.setOpenID(id);
	}
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.user.getName();
	}
	/**
	 * @param name the name to set
	 */
	@Override
	public void setName(String name) {
		this.user.setName(name);
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

}
