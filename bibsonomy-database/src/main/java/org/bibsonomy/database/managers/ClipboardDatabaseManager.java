/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.ClipboardParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;

/**
 * Manages Clipboard functionalities
 * 
 * TODO: rename to ClipboardDatabaseManager
 * TODO: implement full clipboard functionality
 * 
 * @author Dominik Benz
 * @author Christian Kramer
 */
public class ClipboardDatabaseManager extends AbstractDatabaseManager {
	private final static ClipboardDatabaseManager singleton = new ClipboardDatabaseManager();
	
	/**
	 * @return a singleon instance of this ClipboardDatabaseManager
	 */
	public static ClipboardDatabaseManager getInstance() {
		return singleton;
	}
	
	private final DatabasePluginRegistry plugins;

	private ClipboardDatabaseManager() {
		this.plugins = DatabasePluginRegistry.getInstance();
	}

	/**
	 * Retrieve the number of entries currently present in the clipboard of the
	 * given user.
	 * 
	 * @param username
	 *            the username
	 * @param session
	 *            the database session
	 * @return the number of entries currently stored in the clipboard
	 */
	public int getNumberOfClipboardEntries(final String username, final DBSession session) {
		final Integer result = this.queryForObject("getNumClipboardEntries", username, Integer.class, session);
		return saveConvertToint(result);
	}
	
	/**
	 * @param session
	 * @return the number of clipboard posts
	 */
	public int getNumberOfClipboardPosts(DBSession session) {
		final Integer result = this.queryForObject("getClipboardCount", Integer.class, session);
		return saveConvertToint(result);
	}

	/**
	 * @param session
	 * @return the number of clipboard posts in log table
	 */
	public int getNumberOfClipboardPostsInHistory(DBSession session) {
		final Integer result = this.queryForObject("getClipboardHistoryCount", Integer.class, session);
		return saveConvertToint(result);
	}
	
	/**
	 * creates clipboard items
	 * @param userName - name of the user from whose clipboard we want to delete the item
	 * @param contentId 
	 * @param session 
	 */
	public void createItem(final String userName, final int contentId, final DBSession session){
		final ClipboardParam param = new ClipboardParam();
		param.setUserName(userName);
		param.setContentId(contentId);
		this.insert("createClipboardItem", param, session);
	}
	
	/**
	 * deletes clipboard items
	 * @param userName - name of the user from whose clipboard we want to delete the item
	 * @param contentId 
	 * @param session 
	 */
	public void deleteItem(final String userName, final int contentId, final DBSession session){
		final ClipboardParam param = new ClipboardParam();			
		param.setUserName(userName);
		param.setContentId(contentId);
		this.plugins.onDeleteClipboardItem(param, session);
		this.delete("deleteClipboardItem", param, session);
	}
	
	/**
	 * Deletes all items with the given content_id from the clipboard.
	 * 
	 * @param contentId
	 * @param session
	 */
	public void deleteItems(final int contentId, final DBSession session){
		final ClipboardParam param = new ClipboardParam();			
		param.setContentId(contentId);
		this.plugins.onDeleteClipboardItem(param, session);
		this.delete("deleteClipboardItems", param, session);
	}
	
	/**
	 * updates clipboard items
	 * @param newContentId 
	 * @param contentId 
	 * @param session 
	 */
	public void updateItems(final int newContentId, final int contentId, final DBSession session){
		final ClipboardParam param = new ClipboardParam();
		param.setContentId(contentId);
		param.setNewContentId(newContentId);
		this.update("updateClipboardItems", param, session);
	}
	
	/**
	 * drops all clipboard items related to this user name
	 * 
	 * @param userName
	 * @param session
	 */
	public void deleteAllItems(final String userName, final DBSession session){
		this.plugins.onDeleteAllClipboardItems(userName, session);
		this.delete("deleteAllItems", userName, session);
	}
}