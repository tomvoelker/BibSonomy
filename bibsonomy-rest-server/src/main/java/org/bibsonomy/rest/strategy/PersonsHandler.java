package org.bibsonomy.rest.strategy;

import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.persons.PostPersonStrategy;
import org.bibsonomy.rest.strategy.persons.PostResourcePersonRelationStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;

public class PersonsHandler implements ContextHandler {
    @Override
    public Strategy createStrategy(Context context, URLDecodingPathTokenizer urlTokens, HttpMethod httpMethod) {
        final int numTokensLeft = urlTokens.countRemainingTokens();
        final String personId;
        final String req;

        switch (numTokensLeft) {
            // /persons
            case 0:
                return createPersonListStrategy(context, httpMethod);
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

    private Strategy createPersonRelationStrategy(Context context, HttpMethod httpMethod, String userId) {
        switch (httpMethod) {
            case POST:
                return new PostResourcePersonRelationStrategy(context);
            default:
                throw new UnsupportedHttpMethodException(httpMethod, "PersonRelation");
        }
    }

    private Strategy createPersonListStrategy(Context context, HttpMethod httpMethod) {
        switch (httpMethod) {
            case POST:
                return new PostPersonStrategy(context);
            default:
                throw new UnsupportedHttpMethodException(httpMethod, "PersonList");
        }
    }
}
