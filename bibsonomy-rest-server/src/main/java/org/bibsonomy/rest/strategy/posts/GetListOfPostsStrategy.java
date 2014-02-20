package org.bibsonomy.rest.strategy.posts;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.BookmarkUtils;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.SortUtils;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class GetListOfPostsStrategy extends AbstractListOfPostsStrategy {
	private final String nextLinkPrefix;

	/**
	 * @param context
	 */
	public GetListOfPostsStrategy(final Context context) {
		super(context);
		this.nextLinkPrefix = this.getUrlRenderer().getApiUrl() + RESTConfig.POSTS_URL;
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder(this.nextLinkPrefix);
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		List<SortKey> sortKeyList = SortUtils.parseSortKeys(sortKeys);
		List<SortOrder> sortOrderList = SortUtils.parseSortOrders(sortOrders);
        
		if (BibTex.class == resourceType) {
			List<Post<BibTex>> bibtexList = getList( BibTex.class );
			BibTexUtils.sortBibTexList( bibtexList, sortKeyList, sortOrderList );
			return bibtexList;
		} else if ( Bookmark.class == resourceType ) {
			List<Post<Bookmark>> bookmarkList = getList( Bookmark.class );
			BookmarkUtils.sortBookmarkList( bookmarkList, sortKeyList, sortOrderList );
			return bookmarkList;
		}
		
		return getList(resourceType);
	}
	
	protected <T extends Resource> List<Post<T>> getList ( Class<T> _resourceType )
	{
		List<Post<T>> postList = this.getLogic().getPosts(_resourceType, grouping, groupingValue, this.tags, hash, search, null, order, null, null, getView().getStartValue(), getView().getEndValue());
		
		return postList;
	}
}