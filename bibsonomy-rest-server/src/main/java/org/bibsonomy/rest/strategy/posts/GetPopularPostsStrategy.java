package org.bibsonomy.rest.strategy.posts;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.Order;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetPopularPostsStrategy extends AbstractListOfPostsStrategy {
	private final String nextLinkPrefix;
	
	public GetPopularPostsStrategy(final Context context) {
		super(context);
		this.nextLinkPrefix = RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getPostsUrl() + "/" + RestProperties.getInstance().getPopularPostsUrl();
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder(this.nextLinkPrefix);
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingValue, List<String> tags, String hash, Object object, int start, int end) {
		return this.context.getLogic().getPosts(resourceType, grouping, groupingValue, this.context.getTags("tags"), null, Order.POPULAR, start, end);
	}

	/*@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		// setup viewModel
		final int start = this.context.getIntAttribute("start", 0);
		int end = this.context.getIntAttribute("end", 20);

		final Class<? extends Resource> resourceType = Resource.getResource(this.context.getStringAttribute("resourcetype", "all"));

		final GroupingEntity grouping = chooseGroupingEntity();
		final String groupingValue;
		if (grouping != GroupingEntity.ALL) {
			groupingValue = this.context.getStringAttribute(grouping.toString().toLowerCase(), null);
		} else {
			groupingValue = null;
		}

		final List<? extends Post<? extends Resource>> posts = this.context.getLogic().getPosts(this.context.getAuthUserName(), resourceType, grouping, groupingValue, this.context.getTags("tags"), null, Order.POPULAR, start, end);
		final ViewModel viewModel = new ViewModel();
		if (posts.size() < end || posts.size() > end) {
			end = posts.size();
		} else {
			String next = RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getPostsUrl() + "/" + RestProperties.getInstance().getPopularPostsUrl() + "?start=" + String.valueOf(end) + "&end=" + String.valueOf(end + 20);
			if (resourceType != Resource.class) {
				next += "&resourcetype=" + Resource.toString(resourceType).toLowerCase();
			}
			if (grouping != GroupingEntity.ALL && groupingValue != null) {
				next += "&" + grouping.toString().toLowerCase() + "=" + groupingValue;
			}
			viewModel.setUrlToNextResources(next);
		}
		viewModel.setStartValue(start);
		viewModel.setEndValue(end);

		// delegate to the renderer
		this.context.getRenderer().serializePosts(writer, posts, viewModel);
	}*/
}