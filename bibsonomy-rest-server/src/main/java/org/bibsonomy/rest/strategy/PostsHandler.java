/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy;

import org.bibsonomy.model.enums.GoldStandardRelation;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.posts.GetListOfPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetNewPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetPopularPostsStrategy;
import org.bibsonomy.rest.strategy.posts.community.PostCommunityPostStrategy;
import org.bibsonomy.rest.strategy.posts.community.PutCommunityPostStrategy;
import org.bibsonomy.rest.strategy.posts.community.relations.DeleteRelationsStrategy;
import org.bibsonomy.rest.strategy.posts.community.relations.PostRelationsStrategy;
import org.bibsonomy.rest.strategy.users.DeletePostStrategy;
import org.bibsonomy.rest.strategy.users.GetPostDetailsStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class PostsHandler implements ContextHandler {

	@Override
	public Strategy createStrategy(final Context context, final URLDecodingPathTokenizer urlTokens, final HttpMethod httpMethod) {
		switch (urlTokens.countRemainingTokens()) {
		case 0:
			// /posts
			if (HttpMethod.GET == httpMethod) {
				return new GetListOfPostsStrategy(context);
			}
			break;
		case 1: {
				final String path = urlTokens.next();
				
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
				final String path = urlTokens.next();
				final String loggedInUserName = context.getLogic().getAuthenticatedUser().getName();
			
				// /posts/community/[hash]
				if (!RESTConfig.COMMUNITY_SUB_PATH.equalsIgnoreCase(path)) {
					break;
				}
				
				final String resourceHash = urlTokens.next();
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
				final String path = urlTokens.next();
				final String hash = urlTokens.next();
				
				final String relationString = urlTokens.next();
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