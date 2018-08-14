package org.bibsonomy.rest.strategy;

import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.RESTUtils;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.persons.PostPersonRelationStrategy;
import org.bibsonomy.rest.strategy.persons.PostPersonStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;

import static org.bibsonomy.util.ValidationUtils.present;

public class PersonsHandler implements ContextHandler {
    @Override
    public Strategy createStrategy(Context context, URLDecodingPathTokenizer urlTokens, HttpMethod httpMethod) {
        final int numTokensLeft = urlTokens.countRemainingTokens();
        final String userName;
        final String req;

        switch (numTokensLeft) {
            // /person
            case 0:
                return createPersonListStrategy(context, httpMethod);
            // /person/[username]/relation
            case 2:
                userName = normalizeAndCheckUserName(context, urlTokens);
                req = urlTokens.next();
                if (RESTConfig.RELATION_PARAM.equalsIgnoreCase(req)) {
                    return createPersonRelationStrategy(context, httpMethod, userName);
                }
                break;
        }
        throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax");
    }

    /**
     * @param context
     * @param urlTokens
     * @return the normalized username @see {@link RESTUtils#normalizeUser(String, Context)}
     */
    private String normalizeAndCheckUserName(final Context context, final URLDecodingPathTokenizer urlTokens) {
        final String userName = RESTUtils.normalizeUser(urlTokens.next(), context);
        if (!present(userName)) {
            throw new BadRequestOrResponseException("username not specified");
        }
        return userName;
    }

    private Strategy createPersonRelationStrategy(Context context, HttpMethod httpMethod, String userName) {
        switch (httpMethod) {
            case POST:
                return new PostPersonRelationStrategy(context, userName);
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
