/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import java.util.StringTokenizer;

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.concepts.GetConceptDetailsStrategy;
import org.bibsonomy.rest.strategy.concepts.GetConceptsStrategy;

/**
 * A Context Handler for all <em>/concept</em> urls
 * 
 * @author Stefan Stützer
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
