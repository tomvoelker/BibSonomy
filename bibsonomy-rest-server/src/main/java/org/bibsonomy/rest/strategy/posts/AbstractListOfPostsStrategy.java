package org.bibsonomy.rest.strategy.posts;

import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Jens Illig
 * @version $Id$
 */
public abstract class AbstractListOfPostsStrategy extends Strategy {

	public AbstractListOfPostsStrategy(final Context context) {
		super(context);
	}

	@Override
	public final void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		// setup viewModel
		final int start = this.context.getIntAttribute("start", 0);
		int end = this.context.getIntAttribute("end", 20);

		final Class<? extends Resource> resourceType = Resource.getResource(this.context.getStringAttribute("resourcetype", "all"));

		final String hash = this.context.getStringAttribute("resource", null);

		final GroupingEntity grouping = chooseGroupingEntity();
		final String groupingValue;
		if (grouping != GroupingEntity.ALL) {
			groupingValue = this.context.getStringAttribute(grouping.toString().toLowerCase(), null);
		} else {
			groupingValue = null;
		}

		final List<? extends Post<? extends Resource>> posts = getList(resourceType, grouping, groupingValue, this.context.getTags("tags"), hash, null, start, end);
		
		final ViewModel viewModel = new ViewModel();
		if (posts.size() != (end - start)) {
			end = posts.size() + start;
		} else {
			viewModel.setUrlToNextResources( buildNextLink(start, end, resourceType, hash, grouping, groupingValue) );
		}
		viewModel.setStartValue(start);
		viewModel.setEndValue(end);

		// delegate to the renderer
		this.context.getRenderer().serializePosts(writer, posts, viewModel);
	}

	protected abstract List<? extends Post<? extends Resource>> getList(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingValue, List<String> tags, String hash, Object object, int start, int end);

	private final String buildNextLink(final int start, final int end, final Class<? extends Resource> resourceType, final String hash, final GroupingEntity grouping, final String groupingValue) {
		final StringBuilder sb = getLinkPrefix();
		sb.append("?start=").append(end).append("&end=").append(end + end - start);
		if (resourceType != Resource.class) {
			sb.append("&resourcetype=").append(Resource.toString(resourceType).toLowerCase());
		}
		final String tags = this.context.getStringAttribute("tags", null);
		if (tags != null) {
			sb.append("&tags=").append(tags);
		}
		if (hash != null) {
			sb.append("&resource=").append(hash);
		}
		if (grouping != GroupingEntity.ALL && groupingValue != null) {
			sb.append('&').append(grouping.toString().toLowerCase()).append('=').append(groupingValue);
		}
		return sb.toString();
	}

	protected abstract StringBuilder getLinkPrefix();

	@Override
	public final String getContentType(final String userAgent) {
		if (this.context.apiIsUserAgent(userAgent)) return "bibsonomy/posts+" + this.context.getRenderingFormat().toString();
		return RestProperties.getInstance().getContentType();
	}
}