package org.bibsonomy.database.plugin;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.params.UserParam;

/**
 * This class should be used by plugins. This way they don't have to implement
 * all methods from the interface DatabasePlugin. Furthermore they have access
 * to some basic database methods.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @author Anton Wilhelm
 * @author Stefan Stützer
 * @version $Id$
 */
public class AbstractDatabasePlugin extends AbstractDatabaseManager implements DatabasePlugin {

	@Override
	public Runnable onBibTexInsert(final int contentId, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onBibTexDelete(final int contentId, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onBibTexUpdate(final int newContentId, final int contentId, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onGoldStandardPublicationCreate(final String interhash, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onGoldStandardPublicationDelete(final String interhash, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onGoldStandardPublicationUpdate(final String newInterhash, final String interhash, final DBSession session) {
		return null;
	}
	
	@Override
	public Runnable onGoldStandardPublicationReferenceCreate(final String userName, final String interHashPublication, final String interHashReference) {
		return null;
	}

	@Override
	public Runnable onGoldStandardPublicationReferenceDelete(final String userName, final String interHashPublication, final String interHashReference, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onBookmarkInsert(final int contentId, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onBookmarkDelete(final int contentId, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onConceptDelete(final String conceptName, final String userName, final DBSession session) {
		return null;
	}
	
	@Override
	public Runnable onTagDelete(final int contentId, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onRemoveUserFromGroup(final String userName, final int groupId, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onUserDelete(final String userName, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onUserInsert(final String userName, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onUserUpdate(final String userName, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onDeleteFellowship(final UserParam param, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onDeleteFriendship(final UserParam param, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onDeleteBasketItem(final BasketParam param, final DBSession session) {
		return null;
	}
	
	@Override
	public Runnable onDeleteAllBasketItems(final String userName, final DBSession session) {
		return null;
	}
}