/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.RESTUtils;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.organizations.AddOrganizationStrategy;
import org.bibsonomy.rest.strategy.organizations.AddUserToOrganizationStrategy;
import org.bibsonomy.rest.strategy.organizations.DeleteOrganizationStrategy;
import org.bibsonomy.rest.strategy.organizations.GetListOfOrganizationStrategy;
import org.bibsonomy.rest.strategy.organizations.GetOrganizationStrategy;
import org.bibsonomy.rest.strategy.organizations.GetUserListOfOrganizationStrategy;
import org.bibsonomy.rest.strategy.organizations.RemoveUserFromOrganizationStrategy;
import org.bibsonomy.rest.strategy.organizations.UpdateOrganizationDetailsStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;

/**
 * Handler for organization related strategies
 *
 * @author kchoong
 */
public class OrganizationsHandler implements ContextHandler {

	@Override
	public Strategy createStrategy(final Context context, final URLDecodingPathTokenizer urlTokens, final HttpMethod httpMethod) {
		final int numTokensLeft = urlTokens.countRemainingTokens();
		final String orgName;

		switch (numTokensLeft) {
			case 0:
				// /organizations
				return createOrganizationListStrategy(context, httpMethod);
			case 1:
				// /organizations/[orgname]
				return createOrganizationStrategy(context, httpMethod, urlTokens.next());
			case 2:
				orgName = urlTokens.next();
				if (RESTConfig.USERS_URL.equalsIgnoreCase(urlTokens.next())) {
					// /organizations/[orgname]/users
					return createUserPostsStrategy(context, httpMethod, orgName);
				}
				break;
			case 3:
				// /organizations/[orgname]/users/[username]
				orgName = urlTokens.next();
				if (RESTConfig.USERS_URL.equalsIgnoreCase(urlTokens.next())) {
					final String username = RESTUtils.normalizeUser(urlTokens.next(), context);
					if (HttpMethod.DELETE == httpMethod) {
						return new RemoveUserFromOrganizationStrategy(context, orgName, username);
					}
				}
				break;
		}
		throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
	}

	private static Strategy createOrganizationListStrategy(final Context context, final HttpMethod httpMethod) {
		switch (httpMethod) {
			case GET:
				return new GetListOfOrganizationStrategy(context);
			case POST:
				return new AddOrganizationStrategy(context);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "OrganizationList");
		}
	}

	private static Strategy createOrganizationStrategy(final Context context, final HttpMethod httpMethod, final String groupName) {
		switch (httpMethod) {
			case GET:
				return new GetOrganizationStrategy(context, groupName);
			case PUT:
				return new UpdateOrganizationDetailsStrategy(context, groupName);
			case DELETE:
				return new DeleteOrganizationStrategy(context, groupName);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "Organization");
		}
	}

	private static Strategy createUserPostsStrategy(final Context context, final HttpMethod httpMethod, final String orgName) {
		switch (httpMethod) {
			case GET:
				return new GetUserListOfOrganizationStrategy(context, orgName);
			case POST:
				return new AddUserToOrganizationStrategy(context, orgName);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "Organization");
		}
	}
}
