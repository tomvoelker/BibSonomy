/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.database.managers.discussion;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.Chain;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;

/**
 * @author dzo
 */
public class DiscussionDatabaseManager extends AbstractDatabaseManager {
	private static final DiscussionDatabaseManager INSTANCE = new DiscussionDatabaseManager();
	protected final DatabasePluginRegistry plugins;

	/**
	 * @return the @{link:DiscussionManager} instance
	 */
	public static DiscussionDatabaseManager getInstance() {
		return INSTANCE;
	}

	
	private Chain<List<DiscussionItem>, DiscussionItemParam<?>> chain;

	private DiscussionDatabaseManager() {
		this.plugins = DatabasePluginRegistry.getInstance();
	}
	
	/**
	 * @param loginUser the login user (to get groups of the user)
	 * @param interHash 
	 * @param session
	 * @return a list of discussion items
	 */
	public List<DiscussionItem> getDiscussionSpace(final User loginUser, final String interHash, final DBSession session) {
		final DiscussionItemParam<?> param = new DiscussionItemParam<DiscussionItem>();
		param.setInterHash(interHash);
		param.setUserName(loginUser.getName());
		param.addGroupsAndGroupnames(UserUtils.getListOfGroups(loginUser));
		
		/*
		 * get the list of discussion items
		 */
		return this.chain.perform(param, session);
	}
	
	/**
	 * 
	 * @param interHash
	 * @param loginUser 
	 * @param visibleGroupIDs 
	 * @param session
	 * @return all resources for the specific resource
	 */
	public List<DiscussionItem> getDiscussionSpaceForResource(final String interHash, final String loginUser, final List<Integer> visibleGroupIDs, final DBSession session) {
		final DiscussionItemParam<DiscussionItem> param = this.createDiscussionParam(interHash, loginUser);
		param.setGroups(visibleGroupIDs);
		param.setInterHash(interHash);
		
		// TODO: maybe we should query here for a map (item.hash => item)
		return this.buildThreadStructure(this.queryForList("getDiscussionSpaceForResource", param, DiscussionItem.class, session));
	}
	
	private DiscussionItemParam<DiscussionItem> createDiscussionParam(final String interHash, final String userName) {
		final DiscussionItemParam<DiscussionItem> param = new DiscussionItemParam<DiscussionItem>();
		param.setUserName(userName);
		param.setInterHash(interHash);
		return param;
	}

	protected List<DiscussionItem> buildThreadStructure(final List<DiscussionItem> discussionItems) {
		/*
		 * build thread structure for discussion items
		 * 
		 * 1.) build a map discussionItem.hash => discussionItem
		 */
		final Map<String, DiscussionItem> dicussionItemsMap = new HashMap<String, DiscussionItem>();
		for (final DiscussionItem discussionItem : discussionItems) {
			dicussionItemsMap.put(discussionItem.getHash(), discussionItem);
		}
		
		/*
		 * 2.) loop through all discussion items and find roots (no parentHash)
		 * and add all sub items to its parent
		 */
		final List<DiscussionItem> rootItems = new LinkedList<DiscussionItem>();
		for (final DiscussionItem discussionItem : discussionItems) {
			final String parentHash = discussionItem.getParentHash();
			if (!present(parentHash)) {
				/*
				 *  no parentHash => a root discussion item
				 */
				rootItems.add(discussionItem);
			} else {
				final DiscussionItem parentItem;
				
				/*
				 * no parent => maybe deleted or invisible for the user
				 */
				if (!dicussionItemsMap.containsKey(parentHash)) {
					// we don't know which item it is
					parentItem = new DiscussionItem();
					parentItem.setHash(parentHash);
					dicussionItemsMap.put(parentHash, parentItem);
				} else {
					 parentItem = dicussionItemsMap.get(parentHash);
				}
				
				parentItem.addToDiscussionItems(discussionItem);
			}
		}
		
		/*
		 * we want that root items are sorted by date descending but sub items
		 * sorted ascending
		 * root items are currently sorted ascending (by sql query) so we only
		 * need to reverse them
		 */
		Collections.reverse(rootItems);
		return rootItems;
	}
	
	public void updateDiscussionsInGroupFromLeavingUser(User leavingUser, int groupId, DBSession session) {
		final DiscussionItemParam<DiscussionItem> param = new DiscussionItemParam<>();
		param.setUserName(leavingUser.getName());
		param.setGroupId(groupId);
		// FIXME: (groups) Logging of group change missing
		
		this.plugins.onDiscussionUpdate("interhash", null, null, session);
		
		this.update("updateDiscussionsInGroupFromLeavingUser", param, session);
	}

	/**
	 * @param chain the chain to set
	 */
	public void setChain(final Chain<List<DiscussionItem>, DiscussionItemParam<?>> chain) {
		this.chain = chain;
	}
}
