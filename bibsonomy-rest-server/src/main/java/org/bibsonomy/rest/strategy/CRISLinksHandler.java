package org.bibsonomy.rest.strategy;

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.cris_links.PostCRISLinkStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;

/**
 * handler for {@link org.bibsonomy.model.cris.CRISLink} related strategies
 *
 * @author pda
 */
public class CRISLinksHandler implements ContextHandler{
    @Override
    public Strategy createStrategy(Context context, URLDecodingPathTokenizer urlTokens, HttpMethod httpMethod) {
        final int numTokensLeft = urlTokens.countRemainingTokens();

        switch (numTokensLeft) {
            // /cris_links
            case 0:
                return createProjectStrategy(context, httpMethod);
            default:
                throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax");
        }
    }

    private Strategy createProjectStrategy(Context context, HttpMethod httpMethod) {
        switch (httpMethod) {
            case POST:
                return new PostCRISLinkStrategy(context);
            default:
                throw new UnsupportedHttpMethodException(httpMethod, "CRIS Link");
        }
    }
}
