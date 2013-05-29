package org.bibsonomy.recommender.connector.model;

import java.util.List;

import org.bibsonomy.model.User;

import recommender.core.interfaces.model.Item;

public class UserWrapper implements recommender.core.interfaces.model.RecommendationUser{

	private User user;
	private List<Item> items;
	
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
	 * @return the item
	 */
	public List<Item> getItems() {
		return items;
	}
	/**
	 * @param item the item to set
	 */
	public void setItems(List<Item> items) {
		this.items = items;
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

	@Override
	public Item getItem(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

}
