package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChain;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;


/**
 * Used to CRUD bookmarks from the database.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @author Daniel Zoller
 * @version $Id$
 */
public class BookmarkDatabaseManager extends PostDatabaseManager<Bookmark, BookmarkParam> {
	private static final BookmarkDatabaseManager singleton = new BookmarkDatabaseManager();
	
	private static final BookmarkChain chain = new BookmarkChain();
	private static final HashID[] hashRange = { HashID.SIM_HASH0 };
	
	/**
	 * @return BookmarkDatabaseManager
	 */
	public static BookmarkDatabaseManager getInstance() {
		return singleton;
	}

	private BookmarkDatabaseManager() {
	}


	@Override
	protected List<Post<Bookmark>> getPostsForHomepage(BookmarkParam param, DBSession session) {
		final FilterEntity filter = param.getFilter();
		
		if (FilterEntity.UNFILTERED.equals(filter)) {
			return this.postList("getBookmarkForHomepageUnfiltered", param, session);
		}
		
		return super.getPostsForHomepage(param, session);
	}
	
	@Override
	public List<Post<Bookmark>> getPostsFromBasketForUser(String loginUser, int limit, int offset, DBSession session) {
		throw new UnsupportedOperationException("not available for bookmarks");
	}
	
	@Override
	protected void checkPost(Post<Bookmark> post, DBSession session) {
		// nop
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#onPostDelete(java.lang.Integer, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void onPostDelete(final Integer contentId, final DBSession session) {
		this.plugins.onBookmarkDelete(contentId, session);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#onPostUpdate(java.lang.Integer, java.lang.Integer, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void onPostUpdate(Integer oldContentId, Integer newContentId, DBSession session) {
		this.plugins.onBookmarkUpdate(oldContentId, newContentId, session);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getChain()
	 */
	@Override
	protected FirstChainElement<Post<Bookmark>, BookmarkParam> getChain() {
		return chain;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getHashRange()
	 */
	@Override
	protected HashID[] getHashRange() {
		return hashRange;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getInsertParam(org.bibsonomy.model.Post, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected BookmarkParam getInsertParam(final Post<? extends Bookmark> post, final DBSession session) {
		final BookmarkParam insert = this.getNewParam();
		
		insert.setResource(post.getResource());
		insert.setDate(post.getDate());
		insert.setRequestedContentId(post.getContentId());
		insert.setHash(post.getResource().getIntraHash());
		insert.setDescription(post.getDescription());
		insert.setUserName(post.getUser().getName());
		insert.setUrl(post.getResource().getUrl());	

		// in field group in table bookmark, insert the id for PUBLIC, PRIVATE or the id of the FIRST group in list
		final int groupId = post.getGroups().iterator().next().getGroupId();
		insert.setGroupId(groupId);
		
		return insert;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getNewParam()
	 */
	@Override
	protected BookmarkParam getNewParam() {
		return new BookmarkParam();
	}
}