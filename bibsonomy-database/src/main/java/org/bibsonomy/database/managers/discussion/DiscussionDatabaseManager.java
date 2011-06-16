package org.bibsonomy.database.managers.discussion;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.discussion.DiscussionChain;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;

/**
 * @author dzo
 * @version $Id$
 */
public class DiscussionDatabaseManager extends AbstractDatabaseManager {
	private static final DiscussionDatabaseManager INSTANCE = new DiscussionDatabaseManager();


	private static final DiscussionChain CHAIN = new DiscussionChain();
	
	/**
	 * @return the @{link:DiscussionManager} instance
	 */
	public static DiscussionDatabaseManager getInstance() {
		return INSTANCE;
	}

	private DiscussionDatabaseManager() {

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
		return CHAIN.getFirstElement().perform(param, session);
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
		return rootItems;
	}
}
