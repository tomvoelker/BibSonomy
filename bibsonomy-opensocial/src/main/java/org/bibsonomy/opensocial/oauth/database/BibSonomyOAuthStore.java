package org.bibsonomy.opensocial.oauth.database;

import net.oauth.OAuthServiceProvider;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.GadgetException.Code;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret;
import org.apache.shindig.gadgets.oauth.OAuthRequestException;
import org.apache.shindig.gadgets.oauth.OAuthStore;

/**
 * TODO: remove BibSonomy from class name
 * Class for managing OAuth information for accessing external providers where
 * BibSonomy acts as a consumer.
 * 
 * @see OAuthStore
 * 
 * @author fei
 */
public class BibSonomyOAuthStore implements OAuthStore {
	/** singleton pattern */
	private static BibSonomyOAuthStore instance;
	
	public static BibSonomyOAuthStore getInstance() {
		if (instance==null) {
			instance = new BibSonomyOAuthStore();
		};
		
		return instance;
	}
	
	// FIXME: configure via spring
	private final IOAuthLogic authLogic = IbatisOAuthLogic.getInstance();
	private String defaultCallbackUrl;
	private BasicOAuthStoreConsumerKeyAndSecret defaultKey;
	
	@Override
	public ConsumerInfo getConsumerKeyAndSecret(final SecurityToken securityToken, final String serviceName, final OAuthServiceProvider provider)throws GadgetException {
		try {
			return this.authLogic.readAuthentication(securityToken, serviceName, provider);
		} catch (final OAuthRequestException e) {
			throw new GadgetException(Code.INVALID_PARAMETER, e.getMessage());
		}
	}

	@Override
	public TokenInfo getTokenInfo(final SecurityToken securityToken, final ConsumerInfo consumerInfo, final String serviceName, final String tokenName) throws GadgetException {
		return this.authLogic.readToken(securityToken, consumerInfo, serviceName, tokenName);
	}

	@Override
	public void removeToken(final SecurityToken securityToken, final ConsumerInfo consumerInfo, final String serviceName, final String tokenName) throws GadgetException {
		this.authLogic.deleteToken(securityToken, consumerInfo, serviceName, tokenName);
	}

	@Override
	public void setTokenInfo(final SecurityToken securityToken, final ConsumerInfo consumerInfo, final String serviceName, final String tokenName, final TokenInfo tokenInfo) throws GadgetException {
		this.authLogic.createToken(securityToken, consumerInfo, serviceName, tokenName, tokenInfo);
	}

	public void setDefaultCallbackUrl(final String defaultCallbackUrl) {
		this.defaultCallbackUrl = defaultCallbackUrl;
	}

	public String getDefaultCallbackUrl() {
		return defaultCallbackUrl;
	}

	public void setDefaultKey(final BasicOAuthStoreConsumerKeyAndSecret defaultKey) {
		this.defaultKey = defaultKey;
	}

	public BasicOAuthStoreConsumerKeyAndSecret getDefaultKey() {
		return defaultKey;
	}

	
}
