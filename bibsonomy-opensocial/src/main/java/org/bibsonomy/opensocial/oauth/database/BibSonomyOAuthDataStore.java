package org.bibsonomy.opensocial.oauth.database;

import java.util.Date;
import java.util.UUID;

import net.oauth.OAuthConsumer;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthServiceProvider;
import net.oauth.signature.RSA_SHA1;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.crypto.Crypto;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry.Type;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import static org.bibsonomy.util.ValidationUtils.present;

import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * {@link OAuthDataStore} implementation that is used during both 2 and 3-legged OAuth authorizations from Shindig.
 * 
 * FIXME: we should consider using the database for accessing consumer info only - 
 *        managing OAuth tokens in a map
 */
public class BibSonomyOAuthDataStore implements OAuthDataStore {
	// This needs to be long enough that an attacker can't guess it.  If the attacker can guess this
	// value before they exceed the maximum number of attempts, they can complete a session fixation
	// attack against a user.
	private static final int CALLBACK_TOKEN_LENGTH = 6;

	private static final String OAUTH_CONTAINER_NAME = "default";

	private static final String OAUTH_DOMAIN_NAME = "samplecontainer.com";

	/**
	 * database logic for accessing OAuth tokens
	 * FIXME: configure via spring
	 */
	IOAuthLogic authLogic = IbatisOAuthLogic.getInstance();

	/** 
	 * properties of our OAuth service provider 
	 * FIXME: configure via spring 
	 */
	private OAuthServiceProvider serviceProvider;

	@Inject
	public BibSonomyOAuthDataStore(@Named("shindig.oauth.base-url") String baseUrl) {
		this.serviceProvider = new OAuthServiceProvider(baseUrl + "requestToken", baseUrl + "authorize", baseUrl + "accessToken");
	}

	//------------------------------------------------------------------------
	// OAuthDataStore interface
	//------------------------------------------------------------------------
	/**
	 * Authorize the request token for the given user id.
	 *
	 * @param entry A valid OAuthEntry
	 * @param userId A user id
	 * @throws OAuthProblemException when the implementing class wants to control the error response
	 */
	public void authorizeToken(OAuthEntry entry, String userId) throws OAuthProblemException {
		if (present(entry) && present(userId)) {
			entry.setAuthorized(true);
			entry.setUserId(userId);
			if (entry.isCallbackUrlSigned()) {
				entry.setCallbackToken(Crypto.getRandomDigits(CALLBACK_TOKEN_LENGTH));
			}
			this.authLogic.updateProviderToken(entry);
		} else {
			throw new RuntimeException("Error updating token '"+entry.getToken()+"' for '"+entry.getUserId()+"'");
		}
	}

	/**
	 * convert the so far temporary request token to an authorized access token 
	 */
	public OAuthEntry convertToAccessToken(OAuthEntry entry) throws OAuthProblemException {
		if (!present(entry)) {
			throw new IllegalArgumentException("no OAuth entry given");
		}
		
		if (!OAuthEntry.Type.REQUEST.equals(entry.getType())) {
			throw new OAuthProblemException("Token must be a request token");
		}

	    OAuthEntry accessEntry = new OAuthEntry(entry);
	    
	    accessEntry.setUserId(entry.getUserId());

	    accessEntry.setToken(UUID.randomUUID().toString());
	    accessEntry.setTokenSecret(UUID.randomUUID().toString());

	    accessEntry.setType(OAuthEntry.Type.ACCESS);
	    accessEntry.setIssueTime(new Date());
	    
	    // remove the temporary request token
	    this.authLogic.deleteProviderToken(entry.getToken());
	    // add the authorized access token
	    this.authLogic.createProviderToken(accessEntry);

	    return accessEntry;
	}

	public void disableToken(OAuthEntry entry) {
		if (!present(entry)) {
			throw new IllegalArgumentException("no OAuth entry given");
		}

		entry.setType(Type.DISABLED);
		this.authLogic.updateProviderToken(entry);
	}

	/**
	 * Generate a valid requestToken for the given consumerKey
	 * 
	 * This is a temporary token which will be converted to an access token afterwards
	 * 
	 * TODO: we probably don't need to store the request token in the database but keep track 
	 *       of it via a hash map
	 * 
	 * @param consumerKey A valid consumer key
	 * @param signedCallbackUrl Callback URL sent from consumer, may be null.  If callbackUrl is not
	 *     null then the returned entry should have signedCallbackUrl set to true.
	 * @return An OAuthEntry containing a valid request token.
	 * @throws OAuthProblemException when the implementing class wants to control the error response
	 */
	public OAuthEntry generateRequestToken(String consumerKey, String oauthVersion, String signedCallbackUrl) throws OAuthProblemException {
		OAuthEntry entry = new OAuthEntry();
		entry.setAppId(consumerKey);
		entry.setConsumerKey(consumerKey);
		// FIXME: use prject home
		entry.setDomain(OAUTH_DOMAIN_NAME);
		// FIXME: configre this value
		entry.setContainer(OAUTH_CONTAINER_NAME);

		// TODO: how collision save are UUIDs? At this point we don't have the login user name at hand...
		entry.setToken(UUID.randomUUID().toString());
		entry.setTokenSecret(UUID.randomUUID().toString());

		entry.setType(OAuthEntry.Type.REQUEST);
		entry.setIssueTime(new Date());
		entry.setOauthVersion(oauthVersion);
		if (signedCallbackUrl != null) {
			entry.setCallbackUrlSigned(true);
			entry.setCallbackUrl(signedCallbackUrl);
		}

		this.authLogic.createProviderToken(entry);
		return entry;
	}

	/**
	 * Lookup consumers.  Generally this corresponds to an opensocial Application
	 * but could be abstracted in other ways.  If you have multiple containers you
	 * may want to include the container as part of the identifier.
	 *
	 * Your consumer object should have the key and secret, a link to your provider
	 * plus you should consider setting properties that correspond to the metadata
	 * in the opensocial app like icon, description, etc.
	 *
	 * Returning null will inform the client that the consumer key does not exist.  If you
	 * want to control the error response throw an OAuthProblemException
	 *
	 * @param consumerKey A valid, non-null ConsumerKey
	 * @return the consumer object corresponding to the specified key.
	 * @throws OAuthProblemException when the implementing class wants to signal errors
	 */
	public OAuthConsumer getConsumer(String consumerKey) throws OAuthProblemException {
		OAuthConsumerInfo consumerInfo = this.authLogic.readConsumer(consumerKey);
		if (!present(consumerInfo)) {
			return null;
		}

		// null below is for the callbackUrl, which we don't have in the db
		OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerInfo.getConsumerSecret(), serviceProvider);
		
		// set the public key
		if (present(consumerInfo.getKeyName())) {
			consumer.setProperty(consumerInfo.getKeyName(), consumerInfo.getConsumerSecret());
		}

		// Set some properties loosely based on the ModulePrefs of a gadget
		consumer.setProperty("title", consumerInfo.getTitle());
		consumer.setProperty("icon", consumerInfo.getIcon());
		consumer.setProperty("thumbnail", consumerInfo.getThumbnail());
		consumer.setProperty("summary", consumerInfo.getSummary());
		consumer.setProperty("description", consumerInfo.getDescription());

		return consumer;
	}

	/**
	 * Get the OAuthEntry that corresponds to the oauthToken.
	 *
	 * @param oauthToken a non-null oauthToken
	 * @return a valid OAuthEntry or null if no match
	 */
	public OAuthEntry getEntry(String oauthToken) {
		return this.authLogic.readProviderToken(oauthToken);
	}

	public SecurityToken getSecurityTokenForConsumerRequest(String consumerKey, String userId) throws OAuthProblemException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeToken(OAuthEntry entry) {
		// TODO Auto-generated method stub

	}

}
