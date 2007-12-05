package org.bibsonomy.database.managers;

import org.apache.log4j.Logger;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.util.DBSession;

/**
 * Manages Basket functionalities 
 * 
 * TODO: implement full basket functionality
 *
 * @version: $Id$
 * @author:  dbenz
 *
 */
public class BasketDatabaseManager extends AbstractDatabaseManager {
	
	private static final Logger LOGGER = Logger.getLogger(BasketDatabaseManager.class);
	private final static BasketDatabaseManager singleton = new BasketDatabaseManager();
	
	private BasketDatabaseManager() {
	}

	/**
	 * @return a singleon instance of this BasketDatabaseManager
	 */
	public static BasketDatabaseManager getInstance() {
		return singleton;
	}
		
	/**
	 * Retrieve the number of entries currently present in the basket
	 * of the given user.
	 * 
	 * @param username the username
	 * @param session the database session
	 * @return the number of entries currently stored in the basket
	 */
	public int getNumBasketEntries(String username, final DBSession session) {
		return queryForObject("getNumBasketEntries", username, Integer.class, session);
	}
	
}
