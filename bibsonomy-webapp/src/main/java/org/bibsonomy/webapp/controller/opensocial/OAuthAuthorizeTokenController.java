/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.opensocial.OAuthCommand;
import org.bibsonomy.webapp.command.opensocial.OAuthCommand.AuthorizeAction;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * This controller implements the OAuth endpoints described in RFC 5849, section 2:
 * 
 *  Resource Owner Authorization ("authorize")
 *        The endpoint to which the resource owner is redirected to grant
 *        authorization as described in Section 2.2.
 * 
 * 
 * @author fei
 */
public class OAuthAuthorizeTokenController extends OAuthProtocolController {
	private static final Log log = LogFactory.getLog(OAuthAuthorizeTokenController.class);
	
	/**
	 * authorize a given temporary credential ("request token")
	 */
	@Override
	protected View doWorkOn(final OAuthCommand command, final User loginUser) throws IOException, OAuthException, URISyntaxException {
		// only logged in user may authorize tokens
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		
		// extract the OAuth parameters from the request
		final OAuthMessage requestMessage = this.requestLogic.getOAuthMessage(null);

		// retrieve the previously generated temporary credentials corresponding to the given OAuth token
		if (!present(requestMessage.getToken())) {
			final OAuthProblemException e = new OAuthProblemException(OAuth.Problems.OAUTH_PARAMETERS_ABSENT);
			e.setParameter(OAuth.Problems.OAUTH_PARAMETERS_ABSENT, OAuth.OAUTH_TOKEN);
			throw e;
		}
		final OAuthEntry entry = getDataStore().getEntry(requestMessage.getToken());

		if (!present(entry)) {
			final OAuthProblemException e = new OAuthProblemException(OAuth.Problems.PARAMETER_REJECTED);
			e.setParameter(OAuth.Problems.OAUTH_PARAMETERS_REJECTED, OAuth.OAUTH_TOKEN);
			throw e;
		}

		final OAuthConsumer consumer = getDataStore().getConsumer(entry.getConsumerKey());

		// Extremely rare case where consumer dissappears
		if (!present(consumer)) {
			throw new OAuthProblemException(OAuth.Problems.CONSUMER_KEY_UNKNOWN);
		}

		// The token is disabled if you try to convert to an access token prior to authorization
		if (entry.getType() == OAuthEntry.Type.DISABLED) {
			throw new OAuthProblemException(OAuth.Problems.TOKEN_REVOKED);
		}

		// get the client's callback URL
		String callback = entry.getCallbackUrl();

		// fill in consumer meta information
		command.setConsumer(consumer);
		command.setEntry(entry);
		command.setAppDescription((String)consumer.getProperty("description"));
		command.setAppIcon((String)consumer.getProperty("icon"));
		command.setAppThumbnail((String)consumer.getProperty("thumbnail"));
		command.setAppTitle((String)consumer.getProperty("title"));
		command.setCallBackUrl(callback);

		// Redirect to a UI flow if the token is not authorized
		if (!entry.isAuthorized() && !AuthorizeAction.Authorize.toString().equals(command.getAuthorizeAction()) && !AuthorizeAction.Deny.toString().equals(command.getAuthorizeAction())) {
			return Views.OAUTH_AUTHORIZE;
		}

		// If user clicked on the Authorize button then we're good.
		// TODO: check if it is possible to create a link that silently authorizes a requesttoken (that would probably be bad)
		if (AuthorizeAction.Authorize.toString().equals(command.getAuthorizeAction())) {
			log.debug("Authorizing token '" + entry.getToken() + "' for user '" + loginUser.getName() + "'");
			
			// If the user clicked the Authorize button we authorize the token and redirect back.
			getDataStore().authorizeToken(entry, loginUser.getName());

			// If we're here then the entry has been authorized

			// redirect to callback
			if (!present(callback) || OUT_OF_BAND.equals(callback)) {
				return Views.OAUTH_AUTHORIZATION_SUCCESS;
			}
			
			callback = OAuth.addParameters(callback, OAuth.OAUTH_TOKEN, entry.getToken());
			// Add user_id to the callback
			callback = OAuth.addParameters(callback, OAUTH_HEADER_USER_ID, entry.getUserId());
			
			if (present(entry.getCallbackToken())) {
				callback = OAuth.addParameters(callback, OAuth.OAUTH_VERIFIER, entry.getCallbackToken());
			}
			return new ExtendedRedirectView(callback);
		} else if (AuthorizeAction.Deny.toString().equals(command.getAuthorizeAction())) {
			getDataStore().removeToken(entry);
			return Views.OAUTH_DENY;
		}

		return Views.OAUTH_AUTHORIZE;
	}

	@Override
	protected String getRequestAction() {
		return OAuthAction.authorize.name();
	}

}
