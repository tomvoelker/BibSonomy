package org.bibsonomy.rest.strategy.users;

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
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserPostsStrategy extends Strategy {

	private final String userName;

	public GetUserPostsStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		final int start = this.context.getIntAttribute("start", 0);
		int end = this.context.getIntAttribute("end", 19);

		final Class<? extends Resource> resourceType = Resource.getResource(this.context.getStringAttribute("resourcetype", "all"));
		final List<? extends Post<? extends Resource>> posts = this.context.getLogic().getPosts(resourceType, GroupingEntity.USER, userName, this.context.getTags("tags"), null, null, start, end);

		// setup viewModel
		final ViewModel viewModel = new ViewModel();
		if (posts.size() < end + 1) {
			end = posts.size() - 1;
		} else {
			String next = RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getUsersUrl() + "/" + userName + "/" + RestProperties.getInstance().getPostsUrl() + "?start=" + String.valueOf(end + 1) + "&end=" + String.valueOf(end + 10);

			final String tags = this.context.getStringAttribute("tags", null);
			if (tags != null) {
				next += "&tags=" + tags;
			}

			if (resourceType != Resource.class) {
				next += "&resourcetype=" + resourceType.toString();
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