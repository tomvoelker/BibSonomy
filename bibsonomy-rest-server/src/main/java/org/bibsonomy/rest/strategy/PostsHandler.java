package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.model.enums.GoldStandardRelation;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.posts.GetListOfPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetNewPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetPopularPostsStrategy;
import org.bibsonomy.rest.strategy.posts.community.PostCommunityPostStrategy;
import org.bibsonomy.rest.strategy.posts.community.PutCommunityPostStrategy;
import org.bibsonomy.rest.strategy.posts.community.references.DeleteRelationsStrategy;
import org.bibsonomy.rest.strategy.posts.community.references.PostRelationsStrategy;
import org.bibsonomy.rest.strategy.users.DeletePostStrategy;
import org.bibsonomy.rest.strategy.users.GetPostDetailsStrategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
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
						if (RESTConfig.POSTS_ADDED_SUB_PATH.equalsIgnoreCase(path)) {
							return new GetNewPostsStrategy(context);
						} else if (RESTConfig.POSTS_POPULAR_SUB_PATH.equalsIgnoreCase(path)) {
							return new GetPopularPostsStrategy(context);
						}
						break;
				case POST:
					// /posts/community
					if (RESTConfig.COMMUNITY_SUB_PATH.equalsIgnoreCase(path)) {
						return new PostCommunityPostStrategy(context, context.getLogic().getAuthenticatedUser().getName());
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
			
				// /posts/community/[hash]
				if (!RESTConfig.COMMUNITY_SUB_PATH.equalsIgnoreCase(path)) {
					break;
				}
				
				final String resourceHash = urlTokens.nextToken();
				switch (httpMethod) {
					case GET:
						return new GetPostDetailsStrategy(context, "", resourceHash); // gold standards have no owners
					case PUT:
						return new PutCommunityPostStrategy(context, loggedInUserName, resourceHash);
					case DELETE:
						return new DeletePostStrategy(context, loggedInUserName, resourceHash);
					default:
						break; // no such resource
				}
				break;
			}
		case 3: {
				// /posts/community/[hash]/reference/ or /posts/community/[hash]/part_of/
				final String path = urlTokens.nextToken();
				final String hash = urlTokens.nextToken();
				
				final String relationString = urlTokens.nextToken();
				if (!RESTConfig.COMMUNITY_SUB_PATH.equalsIgnoreCase(path)) {
					break;
				}
				try {
					final GoldStandardRelation relation = Enum.valueOf(GoldStandardRelation.class, relationString.toUpperCase());
					switch (httpMethod) {
					case POST:
						return new PostRelationsStrategy(context, hash, relation);
					case DELETE:
						return new DeleteRelationsStrategy(context, hash, relation);
					default:
						break;
					}
				} catch (IllegalArgumentException e) {
					// invalid relation
					break;
				}
			}
		}
		
		throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
	}
}