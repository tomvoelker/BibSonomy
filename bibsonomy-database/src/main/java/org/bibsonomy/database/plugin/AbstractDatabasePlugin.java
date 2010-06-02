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

	public Runnable onBibTexInsert(final int contentId, final DBSession session) {
		return null;
	}

	public Runnable onBibTexDelete(final int contentId, final DBSession session) {
		return null;
	}

	public Runnable onBibTexUpdate(final int newContentId, final int contentId, final DBSession session) {
		return null;
	}

	@Override
	public Runnable onGoldStandardPublicationCreate(String interhash, DBSession session) {
		return null;
	}

	@Override
	public Runnable onGoldStandardPublicationDelete(String interhash, DBSession session) {
		return null;
	}

	@Override
	public Runnable onGoldStandardPublicationUpdate(String newInterhash, String interhash, DBSession session) {
		return null;
	}
	
	@Override
	public Runnable onGoldStandardPublicationReferenceCreate(String userName, String interHashPublication, String interHashReference) {
		return null;
	}

	@Override
	public Runnable onGoldStandardPublicationReferenceDelete(String userName, String interHashPublication, String interHashReference, DBSession session) {
		return null;
	}

	public Runnable onBookmarkInsert(final int contentId, final DBSession session) {
		return null;
	}

	public Runnable onBookmarkDelete(final int contentId, final DBSession session) {
		return null;
	}

	public Runnable onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session) {
		return null;
	}

	public Runnable onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		return null;
	}

	public Runnable onConceptDelete(String conceptName, String userName, DBSession session) {
		return null;
	}
	
	public Runnable onTagDelete(final int contentId, final DBSession session) {
		return null;
	}

	public Runnable onRemoveUserFromGroup(final String userName, final int groupId, final DBSession session) {
		return null;
	}

	public Runnable onUserDelete(String userName, DBSession session) {
		return null;
	}

	public Runnable onUserInsert(String userName, DBSession session) {
		return null;
	}

	public Runnable onUserUpdate(String userName, DBSession session) {
		return null;
	}

	public Runnable onDeleteFellowship(UserParam param, DBSession session) {
		return null;
	}

	public Runnable onDeleteFriendship(UserParam param, DBSession session) {
		return null;
	}

	public Runnable onDeleteBasketItem(BasketParam param, DBSession session) {
		return null;
	}
	
	public Runnable onDeleteAllBasketItems(String userName, DBSession session) {
		return null;
	}
}