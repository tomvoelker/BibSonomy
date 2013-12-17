package org.bibsonomy.rest.strategy;

import java.util.Arrays;
import java.util.StringTokenizer;

import org.bibsonomy.common.enums.TagRelation;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.tags.GetListOfTagsStrategy;
import org.bibsonomy.rest.strategy.tags.GetTagDetailsStrategy;
import org.bibsonomy.rest.strategy.tags.GetTagRelationStrategy;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
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
			// /tags/[tag][?relation=...]
			if (HttpMethod.GET == httpMethod) {
				
				// if a "relation" GET attribute is present, we will handle the request with
				// the relationStrategy. Otherwise, we'll just return the tagDetails.
				String relationAttribute = context.getStringAttribute("relation", "");
				
				if(ValidationUtils.present(relationAttribute)) {
					return new GetTagRelationStrategy(context, Arrays.asList(urlTokens.nextToken().split(" ")),
							TagRelation.getRelationByString(relationAttribute));
				}
				
				// No relation attribute found, let's just stick with the normal way.
				return new GetTagDetailsStrategy(context, urlTokens.nextToken());
			}
			break;
		}
		throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
	}
}