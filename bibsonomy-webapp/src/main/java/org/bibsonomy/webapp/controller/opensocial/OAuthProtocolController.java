package org.bibsonomy.webapp.controller.opensocial;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletException;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.opensocial.OAuthCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ResponseLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

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
public abstract class OAuthProtocolController implements MinimalisticController<OAuthCommand> {
	private static final Log log = LogFactory.getLog(OAuthProtocolController.class);
	
	/**
	 * RFC 5849, section 2.1: 
	 *            ' If the client is unable to receive callbacks or a
     *              callback URI has been established via other means,
     *              the parameter value MUST be set to "oob" '
	 */
	public static final String OUT_OF_BAND = "oob";

	/** name of the user name's OAuth parameter */
	public static final String OAUTH_HEADER_USER_ID = "user_id";

	/** supported OAuth actions */
	public enum OAuthAction { accessToken, authorize, requestToken };

	/** used for obtaining the OAuth request parameters */
	protected RequestLogic requestLogic;
	
	/** used for handling OAuth error messages */
	private ResponseLogic responseLogic;
	
	/** project home for setting the correct realm in error responses */
	private String projectHome;

	/** data store for managing OAuth tokens */
	protected OAuthDataStore dataStore;

	/** validates incoming OAuth requests mainly by verifying the request's signature */
	public static final OAuthValidator VALIDATOR = new SimpleOAuthValidator();

	//------------------------------------------------------------------------
	// MinimalisticController interface
	//------------------------------------------------------------------------
	@Override
	public OAuthCommand instantiateCommand() {
		return new OAuthCommand();
	}

	@Override
	public View workOn(final OAuthCommand command) {
		if (!present(this.getDataStore())) {
			throw new RuntimeException("OAuth not enables.");
		}

		// retrieve the log in user
		final User loginUser = command.getContext().getLoginUser();

		// dispatch
		View view = null;
		try {
			view = this.doWorkOn(command, loginUser);
		} catch (final IOException e) {
			throw new RuntimeException("Error processing OAuth request '"+this.getRequestAction()+"'", e);
		} catch (final OAuthException e) {
			this.handleException(e);
			command.setResponseString(e.getMessage());
			return Views.OAUTH_RESPONSE;
		} catch (final URISyntaxException e) {
			throw new RuntimeException("Error processing OAuth request '"+this.getRequestAction()+"'", e);
		}
		if (!present(view) ){
			throw new RuntimeException("Invalid OAuth action requested");
		}

		return view;
	}
	
	//------------------------------------------------------------------------
	// abstract interface
	//------------------------------------------------------------------------
	/**
	 * actually implement one of the protocol actions 
	 * 'requestToken', 'authorize' and 'accessToken'
	 *  
	 * @param command
	 * @param loginUser
	 * @return
	 */
	protected abstract View doWorkOn(OAuthCommand command, User loginUser) throws IOException, OAuthException, URISyntaxException;

	/**
	 * get the name of the implemented protocoll action
	 * 
	 * @return
	 */
	protected abstract String getRequestAction();


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
	protected OAuthEntry getValidatedEntry(final OAuthMessage requestMessage) throws IOException, OAuthException, URISyntaxException {

		final OAuthEntry entry = this.getDataStore().getEntry(requestMessage.getToken());
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
			final OAuthProblemException e = new OAuthProblemException(OAuth.Problems.PARAMETER_ABSENT);
			e.setParameter(OAuth.Problems.OAUTH_PARAMETERS_ABSENT, OAuth.OAUTH_CONSUMER_KEY);
			throw e;
		}

		// check whether the shared secrect between the client and the server match
		final String consumerKey = entry.getConsumerKey();
		if (!consumerKey.equals(requestMessage.getConsumerKey())) {
			throw new OAuthProblemException(OAuth.Problems.CONSUMER_KEY_REFUSED);
		}

		final OAuthConsumer consumer = this.getDataStore().getConsumer(consumerKey);

		if (!present(consumer)) {
			throw new OAuthProblemException(OAuth.Problems.CONSUMER_KEY_UNKNOWN);
		}

		final OAuthAccessor accessor = new OAuthAccessor(consumer);

		accessor.requestToken = entry.getToken();
		accessor.tokenSecret  = entry.getTokenSecret();

		// verify the request's signature
		VALIDATOR.validateMessage(requestMessage, accessor);

		return entry;
	}
	
	/** 
	 * handle OAuth exceptions
	 * 
	 * @param e
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
    public void handleException(final Exception e) {
   		try {
   			final String realm = present(this.projectHome)?this.projectHome:this.requestLogic.getHostInetAddress();
			this.responseLogic.handleOAuthException(e, realm, false);
		} catch (final IOException ex) {
			log.error("Error handling OAuth exception.", e);
		} catch (final ServletException ex) {
			log.error("Error handling OAuth exception.", e);
		}
    }
    
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}

	public void setResponseLogic(final ResponseLogic responseLogic) {
		this.responseLogic = responseLogic;
	}

	public ResponseLogic getResponseLogic() {
		return this.responseLogic;
	}

	public void setProjectHome(final String projectHome) {
		this.projectHome = projectHome;
	}

	public String getProjectHome() {
		return this.projectHome;
	}

	public void setDataStore(final OAuthDataStore dataStore) {
		this.dataStore = dataStore;
	}

	public OAuthDataStore getDataStore() {
		return this.dataStore;
	}

}
