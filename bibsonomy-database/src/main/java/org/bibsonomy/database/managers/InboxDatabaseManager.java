package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.InboxParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.InboxMessage;

/**
 * Manages Inbox functionalities
 *  
 *
 *  
 */

public class InboxDatabaseManager extends AbstractDatabaseManager {
	private final static InboxDatabaseManager singleton = new InboxDatabaseManager();
	
	//private final DatabasePluginRegistry plugins;

	//private InboxDatabaseManager() {
	//	this.plugins = DatabasePluginRegistry.getInstance();
	//}

	/**
	 * @return a singleton instance of this InboxDatabaseManager
	 */
	public static InboxDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Retrieve the number of entries currently present in the inbox of the
	 * given user.
	 * 
	 * @param receiver
	 *            the username of the owner of the inbox
	 * @param session
	 *            the database session
	 * @return the number of entries currently stored in the inbox
	 */
	public int getNumInboxItems(String receiver, final DBSession session) {
		return queryForObject("getNumInboxItems", receiver, Integer.class, session);
	}
	
	
	public List<InboxMessage> getInboxMessages(String receiver, final DBSession session) {
		return queryForList("getInboxItems", receiver, session);
	}
	/**
	 * creates inbox items
	 * @param sender - name of the user who sends the item
	 * @param receiver - name of the user who'll receive the item in his inbox
	 * @param contentId 
	 * @param session 
	 */
	public void createItem(final String sender, final String receiver, final int contentId, final DBSession session){
		final InboxParam param = new InboxParam();			
		param.setSender(sender);
		param.setContentId(contentId);
		param.setReceiver(receiver);
		this.insert("createInboxItem", param, session);
	}
	
	/**
	 * deletes inbox items
	 * @param receiver - name of the user from whose inbox we want to delete the item
	 * @param contentId 
	 * @param session 
	 */
	public void deleteItem(final String receiver, final int contentId, final DBSession session){
		final InboxParam param = new InboxParam();			
		param.setReceiver(receiver);
		param.setContentId(contentId);
		//this.plugins.onDeleteBasketItem(param, session);
		this.delete("deleteInboxItem", param, session);
	}
	
	/**
	 * updates basket items
	 * @param param 
	 * @param session 
	 */
	/*public void updateItem(final BasketParam param, final DBSession session){
		this.update("updateBasketItem", param, session);
	}*/
	
	//public void getItem(final String receiver){
		
	//}
	
	/**
	 * drops all inbox items of users inbox
	 * 
	 * @param receiver
	 * @param session
	 */
	public void deleteAllItems(final String receiver, final DBSession session){
		//this.plugins.onDeleteAllBasketItems(userName, session);
		this.delete("deleteAllInboxItems", receiver, session);
	}
		
}