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
	@Override
	public void isPostDuplicate(final Post<?> post, final DBSession session) {
		session.beginTransaction();
			final String userName = post.getUser().getName();
		/*
		* the current intra hash of the resource
		*/
			final String intraHash = post.getResource().getIntraHash();
		/*
		* get posts with the intrahash of the given post to check for possible duplicates 
		 */
			Post<?> postInDB = null;
			try {
				postInDB = this.getPostDetails(userName, intraHash, userName, new ArrayList<Integer>(), session);
			} catch(final ResourceMovedException ex) {
			/*
						 * getPostDetails() throws a ResourceMovedException for hashes for which
					 * no actual post exists, but an old post has existed with that hash.
						 * 
						 * Since we are not interested in former posts with that hash we ignore
						 * this exception silently. 
						 */
			}
			/*
			 * check if user is trying to create a resource that already exists
			 */
			if (present(postInDB)) {
				final ErrorMessage errorMessage = new DuplicatePostErrorMessage(this.resourceClassName, post.getResource().getIntraHash());
				session.addError(post.getResource().getIntraHash(), errorMessage);
	//			log.warn("Added DuplicatePostErrorMessage for post " + post.getResource().getIntraHash());
				session.commitTransaction();
				return;
			}
		

		return;
	}
}
