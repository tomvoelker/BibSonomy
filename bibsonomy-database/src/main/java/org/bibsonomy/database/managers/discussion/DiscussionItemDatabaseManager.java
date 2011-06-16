package org.bibsonomy.database.managers.discussion;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.validation.DatabaseModelValidator;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.DiscussionItemUtils;

/**
 * @author dzo
 * @version $Id$
 * @param <D> 
 */
public abstract class DiscussionItemDatabaseManager<D extends DiscussionItem> extends AbstractDatabaseManager {
	private static final Log log = LogFactory.getLog(DiscussionItemDatabaseManager.class);	
	
	protected final DatabasePluginRegistry plugins;
	protected final GeneralDatabaseManager generalDb;
	
	private final DatabaseModelValidator<D> validator;

	protected DiscussionItemDatabaseManager() {
		this.plugins = DatabasePluginRegistry.getInstance();
		this.generalDb = GeneralDatabaseManager.getInstance();
		
		this.validator = new DatabaseModelValidator<D>();
	}
	
	protected D getDiscussionItemByHashForResource(final String interHash, final String username, final String hash, final DBSession session) {
		final DiscussionItemParam<D> param = this.createDiscussionItemParam(interHash, username);
		param.setHash(hash);
		
		final List<D> discussionItems = this.getDiscussionItemsByHashForResource(param, session);
		
		if (present(discussionItems)) {
			if (discussionItems.size() > 1) {
				log.fatal("found multiple discussion items for resource '" + interHash + "' with same hash '" + hash + "'");
			}
			
			return discussionItems.get(0);
		}
		
		return null;
	}
	
	// TODO: remove
	protected abstract List<D> getDiscussionItemsByHashForResource(final DiscussionItemParam<D> param, final DBSession session);
	
	/**
	 * creates a new discussion item 
	 * @param interHash
	 * @param discussionItem
	 * @param session
	 * @return <code>true</code> iff the discussion item was created successfully
	 */
	public boolean createDiscussionItemForResource(final String interHash, final D discussionItem, final DBSession session) {
		/*
		 * check if no interHash is present
		 */
		if (!present(interHash)) {
			throw new ValidationException("please provide an interHash for the discussion item");
		}
		
		session.beginTransaction();
		try {
			final String username = discussionItem.getUser().getName();
			final String parentHash = discussionItem.getParentHash();
			/*
			 * check if parent is in database
			 */
			if (present(parentHash)) {
				final DiscussionItem parentComment = this.getDiscussionItemForHash(interHash, parentHash, session);
				if (!present(parentComment)) {
					throw new ValidationException("parent discussion item not found"); // TODO: error message?!
				}
			}
			
			
			/*
			 * get a new discussion id from db (used for group visibility)
			 */
			final int discussionId = this.generalDb.getNewId(ConstantID.IDS_DISCUSSION_ITEM_ID, session);
			discussionItem.setId(discussionId);
						
			/*
			 * populate with date and recalculate hash
			 */
			final Date creationDate = new Date();
			discussionItem.setDate(creationDate);
			discussionItem.setHash(DiscussionItemUtils.recalculateHash(discussionItem));
			
			/*
			 * create the discussion item
			 */
			final boolean created = this.createDiscussionItem(interHash, discussionItem, session, discussionId);
			
			/*
			 * if creation was successful insert the group ids
			 */
			if (created) {
				/*
				 * insert groups
				 */
				final DiscussionItemParam<D> param = new DiscussionItemParam<D>();
				param.setUserName(username);
				param.setDiscussionId(discussionId);
				param.setDate(creationDate);
				
				this.insertGroups(param, discussionItem.getGroups(), session);
			}
			
			session.commitTransaction();
			return created;
		} finally {
			session.endTransaction();
		}
	}

	private void insertGroups(final DiscussionItemParam<D> param, final Set<Group> groups, final DBSession session) {
		for (final Group group : groups) {
			param.setGroupId(group.getGroupId());
			this.insert("insertDiscussionGroup", param, session);
		}
	}
	
	private void deleteGroups(final DiscussionItemParam<?> param, final DBSession session) {
		this.delete("deleteAllGroupsForDiscussionItemById", param, session);
	}

	protected abstract boolean createDiscussionItem(final String interHash, final D discussionItem, final DBSession session, int discussionId);

	protected DiscussionItemParam<D> createDiscussionItemParam(final String interHash, final String username) {
		final DiscussionItemParam<D> param = new DiscussionItemParam<D>();
		this.fillDiscussionItemParam(param, interHash, username);
		return param;
	}
	
	protected void fillDiscussionItemParam(final DiscussionItemParam<?> param, final String interHash, final String username) {
		param.setInterHash(interHash);
		param.setUserName(username);
	}
	
	/**
	 * updates a discussion item
	 * 
	 * @param interHash
	 * @param oldHash
	 * @param discussionItem
	 * @param session
	 * @return <code>true</code> iff the discussion item was updated successfully
	 */
	public boolean updateDiscussionItemForResource(final String interHash, final String oldHash, final D discussionItem, final DBSession session) {
		if (!present(interHash)) {
			throw new ValidationException("please provide an interHash");
		}
		
		log.debug("updating discussionItem with hash " + discussionItem.getHash() + " for resource " + interHash);
		session.beginTransaction();
		try {
			/*
			 * first check if old discussion item is in database
			 */
			final String username = discussionItem.getUser().getName();
			final D oldDiscussionItem = this.getDiscussionItemByHashForResource(interHash, username, oldHash, session);
			final Date changeDate = new Date(); // only one change date
			
			if (!present(oldDiscussionItem)) {
				return false; // TODO error message?
			}
			
			/*
			 * set dates and recalculate hash
			 */
			discussionItem.setDate(oldDiscussionItem.getDate()); // be sure
			discussionItem.setChangeDate(changeDate);
			discussionItem.setHash(DiscussionItemUtils.recalculateHash(discussionItem));
			
			/*
			 * first check discussion item to update
			 */
			this.checkDiscussionItem(discussionItem, session);
			
			/*
			 * inform the plugins
			 */
			this.plugins.onDiscussionUpdate(interHash, discussionItem, oldDiscussionItem, session);
			
			final boolean updated = this.updateDiscussionItem(interHash, discussionItem, oldDiscussionItem, session);
			
			if (updated) {
				/*
				 * update groups
				 */
				final DiscussionItemParam<D> param = new DiscussionItemParam<D>();
				param.setUserName(username);
				param.setDiscussionId(oldDiscussionItem.getId());
				param.setDate(discussionItem.getDate());
				param.setChangeDate(changeDate);
				
				this.deleteGroups(param, session);
				this.insertGroups(param, discussionItem.getGroups(), session);
			}
			
			session.commitTransaction();
			return updated;
		} finally {
			session.endTransaction();
		}
	}
	
	protected DiscussionItem getDiscussionItemForHash(final String interHash, final String hash, final DBSession session) {
		final DiscussionItemParam<DiscussionItem> param = new DiscussionItemParam<DiscussionItem>();
		param.setHash(hash);
		param.setInterHash(interHash);
		
		return this.queryForObject("getDiscussionItemForResourceByHash", param, DiscussionItem.class, session);
	}
	
	protected abstract boolean updateDiscussionItem(final String interHash, final D discussionItem, final D oldDiscussionItem, final DBSession session);
	
	protected abstract void checkDiscussionItem(final D discussionItem, final DBSession session);
	
	protected void checkLength(final D discussionItem, final DBSession session) {
		this.validator.validateFieldLength(discussionItem, discussionItem.getHash(), session);
	}

	/**
	 * deletes the specified hash from the query
	 * 
	 * @param interHash
	 * @param user
	 * @param hash
	 * @param session
	 * @return <code>true</code> iff the discussion item was deleted
	 */
	public boolean deleteDiscussionItemForResource(final String interHash, final User user, final String hash, final DBSession session) {
		session.beginTransaction();
		try {
			final String username = user.getName();
			final D oldItem = this.getDiscussionItemByHashForResource(interHash, username, hash, session);
			
			if (!present(oldItem)) {
				return false;
			}
			
			final DiscussionItemParam<D> param = this.createDiscussionItemParam(interHash, username);
			param.setHash(hash);
			param.setDiscussionId(oldItem.getId());
			
			/*
			 * inform the plugins (logging, …)
			 */
			this.plugins.onDiscussionItemDelete(interHash, oldItem, session);
			
			this.handleDiscussionItemDelete(interHash, user, oldItem, session);
			
			final boolean hasChildren = this.queryForObject("hasSubDiscussionItems", param, Boolean.class, session);
			
			if (hasChildren) {
				/*
				 * to keep thread structure we don't delete the item we only
				 * clean it and don't delete group assignments
				 * otherwise no result set
				 */
				this.update("cleanDiscussionItem", param, session);
			} else {
				// delete discussion item
				this.delete("deleteDiscussionItem", param, session);
				
				// delete groups
				this.deleteGroups(param, session);
			}		
			
			session.commitTransaction();
			return true;
		} finally {
			session.endTransaction();
		}
	}

	protected void handleDiscussionItemDelete(final String interHash, final User user, final D oldDiscussionItem, final DBSession session) {
		// noop
	}
}
