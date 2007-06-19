package org.bibsonomy.rest.strategy.posts;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetListOfPostsStrategy extends AbstractListOfPostsStrategy {
	private final String nextLinkPrefix;

	public GetListOfPostsStrategy(final Context context) {
		super(context);
		this.nextLinkPrefix = RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getPostsUrl();
	}

	protected List<? extends Post<? extends Resource>> getList(String authUserName, Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingValue, List<String> tags, String hash, Object object, int start, int end) {
		return this.context.getLogic().getPosts(this.context.getAuthUserName(), resourceType, grouping, groupingValue, this.context.getTags("tags"), hash, null, start, end);
	}

	protected StringBuilder getLinkPrefix() {
		return new StringBuilder(this.nextLinkPrefix);
	}
}