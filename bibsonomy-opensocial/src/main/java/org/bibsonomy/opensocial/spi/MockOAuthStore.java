package org.bibsonomy.opensocial.spi;

import net.oauth.OAuthServiceProvider;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.oauth.OAuthStore;

public class MockOAuthStore implements OAuthStore {

	public ConsumerInfo getConsumerKeyAndSecret(SecurityToken securityToken,
			String serviceName, OAuthServiceProvider provider)
			throws GadgetException {
		// TODO Auto-generated method stub
		return null;
	}

	public TokenInfo getTokenInfo(SecurityToken securityToken,
			ConsumerInfo consumerInfo, String serviceName, String tokenName)
			throws GadgetException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeToken(SecurityToken securityToken,
			ConsumerInfo consumerInfo, String serviceName, String tokenName)
			throws GadgetException {
		// TODO Auto-generated method stub
		
	}

	public void setTokenInfo(SecurityToken securityToken,
			ConsumerInfo consumerInfo, String serviceName, String tokenName,
			TokenInfo tokenInfo) throws GadgetException {
		// TODO Auto-generated method stub
		
	}

}
