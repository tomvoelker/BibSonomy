package org.bibsonomy.database.managers;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Manages Basket functionalities
 * 
 * TODO: implement full basket functionality
 * 
 * @author Dominik Benz
 * @author Christian Kramer
 * @version $Id$
 */
public class BasketDatabaseManager extends AbstractDatabaseManager {
	private final static BasketDatabaseManager singleton = new BasketDatabaseManager();
	
	private final DatabasePluginRegistry plugins;

	private BasketDatabaseManager() {
		this.plugins = DatabasePluginRegistry.getInstance();
	}

	/**
	 * @return a singleon instance of this BasketDatabaseManager
	 */
	public static BasketDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Retrieve the number of entries currently present in the basket of the
	 * given user.
	 * 
	 * @param username
	 *            the username
	 * @param session
	 *            the database session
	 * @return the number of entries currently stored in the basket
	 */
	public int getNumBasketEntries(String username, final DBSession session) {
		return queryForObject("getNumBasketEntries", username, Integer.class, session);
	}
	
	/**
	 * creates basket items
	 * @param param 
	 * @param session 
	 */
	public void createItem(final BasketParam param, final DBSession session){
		this.insert("createBasketItem", param, session);
	}
	
	/**
	 * deletes basket items
	 * @param param 
	 * @param session 
	 */
	public void deleteItem(final BasketParam param, final DBSession session){
		this.plugins.onDeleteBasketItem(param, session);
		this.delete("deleteBasketItem", param, session);
	}
	
	/**
	 * updates basket items
	 * @param param 
	 * @param session 
	 */
	public void updateItem(final BasketParam param, final DBSession session){
		this.update("updateBasketItem", param, session);
	}
	
	/**
	 * drops all basket items related to this user name
	 * 
	 * @param userName
	 * @param session
	 */
	public void deleteAllItems(final String userName, final DBSession session){
		this.plugins.onDeleteAllBasketItems(userName, session);
		this.delete("deleteAllItems", userName, session);
	}
		
}