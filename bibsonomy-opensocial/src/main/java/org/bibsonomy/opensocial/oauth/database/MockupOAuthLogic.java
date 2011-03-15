package org.bibsonomy.opensocial.oauth.database;

import net.oauth.OAuth;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;
import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;
import org.apache.shindig.gadgets.oauth.OAuthStore.TokenInfo;

public class MockupOAuthLogic implements IOAuthLogic {
	private static Log log = LogFactory.getLog(MockupOAuthLogic.class);
	
	TokenInfo lastToken;

	public void createAuthentication(String gadgetUrl, String server,
			String consumerKey, String consumerSecret, KeyType keyType) {
	}

	public ConsumerInfo readAuthentication(SecurityToken securityToken, String serviceName, OAuthServiceProvider provider) {
		log.info("Retrieving authentication for '"+securityToken.getAppUrl()+"' and server '"+serviceName+"'"+(provider.toString()));
		
		BasicOAuthStoreConsumerKeyAndSecret cks = new BasicOAuthStoreConsumerKeyAndSecret(serviceName, "mockupKey", org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType.HMAC_SYMMETRIC, "mockupName", "mockupCallback");
		OAuthConsumer consumer = null;
		consumer = new OAuthConsumer(null, cks.getConsumerKey(), cks.getConsumerSecret(), provider);
		consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
		return new ConsumerInfo(consumer, cks.getKeyName(), "mockupCallback");
	}


	public TokenInfo createToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName, TokenInfo tokenInfo) {
		log.info("Creating token for "+securityToken.getViewerId());
		this.lastToken = tokenInfo;
		return tokenInfo;
	}

	public void deleteToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName) {
		log.info("Deleting token for "+securityToken.getViewerId());
	}

	public TokenInfo readToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName) {
		log.info("Reading token for "+securityToken.getViewerId());
		return this.lastToken;
	}
	
}
