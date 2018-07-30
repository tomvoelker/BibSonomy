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
package org.bibsonomy.database.plugin;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.ClipboardParam;
import org.bibsonomy.database.params.BibTexExtraParam;
import org.bibsonomy.database.params.DocumentParam;
import org.bibsonomy.database.params.InboxParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.GoldStandardRelation;

/**
 * This class should be used by plugins. This way they don't have to implement
 * all methods from the interface DatabasePlugin. Furthermore they have access
 * to some basic database methods.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @author Anton Wilhelm
 * @author Stefan Stützer
 */
public class AbstractDatabasePlugin extends AbstractDatabaseManager implements DatabasePlugin {

	@Override
	public void onPublicationInsert(final Post<? extends BibTex> post, final DBSession session) {
		// noop
	}

	@Override
	public void onPublicationDelete(final int contentId, final DBSession session) {
		// noop
	}

	@Override
	public void onPublicationUpdate(final int newContentId, final int contentId, final DBSession session) {
		// noop
	}
	
	/**
	 * @param username
	 * @param groupId
	 * @param session
	 */
	@Override
	public void onPublicationMassUpdate(String username, int groupId, DBSession session) {
		// noop
	}

	@Override
	public void onGoldStandardCreate(final String interhash, final DBSession session) {
		// noop
	}

	@Override
	public void onGoldStandardUpdate(final int newContentId, final int contentId, final String newInterhash, final String interhash, final DBSession session) {
		// noop
	}

	@Override
	public void onGoldStandardPublicationReferenceCreate(final String userName, final String interHash_publication, final String interHash_reference, final String interHash_relation) {
		// noop
	}

	@Override
	public void onGoldStandardRelationDelete(final String userName, final String interHash_publication, final String interHash_reference, final GoldStandardRelation relation, final DBSession session) {
		// noop
	}

	@Override
	public void onGoldStandardDelete(final String interhash, final DBSession session) {
		// noop
	}

	@Override
	public void onBookmarkInsert(final Post<? extends Resource> post, final DBSession session) {
		// noop
	}

	@Override
	public void onBookmarkDelete(final int contentId, final DBSession session) {
		// noop
	}

	@Override
	public void onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session) {
		// noop
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onBookmarkMassUpdate(java.lang.String, int)
	 */
	@Override
	public void onBookmarkMassUpdate(String userName, int groupId, DBSession session) {
		// noop
	}

	@Override
	public void onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		// noop
	}

	@Override
	public void onConceptDelete(final String conceptName, final String userName, final DBSession session) {
		// noop
	}

	@Override
	public void onTagDelete(final int contentId, final DBSession session) {
		// noop
	}

	@Override
	public void onUserInsert(final String userName, final DBSession session) {
		// noop
	}

	@Override
	public void onUserDelete(final String userName, final DBSession session) {
		// noop
	}

	@Override
	public void onUserUpdate(final String userName, final DBSession session) {
		// noop
	}

	@Override
	public void onChangeUserMembershipInGroup(final String userName, final int groupId, final DBSession session) {
		// noop
	}

	@Override
	public void onDeleteFellowship(final UserParam param, final DBSession session) {
		// noop
	}

	@Override
	public void onDeleteFriendship(final UserParam param, final DBSession session) {
		// noop
	}

	@Override
	public void onDeleteClipboardItem(ClipboardParam param, DBSession session) {
		// noop
	};

	@Override
	public void onDeleteAllClipboardItems(String userName, DBSession session) {
		// noop
	};

	@Override
	public void onDiscussionUpdate(final String interHash, final DiscussionItem comment, final DiscussionItem oldComment, final DBSession session) {
		// noop
	}

	@Override
	public void onDiscussionItemDelete(final String interHash, final DiscussionItem deletedComment, final DBSession session) {
		// noop
	}

	@Override
	public void onDocumentDelete(final DocumentParam deletedDocumentParam, final DBSession session) {
		// noop
	}

	@Override
	public void onInboxMailDelete(final InboxParam deletedInboxMessageParam, final DBSession session) {
		// noop
	}

	@Override
	public void onBibTexExtraDelete(final BibTexExtraParam deletedBibTexExtraParam, final DBSession session) {
		// noop
	}

	@Override
	public void onDocumentUpdate(DocumentParam updatedDocumentParam, DBSession session) {
		// noop
	}

	@Override
	public void onDiscussionMassUpdate(String username, int groupId, DBSession session) {
		// poon
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonDelete(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonNameDelete(PersonName personName, DBSession session) {
		// noop
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonUpdate(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonUpdate(String personId, DBSession session) {
		// noop
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonDelete(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonDelete(Person person, DBSession session) {
		// noop
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPubPersonDelete(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPubPersonDelete(ResourcePersonRelation rel, DBSession session) {
		// noop
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonUpdateByUserName(java.lang.String, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonUpdateByUserName(String userName, DBSession session) {
		// noop		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onUpdatePersonName(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonNameUpdate(Integer personChangeId, DBSession session) {
		// noop
	}

	@Override
	public void onProjectInsert(Project project, DBSession session) {
		// noop
	}
}