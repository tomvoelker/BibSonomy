package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GoldStandardReferenceParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.LoggingParam;
import org.bibsonomy.database.params.ReviewParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.Review;

/**
 * This plugin implements logging: on several occasions it'll save the old state
 * of objects (bookmarks, publications, etc.) into special tables in the
 * database. This way it is possible to track the changes made by users.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @author Stefan Stützer
 * @author Anton Wilhelm
 * @version $Id$
 */
public class Logging extends AbstractDatabasePlugin {

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onReviewUpdated(java.lang.String, org.bibsonomy.model.Review, org.bibsonomy.model.Review, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public Runnable onReviewUpdated(final String interHash, final Review oldReview, final Review review, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final ReviewParam param = new ReviewParam();
				param.setReview(oldReview);
				param.setInterHash(interHash);
				insert("logReview", param, session);
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onReviewDeleted(java.lang.String, org.bibsonomy.model.Review, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public Runnable onReviewDeleted(final String interHash, final Review oldReview, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final ReviewParam param = new ReviewParam();
				param.setInterHash(interHash);
				param.setReview(oldReview);
				insert("logReview", param, session);
				insert("logReviewHelpfulMarks", param, session);
			}
		};
	}

	@Override
	public Runnable onPublicationDelete(final int contentId, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final BibTexParam param = new BibTexParam();
				param.setRequestedContentId(contentId);
				insert("logBibTex", param, session);
			}
		};
	}

	@Override
	public Runnable onPublicationUpdate(final int newContentId, final int contentId, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final BibTexParam param = new BibTexParam();
				param.setRequestedContentId(contentId);
				insert("logBibTex", param, session);
				param.setNewContentId(newContentId);
				insert("logBibTexUpdate", param, session);
			}
		};
	}
	
	@Override
	public Runnable onGoldStandardPublicationUpdate(final String newInterhash, final String interhash, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final LoggingParam<String> logParam = new LoggingParam<String>();
				logParam.setNewId(newInterhash);
				logParam.setOldId(interhash);
				
				insert("logGoldStandardPublication", logParam, session);
				update("logGoldStandardPublicationUpdate", logParam, session);
			}
		};
	}
	
	@Override
	public Runnable onGoldStandardPublicationDelete(final String interhash, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final LoggingParam<String> logParam = new LoggingParam<String>();
				logParam.setOldId(interhash);
				insert("logGoldStandardPublication", logParam, session);
			}
		};
	}
	
	@Override
	public Runnable onGoldStandardPublicationReferenceDelete(final String userName, final String interHashPublication, final String interHashReference, final DBSession session) {
		return new Runnable() {
			@Override
			public void run() {
				final GoldStandardReferenceParam param = new GoldStandardReferenceParam();
				param.setHash(interHashPublication);
				param.setRefHash(interHashReference);
				param.setUsername(userName);
				
				insert("logGoldStandardPublicationReferenceDelete", param, session);
			}
		};
	}

	@Override
	public Runnable onBookmarkDelete(final int contentId, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final BookmarkParam param = new BookmarkParam();
				param.setRequestedContentId(contentId);
				insert("logBookmark", param, session);
			}
		};
	}

	@Override
	public Runnable onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final BookmarkParam param = new BookmarkParam();
				param.setRequestedContentId(contentId);
				insert("logBookmark", param, session);
				param.setNewContentId(newContentId);
				insert("logBookmarkUpdate", param, session);
			}
		};
	}

	@Override
	public Runnable onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final TagRelationParam trp = new TagRelationParam();
				trp.setOwnerUserName(userName);
				trp.setLowerTagName(lowerTagName);
				trp.setUpperTagName(upperTagName);
				insert("logTagRelation", trp, session);
			}
		};
	}

	@Override
	public Runnable onConceptDelete(final String conceptName, final String userName, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final TagRelationParam trp = new TagRelationParam();
				trp.setOwnerUserName(userName);
				trp.setUpperTagName(conceptName);
				insert("logConcept", trp, session);
			}
		};
	}

	@Override
	public Runnable onTagDelete(final int contentId, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final TagParam param = new TagParam();
				param.setRequestedContentId(contentId);
				insert("logTasDelete", param, session);
			}
		};
	}

	@Override
	public Runnable onRemoveUserFromGroup(final String userName, final int groupId, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final GroupParam groupParam = new GroupParam();
				groupParam.setGroupId(groupId);
				groupParam.setUserName(userName);
				insert("logRemoveUserFromGroup", groupParam, session);

			}
		};
	}

	@Override
	public Runnable onUserUpdate(final String userName, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				insert("logUser", userName, session);
			}
		};
	}
	
	@Override
	public Runnable onDeleteFellowship(final UserParam param, final DBSession session){
		return new Runnable(){
			
			@Override
			public void run(){
				insert("logFollowerDelete", param, session);
			}
		};
	}
	
	@Override
	public Runnable onDeleteFriendship(final UserParam param, final DBSession session){
		return new Runnable(){
			
			@Override
			public void run(){
				insert("logFriendDelete", param, session);
			}
		};
	}
	
	@Override
	public Runnable onDeleteBasketItem(final BasketParam param, final DBSession session){
		return new Runnable(){
			
			@Override
			public void run(){
				insert("logBasketItemDelete", param, session);
			}
		};
	}
	
	@Override
	public Runnable onDeleteAllBasketItems(final String userName, final DBSession session){
		return new Runnable(){
			
			@Override
			public void run(){
				insert("logDeleteAllFromBasket", userName, session);
			}
		};
	}
}