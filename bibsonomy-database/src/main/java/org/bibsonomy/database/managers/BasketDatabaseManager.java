package org.bibsonomy.database.managers;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;

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
		super();
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
	 * @param userName - name of the user from whose basket we want to delete the item
	 * @param contentId 
	 * @param session 
	 */
	public void createItem(final String userName, final int contentId, final DBSession session){
		final BasketParam param = new BasketParam();			
		param.setUserName(userName);
		param.setContentId(contentId);
		this.insert("createBasketItem", param, session);
	}
	
	/**
	 * deletes basket items
	 * @param userName - name of the user from whose basket we want to delete the item
	 * @param contentId 
	 * @param session 
	 */
	public void deleteItem(final String userName, final int contentId, final DBSession session){
		final BasketParam param = new BasketParam();			
		param.setUserName(userName);
		param.setContentId(contentId);
		this.plugins.onDeleteBasketItem(param, session);
		this.delete("deleteBasketItem", param, session);
	}
	
	/**
	 * Deletes all items with the given content_id from the basket.
	 * 
	 * @param contentId
	 * @param session
	 */
	public void deleteItems(final int contentId, final DBSession session){
		final BasketParam param = new BasketParam();			
		param.setContentId(contentId);
		this.plugins.onDeleteBasketItem(param, session);
		this.delete("deleteBasketItems", param, session);
	}
	/**
	 * updates basket items
	 * @param session 
	 * @param param 
	 */
	public void updateItems(final int newContentId, final int contentId, final DBSession session){
		final BasketParam param = new BasketParam();
		param.setContentId(contentId);
		param.setNewContentId(newContentId);
		this.update("updateBasketItems", param, session);
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