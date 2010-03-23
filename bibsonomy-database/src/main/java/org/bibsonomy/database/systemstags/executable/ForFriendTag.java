package org.bibsonomy.database.systemstags.executable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.SystemTagErrorMessage;
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
 * The tag is deactivated (renamed to sent:receiverName) instead of removed
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
	public <T extends Resource> void performBeforeCreate(final Post<T> post, final DBSession session) {
		log.debug("performing before acess");
		// nothing is performed
	}

	@Override
	public <T extends Resource> void performBeforeUpdate(Post<T> newPost, final Post<T> oldPost, PostUpdateOperation operation, DBSession session) {
		log.debug("performing before acess");
		// nothing is performed
	}

	@Override
	public <T extends Resource> void performAfterUpdate(Post<T> newPost, final Post<T> oldPost, PostUpdateOperation operation, DBSession session) {
		// do exactly the same as in a Create => ignore operation
		//this.performAfterCreate(post, session);
	}



	@Override
	public <T extends Resource> void performAfterCreate(final Post<T> post, final DBSession session) {
		log.debug("performing after access");
		String receiver = getValue().toLowerCase();
		String sender = post.getUser().getName();
		String intraHash = post.getResource().getIntraHash();
		/*
		 * Check permissions
		 */
		if (!this.hasPermissions(sender, receiver, intraHash, session)) {
			// sender is not allowed to use this tag, errorMessages were added
			return;
		}
		//TODO: What if contentId is currently unknown (= not stored in post)? => Exception
		/*
		 * Rename forFriendTag from send:userName to sent:userName
		 * We deactivate the systemTag to avoid sending the Message again and again each time the sender updates his post
		 */
		this.tagDb.deleteTags(post, session);		// 1. delete all tags from the database (will be replaced by new ones)
		this.getTag().setName("from:" + sender);	// 2. rename this tag for the receiver (store senderName)
		inboxDb.createInboxMessage(sender, receiver, post, session); // 3. store the inboxMessage with tag from:senderName 
		this.getTag().setName("sent:" + receiver);	// 4. rename this tag for the sender (store receiverName)
		this.tagDb.insertTags(post, session);		// 5. store the tags for the sender with the confirmation tag: sent:userName
}


	/**
	 * Checks the preconditions to this tags usage, adds errorMessages
	 * @param intraHash
	 * @param session
	 * @param sender
	 * @param receiver
	 * @return true iff sender is allowed to use the tag
	 */
	private boolean hasPermissions(final String sender, final String receiver, final String intraHash, final DBSession session) {
		if (!generalDb.isFriendOf(sender, receiver, session)) {
			final String defaultMessage = this.getName()+ ": "  + receiver + " does not exist or did not add you as a friend.";
			session.addError(intraHash, new SystemTagErrorMessage(defaultMessage, "database.exception.systemTag.forFriend.notFriend", new String[]{receiver}));
			return false;
		}
		if (sender.equals(receiver)) {
			final String defaultMessage = this.getName()+": You can not send messages to yourself.";
			session.addError(intraHash, new SystemTagErrorMessage(defaultMessage, "database.exception.systemTag.forFriend.self", new String[]{receiver}));
			return false;
		}
		return true;
	}

}
