package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.posts.GetListOfPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetNewPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetPopularPostsStrategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PostsHandler implements ContextHandler {

	public Strategy createStrategy(final Context context, final StringTokenizer urlTokens, final HttpMethod httpMethod) {
		final int numTokensLeft = urlTokens.countTokens();

		switch (numTokensLeft) {
		case 0:
			// /posts
			if (HttpMethod.GET == httpMethod) {
				return new GetListOfPostsStrategy(context);
			}
			break;
		case 1:
			// /posts/added or popular
			if (HttpMethod.GET == httpMethod) {
				final String path = urlTokens.nextToken();
				if (RestProperties.getInstance().getAddedPostsUrl().equalsIgnoreCase(path)) {
					return new GetNewPostsStrategy(context);
				} else if (RestProperties.getInstance().getPopularPostsUrl().equalsIgnoreCase(path)) {
					return new GetPopularPostsStrategy(context);
				}
			}
			break;
		}
		throw new UnsupportedOperationException("no strategy for url ");
	}
}