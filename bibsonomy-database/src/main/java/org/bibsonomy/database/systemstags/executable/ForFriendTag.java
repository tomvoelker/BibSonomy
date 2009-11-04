package org.bibsonomy.database.systemstags.executable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private final InboxDatabaseManager inboxDb;

	/**
	 * Constructor
	 */
	public ForFriendTag() {
		log.debug("initializing");
		// initialize database manager
		this.permissionDb = PermissionDatabaseManager.getInstance();
		this.inboxDb = InboxDatabaseManager.getInstance();
	}

	@Override
	public SystemTag newInstance() {
		return new ForFriendTag();
	}

	@Override
	public <T extends Resource> void performAfter(Post<T> post, final DBSession session) {
		log.debug("performing after access");
		/*
		 * Check: Is receiver a friend of the current user?
		 */
		String friendName = getValue();
		//permissionDb.ensureFriendOfUser(post.getUser(), friendName);
		/*
		 * get InboxDatabaseManager to store the Message
		 */
		inboxDb.createItem(post.getUser().getName(), friendName, post.getContentId(), session);
		
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
