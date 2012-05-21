package org.bibsonomy.database.managers;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;

/**
 * TODO: implement chain
 * 
 * @author dzo
 * @version $Id$
 */
public class GoldStandardBookmarkDatabaseManager extends GoldStandardDatabaseManager<Bookmark, GoldStandardBookmark, BookmarkParam> {

	private static final GoldStandardBookmarkDatabaseManager INSTANCE = new GoldStandardBookmarkDatabaseManager();

	/**
	 * @return the @{link:CommunityPostBookmarkDatabaseManager} instance
	 */
	public static GoldStandardBookmarkDatabaseManager getInstance() {
		return INSTANCE;
	}

	private GoldStandardBookmarkDatabaseManager() {
		// noop
	}

	@Override
	protected void onGoldStandardReferenceDelete(final String userName, final String interHash, final String interHashRef, final DBSession session) {
		// TODO: implement reference model for bookmarks
	}

	@Override
	protected BookmarkParam createNewParam() {
		return new BookmarkParam();
	}

}
