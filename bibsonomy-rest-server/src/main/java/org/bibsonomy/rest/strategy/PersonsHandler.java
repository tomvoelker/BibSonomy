/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import org.bibsonomy.common.enums.PersonOperation;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.persons.GetListOfPersonsStrategy;
import org.bibsonomy.rest.strategy.persons.GetPersonPostsStrategy;
import org.bibsonomy.rest.strategy.persons.GetPersonRelationsStrategy;
import org.bibsonomy.rest.strategy.persons.PostPersonMergeStrategy;
import org.bibsonomy.rest.strategy.persons.PostPersonStrategy;
import org.bibsonomy.rest.strategy.persons.UpdatePersonStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;

/**
 * handler for {@link org.bibsonomy.model.Person} related strategies
 *
 * @author pda
 */
public class PersonsHandler implements ContextHandler {

	public static final String ERROR_MESSAGE = "cannot process url (no strategy available) - please check url syntax";

	@Override
	public Strategy createStrategy(Context context, URLDecodingPathTokenizer urlTokens, HttpMethod httpMethod) {
		final int numTokensLeft = urlTokens.countRemainingTokens();
		final String req;

		switch (numTokensLeft) {
			// /persons
			case 0:
				return createPersonStrategy(context, httpMethod);
			// /persons/(posts|relations|merge)
			case 1:
				req = urlTokens.next();
				if (RESTConfig.POSTS_URL.equalsIgnoreCase(req)) {
					return createPersonPostsStrategy(context, httpMethod);
				}
				if (RESTConfig.RELATIONS_SUB_PATH.equalsIgnoreCase(req)) {
					return createPersonRelationsStrategy(context, httpMethod);
				}
				if (RESTConfig.PERSONS_MERGE_URL.equalsIgnoreCase(req)) {
					return createPersonMergeStrategy(context, httpMethod);
				}
				break;
			default:
				break;
		}

		throw new NoSuchResourceException(ERROR_MESSAGE);
	}

	/**
	 *
	 * @param context
	 * @param httpMethod
	 * @return
	 */
	private Strategy createPersonStrategy(Context context, HttpMethod httpMethod) {
		switch (httpMethod) {
			case GET:
				return new GetListOfPersonsStrategy(context);
			case POST:
				return new PostPersonStrategy(context);
			case PUT:
				final PersonOperation operation = PersonOperation.valueOf(
						context.getStringAttribute("operation", "update_all").toUpperCase());
				return new UpdatePersonStrategy(context, operation);
			case DELETE:
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "PersonList");
		}
	}

	/**
	 *
	 * @param context
	 * @param httpMethod
	 * @return
	 */
	private Strategy createPersonPostsStrategy(Context context, HttpMethod httpMethod) {
		switch (httpMethod) {
			case GET:
				return new GetPersonPostsStrategy(context);
			case POST:
			case PUT:
			case DELETE:
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "PersonPosts");
		}
	}

	/**
	 *
	 * @param context
	 * @param httpMethod
	 * @return
	 */
	private Strategy createPersonRelationsStrategy(Context context, HttpMethod httpMethod) {
		switch (httpMethod) {
			case GET:
				return new GetPersonRelationsStrategy(context);
			case POST:
			case PUT:
			case DELETE:
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "PersonRelations");
		}
	}

	/**
	 *
	 * @param context
	 * @param httpMethod
	 * @return
	 */
	private Strategy createPersonMergeStrategy(Context context, HttpMethod httpMethod) {
		switch (httpMethod) {
			case POST:
				return new PostPersonMergeStrategy(context);
			case GET:
			case PUT:
			case DELETE:
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "PersonMerge");
		}
	}
}
