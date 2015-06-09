/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.RESTUtils;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.clipboard.DeleteClipboardStrategy;
import org.bibsonomy.rest.strategy.clipboard.GetClipboardStrategy;
import org.bibsonomy.rest.strategy.clipboard.PostClipboardStrategy;
import org.bibsonomy.rest.strategy.users.DeleteDocumentStrategy;
import org.bibsonomy.rest.strategy.users.DeletePostStrategy;
import org.bibsonomy.rest.strategy.users.DeleteUserConceptStrategy;
import org.bibsonomy.rest.strategy.users.DeleteUserStrategy;
import org.bibsonomy.rest.strategy.users.GetPostDetailsStrategy;
import org.bibsonomy.rest.strategy.users.GetPostDocumentStrategy;
import org.bibsonomy.rest.strategy.users.GetRelatedusersForUserStrategy;
import org.bibsonomy.rest.strategy.users.GetUserConceptStrategy;
import org.bibsonomy.rest.strategy.users.GetUserConceptsStrategy;
import org.bibsonomy.rest.strategy.users.GetUserListStrategy;
import org.bibsonomy.rest.strategy.users.GetUserPostsStrategy;
import org.bibsonomy.rest.strategy.users.GetUserStrategy;
import org.bibsonomy.rest.strategy.users.PostPostDocumentStrategy;
import org.bibsonomy.rest.strategy.users.PostPostStrategy;
import org.bibsonomy.rest.strategy.users.PostRelatedusersForUserStrategy;
import org.bibsonomy.rest.strategy.users.PostUserConceptStrategy;
import org.bibsonomy.rest.strategy.users.PostUserStrategy;
import org.bibsonomy.rest.strategy.users.PutPostStrategy;
import org.bibsonomy.rest.strategy.users.PutUpdateDocumentStrategy;
import org.bibsonomy.rest.strategy.users.PutUserConceptStrategy;
import org.bibsonomy.rest.strategy.users.PutUserStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @author Christian Kramer
 */
public class UsersHandler implements ContextHandler {

	@Override
	public Strategy createStrategy(final Context context, final URLDecodingPathTokenizer urlTokens, final HttpMethod httpMethod) {
		final int numTokensLeft = urlTokens.countRemainingTokens();
		final String userName;
		final String req;

		switch (numTokensLeft) {
		case 0:
			// /users
			return createUserListStrategy(context, httpMethod);
		case 1:
			userName = normailzeAndCheckUserName(context, urlTokens);
			// /users/[username]
			return createUserStrategy(context, httpMethod, userName);
		case 2:
			userName = normailzeAndCheckUserName(context, urlTokens);
			req = urlTokens.next();

			// /users/[username]/posts
			if (RESTConfig.POSTS_URL.equalsIgnoreCase(req)) {
				return createUserPostsStrategy(context, httpMethod, userName);
			}

			// /users/[username]/concepts
			if (RESTConfig.CONCEPTS_URL.equalsIgnoreCase(req)) {
				return createUserConceptsStrategy(context, httpMethod, userName);
			}

			// /users/[username]/friends , /users/[username]/followers
			if (RESTConfig.FRIENDS_SUB_PATH.equalsIgnoreCase(req) || RESTConfig.FOLLOWERS_SUB_PATH.equalsIgnoreCase(req)) {
				return createRelatedusersForUserStrategy(context, httpMethod, userName, req, null);
			}
			// /users/[username]/clipboard
			if (RESTConfig.CLIPBOARD_SUBSTRING.equalsIgnoreCase(req)) {
				return createUserClipboardStrategy(context, httpMethod, userName, null);
			}
			break;
		case 3:
			userName = normailzeAndCheckUserName(context, urlTokens);
			req = urlTokens.next();

			// /users/[username]/posts/[resourceHash]
			if (RESTConfig.POSTS_URL.equalsIgnoreCase(req)) {
				final String resourceHash = urlTokens.next();
				return createUserPostStrategy(context, httpMethod, userName, resourceHash);
			}

			// /users/[username]/concepts/[conceptName]
			if (RESTConfig.CONCEPTS_URL.equalsIgnoreCase(req)) {
				final String conceptName = urlTokens.next();
				return createUserConceptsStrategy(context, httpMethod, userName, conceptName);
			}
			// /users/[username]/friends/[tag]
			if (RESTConfig.FRIENDS_SUB_PATH.equalsIgnoreCase(req)) {
				final String tag = urlTokens.next();
				return createRelatedusersForUserStrategy(context, httpMethod, userName, req, tag);
			}
			if (RESTConfig.CLIPBOARD_SUBSTRING.equalsIgnoreCase(req)) {
				final String resourceHash = urlTokens.next();
				return createUserClipboardStrategy(context, httpMethod, userName, resourceHash);
			}
			break;
		case 4:
			// /users/[username]/posts/[resourcehash]/documents
			userName = normailzeAndCheckUserName(context, urlTokens);
			if (RESTConfig.POSTS_URL.equalsIgnoreCase(urlTokens.next())) {
				final String resourceHash = urlTokens.next();

				if (RESTConfig.DOCUMENTS_SUB_PATH.equalsIgnoreCase(urlTokens.next())) {
					return createDocumentPostStrategy(context, httpMethod, userName, resourceHash);
				}
			}
			break;
		case 5:
			// /users/[username]/posts/[resourcehash]/documents/[filename]
			userName = normailzeAndCheckUserName(context, urlTokens);
			if (RESTConfig.POSTS_URL.equalsIgnoreCase(urlTokens.next())) {
				final String resourceHash = urlTokens.next();

				if (RESTConfig.DOCUMENTS_SUB_PATH.equalsIgnoreCase(urlTokens.next())) {
					final String filename = urlTokens.next();
					return createDocumentPostStrategy(context, httpMethod, userName, resourceHash, filename);
				}
			}
			break;
		}
		throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
	}

	/**
	 * @param context
	 * @param urlTokens
	 * @return the normalized username @see {@link RESTUtils#normalizeUser(String, Context)}
	 */
	protected String normailzeAndCheckUserName(final Context context, final URLDecodingPathTokenizer urlTokens) {
		final String userName = RESTUtils.normalizeUser(urlTokens.next(), context);
		if (!present(userName)) {
			throw new BadRequestOrResponseException("username not specified");
		}
		return userName;
	}

	private static Strategy createUserListStrategy(final Context context, final HttpMethod httpMethod) {
		switch (httpMethod) {
		case GET:
			return new GetUserListStrategy(context);
		case POST:
			return new PostUserStrategy(context);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "UserList");
		}
	}

	private static Strategy createUserStrategy(final Context context, final HttpMethod httpMethod, final String userName) {
		switch (httpMethod) {
		case GET:
			return new GetUserStrategy(context, userName);
		case PUT:
			return new PutUserStrategy(context, userName);
		case DELETE:
			return new DeleteUserStrategy(context, userName);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "User");
		}
	}

	private static Strategy createUserPostsStrategy(final Context context, final HttpMethod httpMethod, final String userName) {
		switch (httpMethod) {
		case GET:
			return new GetUserPostsStrategy(context, userName);
		case POST:
			return new PostPostStrategy(context, userName);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "User-Post");
		}
	}

	private static Strategy createUserPostStrategy(final Context context, final HttpMethod httpMethod, final String userName, final String resourceHash) {
		switch (httpMethod) {
		case GET:
			return new GetPostDetailsStrategy(context, userName, resourceHash);
		case PUT:
			return new PutPostStrategy(context, userName, resourceHash);
		case DELETE:
			return new DeletePostStrategy(context, userName, resourceHash);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "User");
		}
	}

	private static Strategy createDocumentPostStrategy(final Context context, final HttpMethod httpMethod, final String userName, final String resourceHash) {
		switch (httpMethod) {
		case POST:
			return new PostPostDocumentStrategy(context, userName, resourceHash);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "User-Post-Document");
		}
	}

	private static Strategy createDocumentPostStrategy(final Context context, final HttpMethod httpMethod, final String userName, final String resourceHash, final String filename) {
		switch (httpMethod) {
		case GET:
			return new GetPostDocumentStrategy(context, userName, resourceHash, filename);
		case DELETE:
			return new DeleteDocumentStrategy(context, userName, resourceHash, filename);
		case PUT:
			return new PutUpdateDocumentStrategy(context, userName, resourceHash, filename);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "Document-Get-Delete-Document");
		}
	}

	private static Strategy createUserConceptsStrategy(final Context context, final HttpMethod httpMethod, final String userName) {
		switch (httpMethod) {
		case GET:
			return new GetUserConceptsStrategy(context, userName);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "Concepts");

		}
	}

	private static Strategy createRelatedusersForUserStrategy(final Context context, final HttpMethod httpMethod, final String userName, final String relationship, final String tag) {
		switch (httpMethod) {
		case GET:
			return new GetRelatedusersForUserStrategy(context, userName, relationship, tag);
		case POST:
			return new PostRelatedusersForUserStrategy(context, userName, relationship, tag);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "Friends");

		}
	}

	private static Strategy createUserConceptsStrategy(final Context context, final HttpMethod httpMethod, final String userName, final String conceptName) {
		switch (httpMethod) {
		case GET:
			return new GetUserConceptStrategy(context, conceptName, userName);
		case PUT:
			return new PutUserConceptStrategy(context, userName);
		case POST:
			return new PostUserConceptStrategy(context, userName);
		case DELETE:
			return new DeleteUserConceptStrategy(context, conceptName, userName);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "Concept-Conceptname");
		}
	}

	private static Strategy createUserClipboardStrategy(final Context context, final HttpMethod httpMethod, final String userName, final String resourceHash) {
		switch (httpMethod) {
		case GET:
			return new GetClipboardStrategy(context, userName);
		case POST:
			if (!present(resourceHash)) {
				throw new BadRequestOrResponseException("missed resource hash");
			}
			return new PostClipboardStrategy(context, userName, resourceHash);
		case DELETE:
			final boolean clearClipboard = Boolean.parseBoolean(context.getStringAttribute(RESTConfig.CLIPBOARD_CLEAR, "false"));
			if (!present(resourceHash) && !clearClipboard) {
				throw new BadRequestOrResponseException("missed resource hash");
			}
			return new DeleteClipboardStrategy(context, userName, resourceHash, clearClipboard);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "clipboard");
		}
	}
}