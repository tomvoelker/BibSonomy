package org.bibsonomy.community.database;

import java.util.Collection;

import org.bibsonomy.community.database.param.CommunityResourceParam;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.model.Bookmark;

/**
 * class for accessing bookmark posts 
 * @author fei
 *
 */
public class BookmarkPostManager extends AbstractPostManager<Bookmark> {
	
	/** singleton pattern's instance reference */
	protected static BookmarkPostManager instance = null;
	
	/** disabled constructor */
	private BookmarkPostManager() {}

	/**
	 * @return An instance of this implementation of 
	 */
	public static BookmarkPostManager getInstance() {
		if (instance == null) instance = new BookmarkPostManager();
		return instance;
	}
	
	@Override
	protected Collection<Post<Bookmark>> getPostsForCommunityInternal(CommunityResourceParam<Bookmark> param) {
		Collection<Post<Bookmark>> resources;
		switch( param.getOrdering() ) {
		case ADDED:
			resources = queryForList("getNewestBookmarksForCommunity", param);
			break;
		default:
			resources = queryForList("getBookmarksForCommunity", param);
		};
		
		return resources;
	}

	@Override
	protected CommunityResourceParam<Bookmark> getResourceParam() {
		return new CommunityResourceParam<Bookmark>();
	}
}
