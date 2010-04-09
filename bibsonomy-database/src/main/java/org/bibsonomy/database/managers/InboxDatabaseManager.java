package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.InboxParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

/**
 * Manages Inbox functionalities
 * s.a. counting, creating and deleting messages from a users inbox
 * 
 * @author sdo
 * @version $Id$
 */
public class InboxDatabaseManager extends AbstractDatabaseManager {
	private final static InboxDatabaseManager singleton = new InboxDatabaseManager(); 
	
	/**
	 * @return a singleton instance of this InboxDatabaseManager
	 */
	public static InboxDatabaseManager getInstance() {
		return singleton;
	}
	
	
	private final  GeneralDatabaseManager generalDb;
	
	private InboxDatabaseManager(){
		this.generalDb = GeneralDatabaseManager.getInstance();
	}
	
	/**
	 * Retrieve the number of messages currently present in the inbox of the
	 * given user.
	 * 
	 * @param receiver
	 *            the username of the owner of the inbox
	 * @param session
	 *            the database session
	 * @return the number of messages currently stored in the inbox
	 */
	public int getNumInboxMessages(String receiver, final DBSession session) {
		return queryForObject("getNumInboxMessages", receiver, Integer.class, session);
	}

	/**
	 * Retrieve the number of messages of the given resourceType currently present in the inbox of the
	 * given user.
	 * @param receiver  the requested user name of the the owner of the inbox 
	 * @param contentType  the contentType of the resource
	 * @param session
	 *            the database session
	 * @return the number of messages currently stored in the inbox
	 */
	public int getNumInboxMessages(final String receiver, final ConstantID contentType, final DBSession session) {
		final InboxParam param = new InboxParam();
		param.setContentType(contentType);
		param.setReceiver(receiver);
		return queryForObject("getNumInboxMessagesByType", param, Integer.class, session);
	}	
	
	private int getInboxMessages(final InboxParam param, final DBSession session) {
		return queryForObject("getNumInboxMessagesByHashAndSenderAndReceiver", param, Integer.class, session);
	}
	
	/**
	 * creates one inbox Message
	 * @param sender - name of the user who sends the item
	 * @param receiver - name of the user who'll receive the item in his inbox
	 * @param post
	 * @param session 
	 */
	public void createInboxMessage(final String sender, final String receiver, final Post<? extends Resource> post, final DBSession session){
		final InboxParam param = new InboxParam();
		final String intraHash = post.getResource().getIntraHash();
		// get a new Message Id
		this.getInboxMessages(param, session);
		param.setMessageId(this.generalDb.getNewContentId(ConstantID.IDS_INBOX_MESSAGE_ID, session));
		/*
		 * store the Message (without tags)
		 */
		param.setSender(sender);
		param.setContentId(post.getContentId());
		param.setIntraHash(intraHash);
		param.setReceiver(receiver);
		if (this.getInboxMessages(param, session) != 0) {
			this.deleteInboxMessage(sender, receiver, intraHash, session);
		}
		if(post.getResource().getClass().isAssignableFrom(BibTex.class)) {
			param.setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
		} else if (post.getResource().getClass().isAssignableFrom(Bookmark.class)) {
			param.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		} else {
			throw new UnsupportedResourceTypeException("Inbox messages can only be created for class types Bookmark or BibTex. The given resource was neiter.");
		}
		this.insert("createInboxMessage", param, session);
		/*
		 * store the Tags (as Strings)
		 */
		for (Tag tag: post.getTags()) {
			param.setTagName(tag.getName());
			this.insert("createInboxMessageTag", param, session);
		}
	}
	
	/**
	 * deletes all inbox items belonging to one message
	 * @param receiver - name of the user from whose inbox we want to delete the item
	 * @param sender - name of the user that sent the post
	 * @param intraHash to which we wish to erase the message
	 * @param session 
	 */
	public void deleteInboxMessage(final String sender, final String receiver, final String intraHash, final DBSession session){
		/* 
		 * for the deletion of the message the message_id has to be retrieved
		 */
		final InboxParam param = new InboxParam();			
		param.setSender(sender);
		param.setReceiver(receiver);
		param.setIntraHash(intraHash);
		int messageId = this.getMessageId(param, session);
		//TODO: What happens if no MessageId can be found?
		/*
		 * delete all entries (message and tags) with the now known message_id
		 */
		this.delete("deleteInboxMessage", messageId, session);
		this.delete("deleteInboxMessageTags", messageId, session);
	}
	
	private int getMessageId(InboxParam param, DBSession session) {
		return this.queryForObject("getInboxMessageId", param, Integer.class, session);
	}
	
	private List<Integer> getInboxMessageIds(final String receiver, final DBSession session) {
		return this.queryForList("getInboxMessageIds", receiver, Integer.class, session);
	}
	
	/**
	 * @param receiver
	 * @param session
	 */
	public void deleteAllInboxMessages(final String receiver, final DBSession session){
		// get all messageIds, that are to be deleted and erase their tags first
		List<Integer> messageIds = getInboxMessageIds(receiver, session);
		for (Integer messageId : messageIds) {
			this.delete("deleteInboxMessageTags", messageId, session);
		}
		this.delete("deleteAllInboxMessages", receiver, session);
	}
}