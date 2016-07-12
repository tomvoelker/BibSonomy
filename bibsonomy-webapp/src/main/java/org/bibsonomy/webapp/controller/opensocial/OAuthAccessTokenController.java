/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.opensocial;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URISyntaxException;

import net.oauth.OAuth;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.opensocial.OAuthCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * This controller implements the OAuth endpoints described in RFC 5849, section
 * 2:
 * 
 * Token Request ("accessToken")
 * The endpoint used by the client to request a set of token
 * credentials using the set of temporary credentials as described
 * in Section 2.3.
 * 
 * @author fei
 */
public class OAuthAccessTokenController extends OAuthProtocolController {
	private static final Log log = LogFactory.getLog(OAuthAccessTokenController.class);

	// ------------------------------------------------------------------------
	// OAuthProtocolController interface
	// ------------------------------------------------------------------------
	@Override
	protected View doWorkOn(final OAuthCommand command, final User loginUser) throws IOException, OAuthException, URISyntaxException {
		return this.createAccessToken(command, loginUser);
	}

	// ------------------------------------------------------------------------
	// OAuth protocol end point implementation
	// ------------------------------------------------------------------------
	/**
	 * Hand out an access token if the consumer key and secret are valid and the
	 * user authorized
	 * the requestToken
	 * 
	 * @param command
	 * @param loginUser
	 * @return
	 * @throws URISyntaxException
	 * @throws OAuthException
	 * @throws IOException
	 */
	private View createAccessToken(final OAuthCommand command, final User loginUser) throws IOException, OAuthException, URISyntaxException {
		// extract the OAuth parameters from the request
		final OAuthMessage requestMessage = this.requestLogic.getOAuthMessage(null);

		// obtain the corresponding token credential
		final OAuthEntry entry = this.getValidatedEntry(requestMessage);
		if (!present(entry.getUserId())) {
			final OAuthProblemException e = new OAuthProblemException(OAuth.Problems.OAUTH_PARAMETERS_ABSENT);
			e.setParameter(OAuth.Problems.OAUTH_PARAMETERS_ABSENT, OAUTH_HEADER_USER_ID);
			log.info("No username given for accessing the OAuth token.");
		}

		if (!present(entry)) {
			throw new OAuthProblemException(OAuth.Problems.TOKEN_REJECTED);
		}

		if (present(entry.getCallbackToken())) {
			// We're using the fixed protocol
			final String clientCallbackToken = requestMessage.getParameter(OAuth.OAUTH_VERIFIER);
			if (!entry.getCallbackToken().equals(clientCallbackToken)) {
				this.getDataStore().disableToken(entry);
				throw new OAuthProblemException(OAuth.Problems.PARAMETER_REJECTED);
			}
		} else if (!entry.isAuthorized()) {
			// Old protocol. Catch consumers trying to convert a token to one
			// that's not authorized
			this.getDataStore().disableToken(entry);
			throw new OAuthProblemException(OAuth.Problems.TOKEN_REJECTED);
		}

		// turn request token into access token
		final OAuthEntry accessEntry = this.getDataStore().convertToAccessToken(entry);

		command.setResponseString(OAuth.formEncode(OAuth.newList(OAuth.OAUTH_TOKEN, accessEntry.getToken(), OAuth.OAUTH_TOKEN_SECRET, accessEntry.getTokenSecret(), OAUTH_HEADER_USER_ID, entry.getUserId())));
		return Views.OAUTH_RESPONSE;
	}

	@Override
	protected String getRequestAction() {
		return OAuthAction.accessToken.name();
	}

}
