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
package org.bibsonomy.database.plugin;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.params.BibTexExtraParam;
import org.bibsonomy.database.params.DocumentParam;
import org.bibsonomy.database.params.InboxParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.GoldStandardRelation;

/**
 * This interface supplies hooks which can be implemented by plugins. This way
 * the code for basic operations, like updating a bookmark or publication, can
 * be kept concise and is easier to maintain.<br/>
 * 
 * If a method returns <code>null</code> its execution will be skipped.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @author Stefan Stützer
 * @author Anton Wilhelm
 */
public interface DatabasePlugin {

	/**
	 * Called when a publication is inserted.
	 * 
	 * @param post
	 * @param session
	 */
	public void onPublicationInsert(Post<? extends Resource> post, DBSession session);

	/**
	 * Called when a publication is deleted.
	 * 
	 * @param contentId
	 * @param session
	 */
	public void onPublicationDelete(int contentId, DBSession session);

	/**
	 * Called when a publication is updated.
	 * 
	 * @param newContentId
	 * @param contentId
	 * @param session
	 */
	public void onPublicationUpdate(int newContentId, int contentId, DBSession session);

	/**
	 * Called when a gold standard publication is created.
	 * 
	 * @param interhash
	 * @param session
	 */
	public void onGoldStandardCreate(String interhash, DBSession session);

	/**
	 * Called when a gold standard publication will be updated.
	 * 
	 * @param newContentId
	 * @param contentId
	 * @param newInterhash
	 * @param interhash
	 * @param session
	 */
	public void onGoldStandardUpdate(int newContentId, int contentId, String newInterhash, String interhash, DBSession session);
	
	/**
	 * Called when a reference of a gold standard publication will be created
	 * @param userName
	 * @param interHash_publication
	 * @param interHash_reference
	 * @param interHash_relation
	 */
	public void onGoldStandardPublicationReferenceCreate(String userName, String interHash_publication, String interHash_reference, String interHash_relation);
	
	/**
	 * Called when a reference of a gold standard publication will be deleted
	 * 
	 * @param userName
	 * @param interHash_publication
	 * @param interHash_reference
	 * @param interHashRelation
	 * @param session
	 */
	public void onGoldStandardRelationDelete(String userName, String interHash_publication, String interHash_reference, GoldStandardRelation interHashRelation, DBSession session);
	
	/**
	 * Called when a gold standard publication is deleted.
	 * 
	 * @param interhash
	 * @param session
	 */
	public void onGoldStandardDelete(String interhash, DBSession session);
	
	/**
	 * Called when a Bookmark is inserted.
	 * 
	 * @param post
	 * @param session
	 */
	public void onBookmarkInsert(Post<? extends Resource> post, DBSession session);

	/**
	 * Called when a Bookmark is deleted.
	 * 
	 * @param contentId
	 * @param session
	 */
	public void onBookmarkDelete(int contentId, DBSession session);

	/**
	 * Called when a Bookmark is updated.
	 * 
	 * @param newContentId
	 * @param contentId
	 * @param session
	 */
	public void onBookmarkUpdate(int newContentId, int contentId, DBSession session);
	
	/**
	 * Called when a TagRelation is deleted.
	 * 
	 * @param upperTagName
	 * @param lowerTagName
	 * @param userName
	 * @param session
	 */
	public void onTagRelationDelete(String upperTagName, String lowerTagName, String userName, DBSession session);
	
	/**
	 * Called when a Concept is deleted.
	 * 
	 * @param conceptName
	 * @param userName
	 * @param session
	 */
	public void onConceptDelete(String conceptName, String userName, DBSession session);
	
	/**
	 * Called when a Tag is deleted.
	 * 
	 * @param contentId
	 * @param session
	 */
	public void onTagDelete(int contentId, DBSession session);
	
	/**
	 * Called when a User is inserted.
	 * 
	 * @param userName
	 * @param session
	 */
	public void onUserInsert(String userName, DBSession session);

	/**
	 * Called when a User is deleted.
	 * 
	 * @param userName
	 * @param session
	 */
	public void onUserDelete(String userName, DBSession session);

	/**
	 * Called when a User is updated.
	 * 
	 * @param userName
	 * @param session
	 */
	public void onUserUpdate(String userName, DBSession session);	

	/**
	 * Called when a user is removed from a group.
	 * 
	 * @param userName
	 * @param groupId
	 * @param session
	 */
	public void onRemoveUserFromGroup(String userName, int groupId, DBSession session);
	
	/**
	 * Called when a fellowship will be deleted
	 * 
	 * @param param
	 * @param session
	 */
	public void onDeleteFellowship(final UserParam param, final DBSession session);
	
	/**
	 * Called when a friendship will be deleted
	 * 
	 * @param param
	 * @param session
	 */
	public void onDeleteFriendship(final UserParam param, final DBSession session);
	
	/**
	 * Called when a basket item will be deleted
	 * 
	 * @param param
	 * @param session
	 */
	public void onDeleteBasketItem(final BasketParam param, final DBSession session);
	
	/**
	 * Called when all basket items will be deleted
	 * 
	 * @param userName 
	 * @param session 
	 * 
	 */
	public void onDeleteAllBasketItems(final String userName, final DBSession session);
	
	/**
	 * called when a comment was updated
	 * 
	 * @param interHash
	 * @param comment
	 * @param oldComment
	 * @param session
	 */
	public void onDiscussionUpdate(final String interHash, DiscussionItem comment, DiscussionItem oldComment, DBSession session);	
	
	/**
	 * called when a comment will be deleted
	 * 
	 * @param interHash
	 * @param deletedComment
	 * @param session
	 */
	public void onDiscussionItemDelete(final String interHash, final DiscussionItem deletedComment, final DBSession session);
	
	/**
	 * called when a document will be deleted
	 * 
	 * @param deletedDocumentParam
	 * @param session
	 */
	public void onDocumentDelete(final DocumentParam deletedDocumentParam, final DBSession session);
	
	/**
	 * called when a will be updated
	 * 
	 * @param updatedDocumentParam
	 * @param session
	 */
	public void onDocumentUpdate(final DocumentParam updatedDocumentParam, final DBSession session);
	
	/**
	 * TODO document me
	 * 
	 * @param deletedInboxMessageParam 
	 * @param session 
	 */
	public void onInboxMailDelete(final InboxParam deletedInboxMessageParam, final DBSession session);
	
	/**
	 * called when a BibTexExtraURL will be deleted
	 * 
	 * @param deletedBibTexExtraParam
	 * @param session
	 * @author MarcelM
	 */
	public void onBibTexExtraDelete(final BibTexExtraParam deletedBibTexExtraParam, final DBSession session);


}