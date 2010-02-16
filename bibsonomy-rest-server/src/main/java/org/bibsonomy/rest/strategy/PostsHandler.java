package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.posts.GetListOfPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetNewPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetPopularPostsStrategy;
import org.bibsonomy.rest.strategy.posts.standard.DeleteStandardPostStrategy;
import org.bibsonomy.rest.strategy.posts.standard.PostStandardPostStrategy;
import org.bibsonomy.rest.strategy.posts.standard.PutStandardPostStrategy;
import org.bibsonomy.rest.strategy.users.GetPostDetailsStrategy;

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
			final String path = urlTokens.nextToken();
			switch(httpMethod) {
			case GET:
				// /posts/added or popular
				if (RestProperties.getInstance().getAddedPostsUrl().equalsIgnoreCase(path)) {
					return new GetNewPostsStrategy(context);
				} else if (RestProperties.getInstance().getPopularPostsUrl().equalsIgnoreCase(path)) {
					return new GetPopularPostsStrategy(context);
				}
			case POST:
				// /posts/standard
				if (RestProperties.getInstance().getStandardPostsUrl().equalsIgnoreCase(path)) {
					return new PostStandardPostStrategy(context, "");
				}
			}
			
			break;
		case 2:
			// /posts/standard/[hash]
			final String standardPath = urlTokens.nextToken();
			if (!RestProperties.getInstance().getStandardPostsUrl().equalsIgnoreCase(standardPath)) {
				break;
			}
			final String resourceHash = urlTokens.nextToken();
			
			final String loggedInUserName = context.getLogic().getAuthenticatedUser().getName();
			switch (httpMethod) {
			case GET:
				return new GetPostDetailsStrategy(context, "", resourceHash); // TODO: logic access
			case PUT:
				return new PutStandardPostStrategy(context, loggedInUserName, resourceHash);
			case DELETE:
				return new DeleteStandardPostStrategy(context, "", resourceHash); // TODO: logic access
			}
		case 3:
			// /posts/standard/[hash]/references/
			final String pathStandard = urlTokens.nextToken();
			final String resHash = urlTokens.nextToken();
			
			// TODO: add and remove references
			switch (httpMethod) {
			case POST:
			case DELETE: 
			}
			
		}
		
		throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
	}
}