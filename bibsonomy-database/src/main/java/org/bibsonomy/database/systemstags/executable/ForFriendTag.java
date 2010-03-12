package org.bibsonomy.database.systemstags.executable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	public <T extends Resource> void performAfter(final Post<T> post, final DBSession session) {
		log.debug("performing after access");
		String receiver = getValue().toLowerCase();
		String sender = post.getUser().getName();
		/*
		 * Check: Is the user (sender) in the list of friends of the receiver?
		 */
		if (!generalDb.isFriendOf(sender, receiver, session)){
			this.setError(Reason.FRIEND, post, receiver, session);
			// the is not allowed to use this tag, therefore we omit trying anything else with this tag
			return;
		}
		if (sender.equals(receiver)) {
			this.setError(Reason.SELF, post, receiver, session);
			// the is not allowed to use this tag, therefore we omit trying anything else with this tag
			return;
		}
		//TODO: What if contentId is currently unknown (= not stored in post)? => Exception

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
	public <T extends Resource> void performBefore(final Post<T> post, final DBSession session) {
		log.debug("performing before acess");
	}

	/**
	 * creates an errorMessage and adds it to the database exception in the session
	 * @param reason
	 * @param post
	 * @param groupName
	 * @param session
	 */
	private void setError(Reason reason, Post<? extends Resource> post, String receiver, DBSession session){
		String error="";
		String localizedMessageKey="";
		switch(reason) {
			case FRIEND: {
				error= this.getName()+ ": "  + receiver + " does not exist or did not add you as a friend.";
				localizedMessageKey="database.exception.systemTag.forFriend.notFriend";
				break;
			}
			case SELF: {
				error= this.getName()+": You can not send messages to yourself.";
				localizedMessageKey = "database.exception.systemTag.forFriend.self";
				break;
			}
		}
		session.addError(post.getResource().getIntraHash(), new SystemTagErrorMessage(error, localizedMessageKey, new String[]{receiver}));
	}

	/**
	 * small enum, to reduce code around the creation of errorMessages
	 * @author sdo
	 *
	 */
	private enum Reason {
		FRIEND,
		SELF;
	}

}
