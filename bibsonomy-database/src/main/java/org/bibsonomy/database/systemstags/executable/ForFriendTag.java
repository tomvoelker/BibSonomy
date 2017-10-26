/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.database.systemstags.executable;

import javax.print.attribute.standard.PresentationDirection;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.UnspecifiedErrorMessage;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.InboxDatabaseManager;
import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.SentSystemTag;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.PostUtils;
import static org.bibsonomy.util.ValidationUtils.present;

/**
 * This system tag creates a link to its post in the inbox of a specified user (the receiver)
 * The link to the post is its content_id
 * The link also receives all tags of the post including this one (deactivated and renamed to from:senderName)
 * The tag is deactivated (renamed to sent:receiverName) instead of removed
 * @author sdo
 */
public class ForFriendTag extends AbstractSystemTagImpl implements ExecutableSystemTag {

	/*
	 * TODO: Rename after release: The tag is not only for friends
	 * better: SendInboxTag oder SendTag
	 */
	private static final String NAME = "send";
	private static boolean toHide = true;

	private Tag tag; // the original (regular) tag that this systemTag was created from

	// a username specified in the .properties that can receive Posts without beeing friends
	private static String bibliographyUser;
	
	
	@Override
	public ForFriendTag newInstance() {
		return new ForFriendTag();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isToHide() {
		return toHide;
	}
	/**
	 * @param tag the tag to set
	 */
	public void setTag(final Tag tag) {
		this.tag = tag;
	}

	@Override
	public <T extends Resource> void performBeforeCreate(final Post<T> post, final DBSession session) {
		// nothing is performed
	}

	@Override
	public <T extends Resource> void performBeforeUpdate(final Post<T> newPost, final Post<T> oldPost, final PostUpdateOperation operation, final DBSession session) {
		// nothing is performed
	}

	@Override
	public <T extends Resource> void performAfterUpdate(final Post<T> newPost, final Post<T> oldPost, final PostUpdateOperation operation, final DBSession session) {
		// do exactly the same as in a Creation of a post (i. e. ignore which operation)
		this.performAfterCreate(newPost, session);
	}


	@Override
	public <T extends Resource> void performAfterCreate(final Post<T> post, final DBSession session) {
		// TODO: document why we insert inbox message after the creating of the post
		log.debug("performing after access");
		final String receiver = this.getArgument().toLowerCase();
		final String sender = post.getUser().getName();
		final String intraHash = post.getResource().getIntraHash();
		/*
		 * Check permissions
		 */
		if (!hasPermissions(sender, receiver, intraHash, session)) {
			// sender is not allowed to use this tag, errorMessages were added
			return;
		}
		log.debug("permissions granted");
		/*
		 * Rename forFriendTag from send:userName to sent:userName
		 * We deactivate the systemTag to avoid sending the Message again and again each time the sender updates his post
		 */
		final TagDatabaseManager tagDb = TagDatabaseManager.getInstance();
		// 1. delete all tags from the database (will be replaced by new ones)
		tagDb.deleteTags(post, session);
		// 2. rename this tag for the receiver (store senderName)
		this.tag.setName("from:" + sender);
		try {
		  // FIXME: move permission checks to inbox manager
			// 3. store the inboxMessage with tag from:senderName 
			InboxDatabaseManager.getInstance().createInboxMessage(sender, receiver, post, session);
			log.debug("message was created");
			// 4. rename this tag for the sender (store receiverName)
			this.tag.setName(SentSystemTag.NAME + SystemTagsUtil.DELIM + receiver);
		} catch (final UnsupportedResourceTypeException urte) {
			session.addError(PostUtils.getKeyForPost(post), new UnspecifiedErrorMessage(urte));
			log.warn("Added UnspecifiedErrorMessage (unsupported ResourceType) for post " + intraHash);
		}
		// 5. store the tags for the sender with the confirmation tag: sent:userName
		tagDb.insertTags(post, session);
	}


	/**
	 * Checks the preconditions to this tags usage, adds errorMessages
	 * using the tag is allowed, 
	 * - if the sender is in the friends list of the receiver or 
	 * - if a group exists that both sender and receiver are a member of
	 * @param intraHash
	 * @param session
	 * @param sender
	 * @param receiver
	 * @return true iff sender is allowed to use the tag
	 */
	private static boolean hasPermissions(final String sender, final String receiver, final String intraHash, final DBSession session) {
		final GroupDatabaseManager groupDb = GroupDatabaseManager.getInstance();
		final GeneralDatabaseManager generalDb = GeneralDatabaseManager.getInstance();
		
		/*
		 * user can't send inbox messages to himself
		 */
		if (sender.equals(receiver)) {
			return false;
		}
		
		/*
		 *  We decided to ignore errors in systemTags. Thus the user is free use any tag.
		 *  The drawback: If it is the user's intention to use a systemTag, he will never know if there was a typo! 
		 */
		if (!(generalDb.isFriendOf(sender, receiver, session) || groupDb.getCommonGroups(sender, receiver, session).size() > 0 || (present(bibliographyUser) && receiver.equals(bibliographyUser)))) {
			return false;
		}
		
		return true;
	}

	/*
	 * We overwrite this method because we want to interpret also the send tag 
	 * without prefix (sys/system) as systemTag and we need an argument
	 * @see org.bibsonomy.database.systemstags.AbstractSystemTagImpl#isInstance(java.lang.String)
	 */
	@Override
	public boolean isInstance(final String tagName) {
		// the send tag must have an argument, the prefix is not required
		return SystemTagsUtil.hasTypeAndArgument(tagName) && NAME.equals(SystemTagsUtil.extractType(tagName));
	}
	
	@Override
	public ExecutableSystemTag clone() {
		try {
			return (ExecutableSystemTag) super.clone();
		} catch (final CloneNotSupportedException ex) {
			// never ever reached
			return null;
		}
	}

	/**
	 * @param bibliographyUser the bibliographyUser to set
	 */
	public static void setBibliographyUser(String bibliographyUser) {
		ForFriendTag.bibliographyUser = bibliographyUser;
	}

}
