package org.bibsonomy.database.systemstags.executable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.InboxDatabaseManager;
import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * This system tag creates a link to its post in the inbox of a specified user (the receiver)
 * The link to the post is its content_id
 * The link also receives all tags of the post including this one (deactivated and renamed to from:senderName)
 * The tag is deactivated (renamed to sent:receiverName)
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
	private final GeneralDatabaseManager generalDb; //needed to check: Is sender friend of receiver?
	private final InboxDatabaseManager inboxDb;
	private final TagDatabaseManager tagDb;

	/**
	 * Constructor
	 */
	public ForFriendTag() {
		log.debug("initializing");
		// initialize database manager
		this.inboxDb = InboxDatabaseManager.getInstance();
		this.generalDb=GeneralDatabaseManager.getInstance();
		this.tagDb = TagDatabaseManager.getInstance();
	}

	@Override
	public SystemTag newInstance() {
		return new ForFriendTag();
	}

	@Override
	public <T extends Resource> void performAfter(Post<T> post, final DBSession session) {
		log.debug("performing after access");
		String receiver = getValue().toLowerCase();
		String sender = post.getUser().getName();
		/*
		 * Check: Is the user (sender) in the list of friends of the receiver?
		 */
		if (!generalDb.isFriendOf(sender, receiver, session)){
			throw new ValidationException("You can only send posts to users that have added you as a friend.");
		}
		//TODO: What if contentId is currently unknown? i.e. not stored in post => exception

		/*
		 * rename forFriendTag from send:userName to sent:userName
		 * We deactivate the systemTag to avoid sending the Message again and again each time the sender updates his post
		 */
		this.tagDb.deleteTags(post, session);		// 1. delete all tags from the database (will be replaced by new ones)
		this.getTag().setName("from:" + sender);	// 2. rename this tag for the receiver (store senderName)
		inboxDb.createInboxMessage(sender, receiver, post, session); // 3. store the inboxMessage with tag from:senderName 
		this.getTag().setName("sent:" + receiver);	// 4. rename this tag for the sender (store receiverName)
		this.tagDb.insertTags(post, session);		// 5. store the tags for the sender with the confirmation tag: sent:userName


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
