package org.bibsonomy.rest.strategy.posts;

import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.BookmarkUtils;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.Context;

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

    @SuppressWarnings("unchecked")
    @Override
    protected List<? extends Post<? extends Resource>> getList() {
        // TODO: why not sort in DBLogic? (Maybe refactoring LogicInterface with a smarter parameter object to keep parameter lists and sorting clear)
        if ((resourceType != null) && BibTex.class.isAssignableFrom(resourceType)) {
            List<? extends Post<? extends BibTex>> bibtexList = getList((Class<? extends BibTex>) resourceType);
            BibTexUtils.sortBibTexList(bibtexList, sortKeys, sortOrders);
            return bibtexList;
        } else if ((resourceType != null) && Bookmark.class.isAssignableFrom(resourceType)) {
            List<? extends Post<? extends Bookmark>> bookmarkList = getList((Class<? extends Bookmark>) resourceType);
            BookmarkUtils.sortBookmarkList(bookmarkList, sortKeys, sortOrders);
            return bookmarkList;
        }
        
        // return other resource types without ordering
        return getList(resourceType);
    }
    
    protected <T extends Resource> List<Post<T>> getList(Class<T> _resourceType) {
        return this.getLogic().getPosts(_resourceType, grouping, groupingValue, this.tags, hash, search, null, order, null, null, getView().getStartValue(), getView().getEndValue());
    }
}