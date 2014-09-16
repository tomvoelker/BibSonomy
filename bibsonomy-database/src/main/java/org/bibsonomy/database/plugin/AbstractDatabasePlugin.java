package org.bibsonomy.database.plugin;

import org.bibsonomy.database.common.AbstractDatabaseManager;
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
	public void onPublicationInsert(final Post<? extends Resource> post, final DBSession session) {
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
	public void onRemoveUserFromGroup(final String userName, final int groupId, final DBSession session) {
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
	public void onDeleteBasketItem(final BasketParam param, final DBSession session) {
		// noop
	}

	@Override
	public void onDeleteAllBasketItems(final String userName, final DBSession session) {
		// noop
	}

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
	
}