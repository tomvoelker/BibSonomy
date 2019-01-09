/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.persons.DeletePersonResourceRelationStrategy;
import org.bibsonomy.rest.strategy.persons.GetListOfPersonsStrategy;
import org.bibsonomy.rest.strategy.persons.GetPersonStrategy;
import org.bibsonomy.rest.strategy.persons.GetResourcePersonRelationsStrategy;
import org.bibsonomy.rest.strategy.persons.PostPersonMergeStrategy;
import org.bibsonomy.rest.strategy.persons.PostPersonStrategy;
import org.bibsonomy.rest.strategy.persons.PostResourcePersonRelationStrategy;
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
		final String personId;
		final String req;

		switch (numTokensLeft) {
			// /persons
			case 0:
				return createPersonStrategy(context, httpMethod);
			// /persons/[personID]
			case 1:
				return createPersonStrategy(context, httpMethod, urlTokens.next());
			// /persons/[personID]/relations|merge
			case 2:
				personId = urlTokens.next();
				req = urlTokens.next();
				if (RESTConfig.RELATION_PARAM.equalsIgnoreCase(req)) {
					return createPersonRelationStrategy(context, httpMethod, personId);
				}
				if (RESTConfig.PERSONS_MERGE_URL.equalsIgnoreCase(req)) {
					return createPersonMergeStrategy(context, httpMethod, personId);
				}
				break;
			// /persons/[personID]/relations/[interhash]/[type]/[index]
			case 5:
				personId = urlTokens.next();
				final String relationsPath = urlTokens.next();
				if (RESTConfig.RELATION_PARAM.equals(relationsPath)) {
					final String interHash = urlTokens.next();
					final String type = urlTokens.next();
					final String index = urlTokens.next();

					if (HttpMethod.DELETE.equals(httpMethod)) {
						try {
							return new DeletePersonResourceRelationStrategy(context, personId, interHash, Integer.parseInt(index), PersonResourceRelationType.valueOf(type.toUpperCase()));
						} catch (final IllegalArgumentException e) {
							throw new BadRequestOrResponseException(e);
						}
					}
				}

				break;
		}

		throw new NoSuchResourceException(ERROR_MESSAGE);
	}

	private Strategy createPersonMergeStrategy(Context context, HttpMethod httpMethod, String personId) {
		switch (httpMethod) {
			case POST:
				return new PostPersonMergeStrategy(context, personId,
						context.getStringAttribute("source", ""));
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "PersonMerge");
		}
	}

	private Strategy createPersonStrategy(Context context, HttpMethod httpMethod, String personId) {
		switch (httpMethod) {
			case GET:
				return new GetPersonStrategy(context, personId);
			case PUT:
				final PersonUpdateOperation operation = PersonUpdateOperation.valueOf(
						context.getStringAttribute("operation", "update_all").toUpperCase());
				return new UpdatePersonStrategy(context, personId, operation);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "PersonList");
		}
	}

	private Strategy createPersonRelationStrategy(Context context, HttpMethod httpMethod, String personId) {
		switch (httpMethod) {
			case GET:
				return new GetResourcePersonRelationsStrategy(context, personId);
			case POST:
				return new PostResourcePersonRelationStrategy(context, personId);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "ResourcePersonRelation");
		}
	}

	private Strategy createPersonStrategy(Context context, HttpMethod httpMethod) {
		switch (httpMethod) {
			case GET:
				return new GetListOfPersonsStrategy(context);
			case POST:
				return new PostPersonStrategy(context);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "Person");
		}
	}
}
