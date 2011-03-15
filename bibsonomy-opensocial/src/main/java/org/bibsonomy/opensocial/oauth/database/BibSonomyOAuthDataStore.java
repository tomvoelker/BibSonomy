package org.bibsonomy.opensocial.oauth.database;

import net.oauth.OAuthConsumer;
import net.oauth.OAuthProblemException;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;

public class BibSonomyOAuthDataStore implements OAuthDataStore {

	public void authorizeToken(OAuthEntry entry, String userId)
			throws OAuthProblemException {
		// TODO Auto-generated method stub
		
	}

	public OAuthEntry convertToAccessToken(OAuthEntry entry)
			throws OAuthProblemException {
		// TODO Auto-generated method stub
		return null;
	}

	public void disableToken(OAuthEntry entry) {
		// TODO Auto-generated method stub
		
	}

	public OAuthEntry generateRequestToken(String consumerKey,
			String oauthVersion, String signedCallbackUrl)
			throws OAuthProblemException {
		// TODO Auto-generated method stub
		return null;
	}

	public OAuthConsumer getConsumer(String consumerKey)
			throws OAuthProblemException {
		// TODO Auto-generated method stub
		return null;
	}

	public OAuthEntry getEntry(String oauthToken) {
		// TODO Auto-generated method stub
		return null;
	}

	public SecurityToken getSecurityTokenForConsumerRequest(String consumerKey,
			String userId) throws OAuthProblemException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeToken(OAuthEntry entry) {
		// TODO Auto-generated method stub
		
	}

}
