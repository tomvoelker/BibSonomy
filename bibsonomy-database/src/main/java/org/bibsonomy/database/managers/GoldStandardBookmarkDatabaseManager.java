package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;

import org.bibsonomy.common.errors.DuplicatePostErrorMessage;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.GoldStandardRelation;

/**
 * TODO: implement chain
 * 
 * @author dzo
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
	protected void onGoldStandardRelationDelete(final String userName, final String interHash, final String interHashRef,final GoldStandardRelation interHashRelation, final DBSession session) {
		// TODO: implement reference model for bookmarks
	}

	@Override
	protected BookmarkParam createNewParam() {
		return new BookmarkParam();
	}
}
