package org.bibsonomy.webapp.controller.opensocial;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.ServletContext;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.OAuth.Parameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.servlet.GuiceServletContextListener;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.bibsonomy.webapp.command.opensocial.OAuthCommand;
import org.bibsonomy.webapp.command.opensocial.OAuthCommand.AuthorizeAction;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.opensocial.BibSonomyOAuthValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.ServletContextAware;

import com.google.inject.Injector;

/**
 * This controller implements the OAuth endpoints described in RFC 5849, section 2:
 * 
 * Temporary Credential Request ("requestToken")
 *        The endpoint used by the client to obtain a set of temporary
 *        credentials as described in Section 2.1.
 *
 *  Resource Owner Authorization ("authorize")
 *        The endpoint to which the resource owner is redirected to grant
 *        authorization as described in Section 2.2.
 * 
 *  Token Request ("accessToken")
 *        The endpoint used by the client to request a set of token
 *        credentials using the set of temporary credentials as described
 *        in Section 2.3.
 * 
 * 
 *    The original community specification used a somewhat different
 *    terminology that maps to this specifications [RFC 5849] as follows (original
 *    community terms provided on left):
 * 
 *    Consumer:  client
 * 
 *    Service Provider:  server
 * 
 *    User:  resource owner
 * 
 *    Consumer Key and Secret:  client credentials
 * 
 *    Request Token and Secret:  temporary credentials
 * 
 *    Access Token and Secret:  token credentials
 * 
 *
 * 
 * @author fei
 * @version $Id$
 */
public class OAuthProtocolController implements ValidationAwareController<OAuthCommand>, ServletContextAware {
	private static final Log log = LogFactory.getLog(OAuthProtocolController.class);
	
	/**
	 * RFC 5849, section 2.1: 
	 *            ' If the client is unable to receive callbacks or a
     *              callback URI has been established via other means,
     *              the parameter value MUST be set to "oob" '
	 */
	private static final String OUT_OF_BAND = "oob";

	/** supported OAuth actions */
	public enum OAuthAction { accessToken, authorize, requestToken };

	/**
	 * requested action to perform
	 * FIXME: this is a hack: we cannot use URL-rewriting as the requests are signed 
	 */
	private String requestAction;

	/** used for obtaining the OAuth request parameters */
	private RequestLogic requestLogic;

	/** the servlet context */
	private ServletContext servletContext;

	/** shindig's Guice injector */
	private Injector injector;

	/** data store for managing OAuth tokens */
	private OAuthDataStore dataStore;

	/** validates incoming OAuth requests mainly by verifying the request's signature */
	public static final OAuthValidator VALIDATOR = new SimpleOAuthValidator();


	/** 
	 * controller initiallization after all properties were set via spring
	 */
	public void init() {
		this.injector  = (Injector) this.servletContext.getAttribute(GuiceServletContextListener.INJECTOR_ATTRIBUTE);
		this.dataStore = injector.getInstance(OAuthDataStore.class);
	}

	//------------------------------------------------------------------------
	// ValidationAwareController interface
	//------------------------------------------------------------------------
	@Override
	public Validator<OAuthCommand> getValidator() {
		return new BibSonomyOAuthValidator();
	}

	@Override
	public boolean isValidationRequired(OAuthCommand command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OAuthCommand instantiateCommand() {
		return new OAuthCommand();
	}

	@Override
	public View workOn(OAuthCommand command) {
		if (!present(requestAction)) {
			throw new RuntimeException("Invalid OAuth action requested");
		}
		String action = requestAction;

		// retrieve the log in user
		User loginUser = AuthenticationUtils.getUser();

		// dispatch
		View view = null;
		try {
			if (OAuthAction.requestToken.name().equals(action)) {
				view = createRequestToken(command, loginUser);
			} else if (OAuthAction.authorize.name().equals(action)) {
				view = authorizeRequestToken(command, loginUser);
			} else if (OAuthAction.accessToken.name().equals(action)) {
				view = createAccessToken(command, loginUser);
			};
		} catch (IOException e) {
			throw new RuntimeException("Error processing OAuth request '"+action+"'", e);
		} catch (OAuthException e) {
			throw new RuntimeException("Error processing OAuth request '"+action+"'", e);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Error processing OAuth request '"+action+"'", e);
		}
		if (!present(view) ){
			throw new RuntimeException("Invalid OAuth action requested");
		}

		return view;
	}
	
	//------------------------------------------------------------------------
	// OAuth protocol end point implementation
	//------------------------------------------------------------------------
	/**
	 * The request token (called "temporary credentials" in RFC 5849) is used for identifying
	 * a continuing authorization process and is transformed to a token credential after a successful
	 * authorization by the resource owner
	 *  
	 * @param command
	 * @param loginUser
	 * 
	 * @return temporary credentials for obtaining token credentials after authorization
	 * 
	 * @throws IOException
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	private View createRequestToken(OAuthCommand command, User loginUser) throws IOException, OAuthException, URISyntaxException {
		// extract the OAuth parameters from the request
		OAuthMessage requestMessage = this.requestLogic.getOAuthMessage(null);

		// get the mandatory consumer key which identifies the requesting client 
		String consumerKey = requestMessage.getConsumerKey();

		if (consumerKey == null) {
			OAuthProblemException e = new OAuthProblemException(OAuth.Problems.PARAMETER_ABSENT);
			e.setParameter(OAuth.Problems.OAUTH_PARAMETERS_ABSENT, OAuth.OAUTH_CONSUMER_KEY);
			throw e;
		}
		
		// check and retrieve the shared secret for the requesting client
		OAuthConsumer consumer = dataStore.getConsumer(consumerKey);

		if (!present(consumer)) {
			throw new OAuthProblemException(OAuth.Problems.CONSUMER_KEY_UNKNOWN);
		}
		
		// validate the OAuth request (i.e. verify the request's signature)
		OAuthAccessor accessor = new OAuthAccessor(consumer);
		VALIDATOR.validateMessage(requestMessage, accessor);

		// Get the client's callback URL (RFC 5849, Section 2.1)
		// If the client is unable to receive callbacks or a
        // callback URI has been established via other means,
        // the parameter value MUST be set to "oob"
		String callback = requestMessage.getParameter(OAuth.OAUTH_CALLBACK);
		if (!present(callback)) {
			// see if the consumer has a callback
			callback = consumer.callbackURL;
		}
		if (!present(callback)) {
			callback = OUT_OF_BAND;
		}

		// generate request_token and secret
		OAuthEntry entry = dataStore.generateRequestToken(consumerKey, requestMessage.getParameter(OAuth.OAUTH_VERSION), callback);
		
		List<Parameter> responseParams = OAuth.newList(OAuth.OAUTH_TOKEN, entry.getToken(), OAuth.OAUTH_TOKEN_SECRET, entry.getTokenSecret());
		if (present(callback)) {
			responseParams.add(new Parameter(OAuth.OAUTH_CALLBACK_CONFIRMED, "true"));
		}

		// return the temporary request token
		command.setResponseString(OAuth.formEncode(responseParams));
		return Views.OAUTH_RESPONSE;
	}

	/**
	 * authorize a given temporary credential ("request token")
	 * 
	 * @param command
	 * @param loginUser
	 * @return
	 * 
	 * @throws OAuthException
	 * @throws IOException
	 */
	private View authorizeRequestToken(OAuthCommand command, User loginUser) throws OAuthException, IOException {
		// extract the OAuth parameters from the request
		OAuthMessage requestMessage = this.requestLogic.getOAuthMessage(null);

		// retrieve the previously generated temporary credentials corresponding to the given OAuth token
		if (!present(requestMessage.getToken())) {
			throw new OAuthException("Authentication token not found");
		}
		OAuthEntry entry = dataStore.getEntry(requestMessage.getToken());

		if (!present(entry)) {
			throw new OAuthException("OAuth entry not found");
		}

		OAuthConsumer consumer = dataStore.getConsumer(entry.getConsumerKey());

		// Extremely rare case where consumer dissappears
		if (!present(consumer)) {
			throw new OAuthException("consumer for entry not found");
		}

		// The token is disabled if you try to convert to an access token prior to authorization
		if (entry.getType() == OAuthEntry.Type.DISABLED) {
			throw new OAuthException("This token is disabled, please reinitate login");
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
		if (!entry.isAuthorized() && !AuthorizeAction.Authorize.equals(command.getAuthorizeAction())) {
			return Views.OAUTH_AUTHORIZE;
		}

		// If user clicked on the Authorize button then we're good.
		if ( AuthorizeAction.Authorize.equals(command.getAuthorizeAction()) ) {
			// If the user clicked the Authorize button we authorize the token and redirect back.
			dataStore.authorizeToken(entry, loginUser.getName());

			// If we're here then the entry has been authorized

			// redirect to callback
			if (!present(callback) || OUT_OF_BAND.equals(callback)) {
				return Views.OAUTH_AUTHORIZATION_SUCCESS;
			} else {
				callback = OAuth.addParameters(callback, OAuth.OAUTH_TOKEN, entry.getToken());
				// Add user_id to the callback
				callback = OAuth.addParameters(callback, "user_id", entry.getUserId());
				if (present(entry.getCallbackToken())) {
					callback = OAuth.addParameters(callback, OAuth.OAUTH_VERIFIER, entry.getCallbackToken());
				}

				return new ExtendedRedirectView(callback);
			}
		} else if (AuthorizeAction.Deny.equals(command.getAuthorizeAction())) {
			dataStore.removeToken(entry);
		}

		return Views.OAUTH_AUTHORIZE;
	}

	/**
	 * Hand out an access token if the consumer key and secret are valid and the user authorized 
	 * the requestToken
	 * 
	 * @param command
	 * @param loginUser 
	 * @return
	 * @throws URISyntaxException 
	 * @throws OAuthException 
	 * @throws IOException 
	 */
	private View createAccessToken(OAuthCommand command, User loginUser) throws IOException, OAuthException, URISyntaxException {
		// extract the OAuth parameters from the request
		OAuthMessage requestMessage = this.requestLogic.getOAuthMessage(null);

		// obtain the corresponding token credential
		OAuthEntry entry = getValidatedEntry(requestMessage);
		if (!present(entry.getUserId())) {
			log.error("No username given for accessing the OAuth token.");
		}
		
		if (!present(entry)) {
			throw new OAuthProblemException(OAuth.Problems.TOKEN_REJECTED);
		}

		if (present(entry.getCallbackToken())) {
			// We're using the fixed protocol
			String clientCallbackToken = requestMessage.getParameter(OAuth.OAUTH_VERIFIER);
			if (!entry.getCallbackToken().equals(clientCallbackToken)) {
				dataStore.disableToken(entry);
				throw new AccessDeniedException("This token is not authorized");
			}
		} else if (!entry.isAuthorized()) {
			// Old protocol.  Catch consumers trying to convert a token to one that's not authorized
			dataStore.disableToken(entry); 
			throw new AccessDeniedException("This token is not authorized");
		}
		
	    // turn request token into access token
	    OAuthEntry accessEntry = dataStore.convertToAccessToken(entry);

		command.setResponseString(OAuth.formEncode(OAuth.newList(
                OAuth.OAUTH_TOKEN, accessEntry.getToken(),
                OAuth.OAUTH_TOKEN_SECRET, accessEntry.getTokenSecret(),
                "user_id", entry.getUserId())));
		return Views.OAUTH_RESPONSE;
	}

	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * tries to obtain the corresponding token credential for the given OAuth request
	 * 
	 * @param requestMessage
	 * 
	 * @return a validated OAuth token credential ("access token")
	 * 
	 * @throws IOException
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	private OAuthEntry getValidatedEntry(OAuthMessage requestMessage) throws IOException, OAuthException, URISyntaxException {

		OAuthEntry entry = dataStore.getEntry(requestMessage.getToken());
		if (!present(entry)) {
			throw new OAuthProblemException(OAuth.Problems.TOKEN_REJECTED);
		}

		// only (temporary) request tokens may be transformed to authorized access tokens
		if (entry.getType() != OAuthEntry.Type.REQUEST) {
			throw new OAuthProblemException(OAuth.Problems.TOKEN_USED);
		}

		if (entry.isExpired()) {
			throw new OAuthProblemException(OAuth.Problems.TOKEN_EXPIRED);
		}

		// find consumer key, compare with supplied value, if present.

		if  (!present(requestMessage.getConsumerKey())) {
			OAuthProblemException e = new OAuthProblemException(OAuth.Problems.PARAMETER_ABSENT);
			e.setParameter(OAuth.Problems.OAUTH_PARAMETERS_ABSENT, OAuth.OAUTH_CONSUMER_KEY);
			throw e;
		}

		// check whether the shared secrect between the client and the server match
		String consumerKey = entry.getConsumerKey();
		if (!consumerKey.equals(requestMessage.getConsumerKey())) {
			throw new OAuthProblemException(OAuth.Problems.CONSUMER_KEY_REFUSED);
		}

		OAuthConsumer consumer = dataStore.getConsumer(consumerKey);

		if (!present(consumer)) {
			throw new OAuthProblemException(OAuth.Problems.CONSUMER_KEY_UNKNOWN);
		}

		OAuthAccessor accessor = new OAuthAccessor(consumer);

		accessor.requestToken = entry.getToken();
		accessor.tokenSecret  = entry.getTokenSecret();

		// verify the request's signature
		VALIDATOR.validateMessage(requestMessage, accessor);

		return entry;
	}

	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	public RequestLogic getRequestLogic() {
		return requestLogic;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setRequestAction(String requestAction) {
		this.requestAction = requestAction;
	}

	public String getRequestAction() {
		return requestAction;
	}

}
