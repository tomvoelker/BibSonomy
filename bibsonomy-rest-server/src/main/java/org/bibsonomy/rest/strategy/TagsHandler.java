package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.tags.GetListOfTagsStrategy;
import org.bibsonomy.rest.strategy.tags.GetTagDetailsStrategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class TagsHandler implements ContextHandler {

	@Override
	public Strategy createStrategy(final Context context, final StringTokenizer urlTokens, final HttpMethod httpMethod) {
		final int numTokensLeft = urlTokens.countTokens();

		switch (numTokensLeft) {
		case 0:
			// /tags
			if (HttpMethod.GET == httpMethod) {
				return new GetListOfTagsStrategy(context);
			}
			break;
		case 1:
			// /tags/[tag]
			if (HttpMethod.GET == httpMethod) {
				return new GetTagDetailsStrategy(context, urlTokens.nextToken());
			}
			break;
		}
		throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
	}
}