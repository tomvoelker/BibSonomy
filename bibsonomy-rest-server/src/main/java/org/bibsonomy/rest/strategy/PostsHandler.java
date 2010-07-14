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
import org.bibsonomy.rest.strategy.posts.standard.references.DeleteReferencesStrategy;
import org.bibsonomy.rest.strategy.posts.standard.references.PostReferencesStrategy;
import org.bibsonomy.rest.strategy.users.GetPostDetailsStrategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PostsHandler implements ContextHandler {

	@Override
	public Strategy createStrategy(final Context context, final StringTokenizer urlTokens, final HttpMethod httpMethod) {
		switch (urlTokens.countTokens()) {
		case 0:
			// /posts
			if (HttpMethod.GET == httpMethod) {
				return new GetListOfPostsStrategy(context);
			}
			break;
		case 1: {
				final String path = urlTokens.nextToken();
				
				switch(httpMethod) {
				case GET:
						// /posts/added or popular
						if (RestProperties.getInstance().getAddedPostsUrl().equalsIgnoreCase(path)) {
							return new GetNewPostsStrategy(context);
						} else if (RestProperties.getInstance().getPopularPostsUrl().equalsIgnoreCase(path)) {
							return new GetPopularPostsStrategy(context);
						}
						break;
				case POST:
					// /posts/standard
					if (RestProperties.getInstance().getStandardPostsUrl().equalsIgnoreCase(path)) {
						return new PostStandardPostStrategy(context, context.getLogic().getAuthenticatedUser().getName());
					}
					break;
				default:
					// no such resource
					break;
				}
				break;
			}
		case 2: {
				final String path = urlTokens.nextToken();
				final String loggedInUserName = context.getLogic().getAuthenticatedUser().getName();
			
				// /posts/standard/[hash]
				if (!RestProperties.getInstance().getStandardPostsUrl().equalsIgnoreCase(path)) {
					break;
				}
				
				final String resourceHash = urlTokens.nextToken();
				switch (httpMethod) {
					case GET:
						return new GetPostDetailsStrategy(context, "", resourceHash); // gold standards have no owners
					case PUT:
						return new PutStandardPostStrategy(context, loggedInUserName, resourceHash);
					case DELETE:
						return new DeleteStandardPostStrategy(context, loggedInUserName, resourceHash);
					default:
						break; // no such resource
				}
				break;
			}
		case 3: {
				final String path = urlTokens.nextToken();
		
				// /posts/standard/[hash]/references/
				final String hash = urlTokens.nextToken();
				final String references = urlTokens.nextToken();
				if (!RestProperties.getInstance().getStandardPostsUrl().equalsIgnoreCase(path) || !RestProperties.getInstance().getReferencesUrl().equalsIgnoreCase(references)) {
					break;
				}			
			
				switch (httpMethod) {
					case POST:
						return new PostReferencesStrategy(context, hash);
					case DELETE:
						return new DeleteReferencesStrategy(context, hash);
					default:
						break;
				}
			}
		}
		
		throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
	}
}