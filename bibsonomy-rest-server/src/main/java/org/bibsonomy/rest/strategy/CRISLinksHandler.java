/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.cris_links.DeleteCRISLinkStrategy;
import org.bibsonomy.rest.strategy.cris_links.PostCRISLinkStrategy;
import org.bibsonomy.rest.strategy.cris_links.PutCRISLinkStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;

/**
 * handler for {@link org.bibsonomy.model.cris.CRISLink} related strategies
 *
 * @author pda
 */
public class CRISLinksHandler implements ContextHandler {

	@Override
	public Strategy createStrategy(Context context, URLDecodingPathTokenizer urlTokens, HttpMethod httpMethod) {
		final int numTokensLeft = urlTokens.countRemainingTokens();

		switch (numTokensLeft) {
			// /cris_links
			case 0:
				return createCRISLinkStrategy(context, httpMethod);
			default:
				throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax");
		}
	}

	private Strategy createCRISLinkStrategy(Context context, HttpMethod httpMethod) {
		switch (httpMethod) {
			case POST:
				return new PostCRISLinkStrategy(context);
			case PUT:
				return new PutCRISLinkStrategy(context);
			case DELETE:
				return new DeleteCRISLinkStrategy(context);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "CRIS Link");
		}
	}
}
