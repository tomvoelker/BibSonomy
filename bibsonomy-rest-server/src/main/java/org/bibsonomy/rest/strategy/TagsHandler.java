/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import java.util.Arrays;

import org.bibsonomy.common.enums.TagRelation;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.tags.GetListOfTagsStrategy;
import org.bibsonomy.rest.strategy.tags.GetTagDetailsStrategy;
import org.bibsonomy.rest.strategy.tags.GetTagRelationStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class TagsHandler implements ContextHandler {

	@Override
	public Strategy createStrategy(final Context context, final URLDecodingPathTokenizer urlTokens, final HttpMethod httpMethod) {
		final int numTokensLeft = urlTokens.countRemainingTokens();

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
				final String relationAttribute = context.getStringAttribute(RESTConfig.RELATION_PARAM, "");
				
				if (ValidationUtils.present(relationAttribute)) {
					return new GetTagRelationStrategy(context, Arrays.asList(urlTokens.next().split(" ")),
							TagRelation.getRelationByString(relationAttribute));
				}
				
				// No relation attribute found, let's just stick with the normal way.
				return new GetTagDetailsStrategy(context, urlTokens.next());
			}
			break;
		}
		throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
	}
}