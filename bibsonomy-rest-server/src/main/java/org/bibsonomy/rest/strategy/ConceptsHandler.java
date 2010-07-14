package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.concepts.GetConceptDetailsStrategy;
import org.bibsonomy.rest.strategy.concepts.GetConceptsStrategy;

/**
 * A Context Handler for all <em>/concept</em> urls
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class ConceptsHandler implements ContextHandler {
	
	@Override
	public Strategy createStrategy(final Context context, final StringTokenizer urlTokens, final HttpMethod httpMethod) {
		final int numTokensLeft = urlTokens.countTokens();

		switch (numTokensLeft) {
		case 0:
			// /concepts
			if (HttpMethod.GET == httpMethod) {
				return new GetConceptsStrategy(context);
			}
			break;
		case 1:
			// /concepts/[conceptname]
			if (HttpMethod.GET == httpMethod) {
				final String conceptName = urlTokens.nextToken();
				return new GetConceptDetailsStrategy(context, conceptName);
			}
			break;
		}
		throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax");
	}

}
