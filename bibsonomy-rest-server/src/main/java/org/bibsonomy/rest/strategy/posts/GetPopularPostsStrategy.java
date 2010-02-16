package org.bibsonomy.rest.strategy.posts;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetPopularPostsStrategy extends AbstractListOfPostsStrategy {
	private final String nextLinkPrefix;
	
	/**
	 * @param context
	 */
	public GetPopularPostsStrategy(final Context context) {
		super(context);
		this.nextLinkPrefix = RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getPostsUrl() + "/" + RestProperties.getInstance().getPopularPostsUrl();
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder(this.nextLinkPrefix);
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		return this.getLogic().getPosts(resourceType, grouping, groupingValue, this.tags, null, Order.POPULAR, null,  this.getView().getStartValue(), this.getView().getEndValue(), search);
	}
}