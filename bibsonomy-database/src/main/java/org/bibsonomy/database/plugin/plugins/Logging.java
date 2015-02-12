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
package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.params.BibTexExtraParam;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.DocumentParam;
import org.bibsonomy.database.params.GoldStandardReferenceParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.InboxParam;
import org.bibsonomy.database.params.LoggingParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.enums.GoldStandardRelation;

/**
 * This plugin implements logging: on several occasions it'll save the old state
 * of objects (bookmarks, publications, etc.) into special tables in the
 * database. This way it is possible to track the changes made by users.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @author Stefan Stützer
 * @author Anton Wilhelm
 * @author Daniel Zoller
 * 
 */
public class Logging extends AbstractDatabasePlugin {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bibsonomy.database.plugin.AbstractDatabasePlugin#onCommentUpdate(
     * java.lang.String, org.bibsonomy.model.Comment,
     * org.bibsonomy.model.Comment, org.bibsonomy.database.common.DBSession)
     */
    @Override
    public void onDiscussionUpdate(final String interHash, final DiscussionItem item, final DiscussionItem oldItem, final DBSession session) {
        this.insert("logDiscussionItem", oldItem.getId(), session);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bibsonomy.database.plugin.AbstractDatabasePlugin#onCommentDelete(
     * java.lang.String, org.bibsonomy.model.Comment,
     * org.bibsonomy.database.common.DBSession)
     */
    @Override
    public void onDiscussionItemDelete(final String interHash, final DiscussionItem deletedItem, final DBSession session) {
        this.insert("logDiscussionItem", deletedItem.getId(), session);
    }

    @Override
    public void onPublicationDelete(final int contentId, final DBSession session) {
        final BibTexParam param = new BibTexParam();
        param.setRequestedContentId(contentId);
        this.insert("logBibTex", param, session);

        // logging of BibTexExtraURLs
        this.insert("logBibTexURLs", param, session);
    }

    @Override
    public void onPublicationUpdate(final int newContentId, final int contentId, final DBSession session) {
        final BibTexParam param = new BibTexParam();
        param.setRequestedContentId(contentId);
        param.setNewContentId(newContentId);
        this.insert("logBibTex", param, session);
        
        // Update current_content_id for history
     	this.update("updateBibTexHistory", param, session);
    }
    
    @Override
    public void onPublicationMassUpdate(String userName, int groupId, DBSession session) {
    	final BibTexParam param = new BibTexParam();
    	param.setGroupId(groupId);
    	param.setRequestedUserName(userName);
    	this.insert("logPublicationMassUpdate", param, session);
    }

    @Override
    public void onGoldStandardUpdate(final int contentId, final int newContentId, final String newInterhash, final String interhash, final DBSession session) {
        final LoggingParam<String> logParam = new LoggingParam<String>();
        logParam.setNewId(newInterhash);
		logParam.setOldId(interhash);
		logParam.setNewContentId(newContentId);
		logParam.setContentId(contentId);
		this.insert("logGoldStandard", logParam, session);

		// Update current_content_id for history
		this.update("updateGoldStandardHistory", logParam, session);
    }

    @Override
    public void onGoldStandardDelete(final String interhash, final DBSession session) {
        final LoggingParam<String> logParam = new LoggingParam<String>();
        logParam.setOldId(interhash);
        /*
         * FIXME: Should we not use newId 0?
         */
        logParam.setNewId("");
        this.insert("logGoldStandard", logParam, session);
    }

    @Override
    public void onGoldStandardRelationDelete(final String userName, final String interHashPublication, final String interHashReference, final GoldStandardRelation relation, final DBSession session) {
        final GoldStandardReferenceParam param = new GoldStandardReferenceParam();
        param.setHash(interHashPublication);
        param.setRefHash(interHashReference);
        param.setUsername(userName);
        param.setRelation(relation);

        this.insert("logGoldStandardRelationDelete", param, session);
    }

    @Override
    public void onBookmarkDelete(final int contentId, final DBSession session) {
        final BookmarkParam param = new BookmarkParam();
        param.setRequestedContentId(contentId);
        this.insert("logBookmark", param, session);
    }

    @Override
    public void onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session) {
        final BookmarkParam param = new BookmarkParam();
        param.setNewContentId(newContentId);
        param.setRequestedContentId(contentId);
        this.insert("logBookmark", param, session);

        // Update current_content_id for history
     	this.update("updateBookmarkHistory", param, session);
    }
    
    /* (non-Javadoc)
     * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onBookmarkMassUpdate(java.lang.String, int)
     */
    @Override
    public void onBookmarkMassUpdate(String userName, int groupId, DBSession session) {
    	final BookmarkParam param = new BookmarkParam();
    	param.setGroupId(groupId);
    	param.setRequestedUserName(userName);
    	this.insert("logBookmarkMassUpdate", param, session);
    }
	
	@Override
	public void onDiscussionMassUpdate(String userName, int groupId, DBSession session) {
		final DiscussionItemParam<DiscussionItem> param = new DiscussionItemParam<>();
		param.setUserName(userName);
		param.setGroupId(groupId);
		this.insert("logDiscussionMassUpdate", param, session);
	}

    @Override
    public void onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
        final TagRelationParam trp = new TagRelationParam();
        trp.setOwnerUserName(userName);
        trp.setLowerTagName(lowerTagName);
        trp.setUpperTagName(upperTagName);
        this.insert("logTagRelation", trp, session);
    }

    @Override
    public void onConceptDelete(final String conceptName, final String userName, final DBSession session) {
        final TagRelationParam trp = new TagRelationParam();
        trp.setOwnerUserName(userName);
        trp.setUpperTagName(conceptName);
        this.insert("logConcept", trp, session);
    }

    @Override
    public void onTagDelete(final int contentId, final DBSession session) {
        final TagParam param = new TagParam();
        param.setRequestedContentId(contentId);
        this.insert("logTasDelete", param, session);
    }

    @Override
    public void onChangeUserMembershipInGroup(final String userName, final int groupId, final DBSession session) {
        final GroupParam groupParam = new GroupParam();
        groupParam.setGroupId(groupId);
        groupParam.setUserName(userName);
        this.insert("logChangeUserMembershipInGroup", groupParam, session);
    }

    @Override
    public void onUserUpdate(final String userName, final DBSession session) {
        this.insert("logUser", userName, session);
    }

    @Override
    public void onDeleteFellowship(final UserParam param, final DBSession session) {
        this.insert("logFollowerDelete", param, session);
    }

    @Override
    public void onDeleteFriendship(final UserParam param, final DBSession session) {
        this.insert("logFriendDelete", param, session);
    }

    @Override
    public void onDeleteBasketItem(final BasketParam param, final DBSession session) {
        this.insert("logBasketItemDelete", param, session);
    }

    @Override
    public void onDeleteAllBasketItems(final String userName, final DBSession session) {
        this.insert("logDeleteAllFromBasket", userName, session);
    }

    @Override
    public void onDocumentDelete(final DocumentParam deletedDocumentParam, final DBSession session) {
        this.insert("logDocument", deletedDocumentParam, session);
    }

    @Override
    public void onDocumentUpdate(final DocumentParam updatedDocumentParam, final DBSession session) {
        this.insert("logDocument", updatedDocumentParam, session);
    }

    @Override
    public void onInboxMailDelete(final InboxParam deletedInboxMessageParam, final DBSession session) {
        this.insert("logInboxMessages", deletedInboxMessageParam, session);
    }

    @Override
    public void onBibTexExtraDelete(final BibTexExtraParam deletedBibTexExtraParam, final DBSession session) {
        this.insert("logBibTexURL", deletedBibTexExtraParam, session);
    }
}