package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.errors.DuplicatePostErrorMessage;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.GoldStandardRelation;

/**
 * Used to create, read, update and delete gold standard publications from the database.
 * 
 * @author dzo
 */
public final class GoldStandardPublicationDatabaseManager extends GoldStandardDatabaseManager<BibTex, GoldStandardPublication, BibTexParam> {
	private static final GoldStandardPublicationDatabaseManager INSTANCE = new GoldStandardPublicationDatabaseManager();
	
	/**
	 * @return the @{link:GoldStandardPublicationDatabaseManager} instance
	 */
	public static GoldStandardPublicationDatabaseManager getInstance() {
		return INSTANCE;
	}
	
	private GoldStandardPublicationDatabaseManager() {}
	
	@Override
	public Post<GoldStandardPublication> getPostDetails(final String loginUserName, final String resourceHash, final String userName, final List<Integer> visibleGroupIDs, final DBSession session) {
		final Post<GoldStandardPublication> post = super.getPostDetails(loginUserName, resourceHash, userName, visibleGroupIDs, session);
		
		if (present(post)) {
			/*
			 * before the resource leaves the logic parse the misc field
			 */
			post.getResource().parseMiscField();
		}
		
		return post;
	}
	
	@Override
	protected void onGoldStandardRelationDelete(final String userName, final String interHash, final String interHashRef, final GoldStandardRelation interHashRelation, final DBSession session) {
		this.plugins.onGoldStandardRelationDelete(userName, interHash, interHashRef, interHashRelation, session);
	}

	@Override
	protected BibTexParam createNewParam() {
		return new BibTexParam();
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
				//log.warn("Added DuplicatePostErrorMessage for post " + post.getResource().getIntraHash());
				session.commitTransaction();
				return;
			}
		

		return;
	}
}