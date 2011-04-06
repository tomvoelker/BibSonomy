package org.bibsonomy.opensocial.oauth.database;

import net.oauth.OAuthServiceProvider;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret;
import org.apache.shindig.gadgets.oauth.OAuthStore;

public class BibSonomyOAuthStore implements OAuthStore {
	
	// FIXME: configure via spring
	IOAuthLogic authLogic = IbatisOAuthLogic.getInstance();
	
	private String defaultCallbackUrl;
	private BasicOAuthStoreConsumerKeyAndSecret defaultKey;
	
	public ConsumerInfo getConsumerKeyAndSecret(SecurityToken securityToken, String serviceName, OAuthServiceProvider provider)throws GadgetException {
		return this.authLogic.readAuthentication(securityToken, serviceName, provider);
	}

	public TokenInfo getTokenInfo(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName) throws GadgetException {
		return this.authLogic.readToken(securityToken, consumerInfo, serviceName, tokenName);
	}

	public void removeToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName) throws GadgetException {
		this.authLogic.deleteToken(securityToken, consumerInfo, serviceName, tokenName);
	}

	public void setTokenInfo(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName, TokenInfo tokenInfo) throws GadgetException {
		this.authLogic.createToken(securityToken, consumerInfo, serviceName, tokenName, tokenInfo);
	}

	public void setDefaultCallbackUrl(String defaultCallbackUrl) {
		this.defaultCallbackUrl = defaultCallbackUrl;
	}

	public String getDefaultCallbackUrl() {
		return defaultCallbackUrl;
	}

	public void setDefaultKey(BasicOAuthStoreConsumerKeyAndSecret defaultKey) {
		this.defaultKey = defaultKey;
	}

	public BasicOAuthStoreConsumerKeyAndSecret getDefaultKey() {
		return defaultKey;
	}

	
}
