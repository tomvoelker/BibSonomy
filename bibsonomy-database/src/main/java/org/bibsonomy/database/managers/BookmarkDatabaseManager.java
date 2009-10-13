package org.bibsonomy.database.managers;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChain;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
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
//	private static final Log log = LogFactory.getLog(BookmarkDatabaseManager.class);

	private final static BookmarkDatabaseManager singleton = new BookmarkDatabaseManager();
	
	private static final BookmarkChain chain = new BookmarkChain();
	private static final HashID[] hashRange = { HashID.SIM_HASH0 };
	
	/**
	 * @return BookmarkDatabaseManager
	 */
	public static BookmarkDatabaseManager getInstance() {
		return singleton;
	}

	private BookmarkDatabaseManager() {
		super();
	}
	
	/**
	 * XXX: requestedGroupName only used in bibtex statment
	 * 
	 * <em>/viewable/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which are set viewable to group.
	 * 
	 * @param groupId
	 * @param userName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmarks
	 */
	public List<Post<Bookmark>> getBookmarkViewable(final int groupId, final String userName, final int limit, final int offset, final DBSession session) {
		if (GroupID.isSpecialGroupId(groupId)) {
			// show users own bookmarks, which are private, public or for friends
			return this.getPostsForUser(userName, userName, HashID.INTER_HASH, groupId, new LinkedList<Integer>(), limit, offset, session);
		}
		
		final BookmarkParam param = new BookmarkParam();
		param.setGroupId(groupId);
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		
		return this.postList("getBookmarkViewable", param, session);
	}
	
	// TODO: remove me
	@Override
	public List<Post<Bookmark>> getPostsForHomepage(int limit, int offset, DBSession session) {
		// FIXME: add filter!!!
//		if (FilterEntity.UNFILTERED.equals(param.getFilter())) {
//			return this.postList("get" + this.resourceClassName + "ForHomepageUnfiltered", param, session);
//		}
		return super.getPostsForHomepage(limit, offset, session);
	}
	
	
	@Override
	public List<Post<Bookmark>> getPostsFromBasketForUser(String loginUser, int limit, int offset, DBSession session) {
		throw new UnsupportedOperationException("not available for bookmarks");
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
	protected BookmarkParam getInsertParam(final Post<Bookmark> post, final DBSession session) {
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