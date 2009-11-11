package org.bibsonomy.database.systemstags.executable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.InboxDatabaseManager;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * System tag 'sys:send:&lt;groupname&gt;'
 * Description: 
 *   If user tags a post with [sys:]for:&lt;groupname&gt;, a message is sent to 
 *   the inbox of the receiver. 
 *   
 *  Precondition: 
 *   User is friend of the receiver of this post
 * @author sdo
 * @version $Id$
 */
public class ForFriendTag extends SystemTag {
	private static final Log log = LogFactory.getLog(ForFriendTag.class);
	//------------------------------------------------------------------------
	/**
	 * This database manager is needed to ensure that a user is allowed to send
	 * posts to given user.
	 */
	private final PermissionDatabaseManager permissionDb;
	private final GeneralDatabaseManager generalDb; //needed to check: Is sender friend of receiver?
	private final InboxDatabaseManager inboxDb;

	/**
	 * Constructor
	 */
	public ForFriendTag() {
		log.debug("initializing");
		// initialize database manager
		this.permissionDb = PermissionDatabaseManager.getInstance();
		this.inboxDb = InboxDatabaseManager.getInstance();
		this.generalDb=GeneralDatabaseManager.getInstance();
	}

	@Override
	public SystemTag newInstance() {
		return new ForFriendTag();
	}

	@Override
	public <T extends Resource> void performAfter(Post<T> post, final DBSession session) {
		log.debug("performing after access");
		/*
		 * Check: Is the user (sender) in the list of friends of the receiver?
		 */
		// create the receiver as user
		String receiver = getValue().toLowerCase();

		// check if the user (sender) is a friend of the receiver
		if (!generalDb.isFriendOf(post.getUser().getName(), receiver, session)){
			throw new ValidationException("You can only send posts to users that have added you as a friend.");
		}
		/*
		 * get InboxDatabaseManager to store the Message
		 */
		inboxDb.createItem(post.getUser().getName(), receiver, post.getContentId(), session);
		
	}

	@Override
	public <T extends Resource> void performBefore(Post<T> post, final DBSession session) {
		log.debug("performing before acess");
	}




	
	/**
	 * Removes all tags with given old name and adds new tag with given new name.
	 * 
	 * @param tags
	 * @param oldName
	 * @param newName
	 */

}
