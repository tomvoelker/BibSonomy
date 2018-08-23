package org.bibsonomy.rest.strategy;

import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.persons.GetPersonStrategy;
import org.bibsonomy.rest.strategy.persons.PostPersonStrategy;
import org.bibsonomy.rest.strategy.persons.PostResourcePersonRelationStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;

/**
 * handler for {@link org.bibsonomy.model.Person} related strategies
 *
 * @author pda
 */
public class PersonsHandler implements ContextHandler {

	@Override
	public Strategy createStrategy(Context context, URLDecodingPathTokenizer urlTokens, HttpMethod httpMethod) {
		final int numTokensLeft = urlTokens.countRemainingTokens();
		final String personId;
		final String req;

		switch (numTokensLeft) {
			// /persons
			case 0:
				return createPersonStrategy(context, httpMethod);
			// /person/[personID]
			case 1:
				return createPersonStrategy(context, httpMethod, urlTokens.next());
			// /persons/[personID]/relation
			case 2:
				personId = urlTokens.next();
				req = urlTokens.next();
				if (RESTConfig.RELATION_PARAM.equalsIgnoreCase(req)) {
					return createPersonRelationStrategy(context, httpMethod, personId);
				}
				break;
		}
		throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax");
	}

	private Strategy createPersonStrategy(Context context, HttpMethod httpMethod, String personId) {
		switch (httpMethod) {
			case GET:
				return new GetPersonStrategy(context, personId);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "PersonList");
		}
	}

	private Strategy createPersonRelationStrategy(Context context, HttpMethod httpMethod, String personId) {
		switch (httpMethod) {
			case POST:
				return new PostResourcePersonRelationStrategy(context, personId);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "ResourcePersonRelation");
		}
	}

	private Strategy createPersonStrategy(Context context, HttpMethod httpMethod) {
		switch (httpMethod) {
			case POST:
				return new PostPersonStrategy(context);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "Person");
		}
	}
}
