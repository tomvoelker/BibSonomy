package org.bibsonomy.rest.strategy.posts;

import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.database.Order;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetNewPostsStrategy extends Strategy {

	public GetNewPostsStrategy(final Context context) {
		super(context);
	}

	@Override
	public void validate() throws ValidationException {
		// should be ok for everybody
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		// setup viewModel
		final int start = this.context.getIntAttribute("start", 0);
		int end = this.context.getIntAttribute("end", 20);

		final Class<? extends Resource> resourceType = Resource.getResourceType(this.context.getStringAttribute("resourcetype", "all"));

		final GroupingEntity grouping = chooseGroupingEntity();
		final String groupingValue;
		if (grouping != GroupingEntity.ALL) {
			groupingValue = this.context.getStringAttribute(grouping.toString().toLowerCase(), null);
		} else {
			groupingValue = null;
		}

		final List<? extends Post<? extends Resource>> posts = this.context.getLogic().getPosts(this.context.getAuthUserName(), resourceType, grouping, groupingValue, this.context.getTags("tags"), null, Order.ADDED, start, end);

		final ViewModel viewModel = new ViewModel();
		if (posts.size() < end || posts.size() > end) {
			end = posts.size();
		} else {
			String next = RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getPostsUrl() + "/" + RestProperties.getInstance().getAddedPostsUrl() + "?start=" + String.valueOf(end) + "&end=" + String.valueOf(end + 20);
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
	}

	@Override
	public String getContentType(final String userAgent) {
		if (this.context.apiIsUserAgent(userAgent)) return "bibsonomy/posts+" + this.context.getRenderingFormat().toString();
		return RestProperties.getInstance().getContentType();
	}
}