/**
 * BibSonomy-OpenSocial - Implementation of the Opensocial specification and OAuth Security Handling
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.opensocial.oauth.database;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;
import org.apache.shindig.gadgets.oauth.OAuthError;
import org.apache.shindig.gadgets.oauth.OAuthRequestException;
import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;
import org.apache.shindig.gadgets.oauth.OAuthStore.TokenInfo;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthParam;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthTokenIndex;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthTokenInfo;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthUserInfo;

import net.oauth.OAuth;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;
import net.oauth.signature.RSA_SHA1;

/**
 * iBatis implementation of the {@link OAuthLogic}
 * 
 * @author fei
 */
public class IbatisOAuthLogic extends AbstractDatabaseManager implements OAuthLogic {
	
	private DBSessionFactory oauthSessionFactory;

	private String defaultCallbackUrl;
	
	private final DBSession createSession() {
		return this.oauthSessionFactory.getDatabaseSession();
	}
	
	@Override
	public void createAuthentication(String gadgetUrl, String server, String consumerKey, String consumerSecret, KeyType keyType) {
		// TODO Auto-generated method stub
		throw new RuntimeException("METHOD NOT IMPLEMENTED");
	}

	@Override
	public ConsumerInfo readAuthentication(SecurityToken securityToken, String serviceName, OAuthServiceProvider provider) throws OAuthRequestException {
		final OAuthConsumerInfo consumerParam = makeConsumerInfo(securityToken, serviceName);
		final DBSession session = this.createSession();
		try {
			final OAuthConsumerInfo consumerInfo = this.queryForObject("getAuthentication", OAuthConsumerInfo.class, consumerParam, session);
			if (!present(consumerInfo)) {
				throw new OAuthRequestException(OAuthError.INVALID_PARAMETER, "No key for gadget " + securityToken.getAppUrl() + " and service " + serviceName);
			}
			
			final BasicOAuthStoreConsumerKeyAndSecret cks = new BasicOAuthStoreConsumerKeyAndSecret(
						consumerInfo.getConsumerKey(),
						consumerInfo.getConsumerSecret(),
						consumerInfo.getKeyType(), 
						consumerInfo.getKeyName(), 
						consumerInfo.getCallbackUrl()
				);
			final OAuthConsumer consumer;
			if (cks.getKeyType() == KeyType.RSA_PRIVATE) {
				consumer = new OAuthConsumer(null, cks.getConsumerKey(), null, provider);
				// The oauth.net java code has lots of magic.  By setting this property here, code thousands
				// of lines away knows that the consumerSecret value in the consumer should be treated as
				// an RSA private key and not an HMAC key.
				consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.RSA_SHA1);
				consumer.setProperty(RSA_SHA1.PRIVATE_KEY, cks.getConsumerSecret());
			} else {
				consumer = new OAuthConsumer(null, cks.getConsumerKey(), cks.getConsumerSecret(), provider);
				consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
			}
			final String callback = (cks.getCallbackUrl() != null ? cks.getCallbackUrl() : this.defaultCallbackUrl);
			return new ConsumerInfo(consumer, cks.getKeyName(), callback);
		} finally {
			session.close();
		}
	}

	@Override
	public TokenInfo createToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName, TokenInfo tokenInfo) {
		final OAuthTokenIndex tokenIndex = makeTokenIndex(securityToken, serviceName);
		tokenIndex.setTokenSecret(tokenInfo.getTokenSecret());
		tokenIndex.setTokenExpireMillis(tokenInfo.getTokenExpireMillis());
		tokenIndex.setAccessToken(tokenInfo.getAccessToken());
		tokenIndex.setSessionHandle(tokenInfo.getSessionHandle());
		
		final DBSession session = this.createSession();
		try {
			this.insert("setToken", tokenIndex, session);
			return tokenInfo;
		} finally {
			session.close();
		}
	}

	@Override
	public void deleteToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName) {
		final OAuthTokenIndex tokenIndex = makeTokenIndex(securityToken, serviceName);
		
		final DBSession session = this.createSession();
		try {
			this.delete("removeToken", tokenIndex, session);
		} finally {
			session.close();
		}
	}

	@Override
	public TokenInfo readToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName) {
		final OAuthTokenIndex tokenIndex = makeTokenIndex(securityToken, serviceName);
		
		final DBSession session = this.createSession();
		try {
			final OAuthTokenInfo tokenInfo = this.queryForObject("getToken", tokenIndex, OAuthTokenInfo.class, session);
			// we have to construct the temporary parameter object as the TokenInfo class has no default constructor
			// FIXME: Ibatis supports pre-initialized parameter objects
			if (present(tokenInfo)) {
				return new TokenInfo(tokenInfo.getAccessToken(), tokenInfo.getTokenSecret(), tokenInfo.getSessionHandle(), tokenInfo.getTokenExpireMillis());
			}
			return null;
		} finally {
			session.close();
		}
	}

	private static OAuthTokenIndex makeTokenIndex(SecurityToken securityToken, String serviceName) {
		final OAuthTokenIndex tokenIndex = new OAuthTokenIndex();
		tokenIndex.setGadgetUri(securityToken.getAppUrl());
		tokenIndex.setServiceName(serviceName);
		tokenIndex.setModuleId(securityToken.getModuleId());
		tokenIndex.setUserId(securityToken.getViewerId());
		return tokenIndex;
	}
	
	@Override
	public void createProviderToken(OAuthEntry entry) {
		final DBSession session = createSession();
		try {
			this.insert("setProviderToken", entry, session);
		} finally {
			session.close();
		}
	}

	@Override
	public void createConsumer(OAuthConsumerInfo consumerInfo) {
		final DBSession session = this.createSession();
		try {
			// if the key name is given, RSA is used for request signing
			if (present(consumerInfo.getKeyName())) {
				consumerInfo.setKeyType(KeyType.RSA_PRIVATE);
			}
			// if RSA is used for request signing, the consumer's public key is stored
			if (!present(consumerInfo.getKeyName()) && KeyType.RSA_PRIVATE.equals(consumerInfo.getKeyType())) {
				consumerInfo.setKeyName(RSA_SHA1.PUBLIC_KEY);
			}
			this.insert("setConsumerInfo", consumerInfo, session);
		} finally {
			session.close();
		}
	}
	
	@Override
	public OAuthConsumerInfo readConsumer(String consumerKey) {
		final DBSession session = this.createSession();
		try {
			return this.queryForObject("getConsumerInfo", consumerKey, OAuthConsumerInfo.class, session);
		} finally {
			session.close();
		}
	}
	
	@Override
	public void deleteConsumer(String consumerKey) {
		final DBSession session = this.createSession();
		try {
			this.delete("removeConsumerInfo", consumerKey, session);
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<OAuthConsumerInfo> listConsumers() {
		final DBSession session = this.createSession();
		try {
			return this.queryForList("listConsumerInfo", null, OAuthConsumerInfo.class, session);
		} finally {
			session.close();
		}
	}

	@Override
	public OAuthEntry readProviderToken(String oauthToken) {
		final DBSession session = this.createSession();
		try {
			return this.queryForObject("getProviderToken", oauthToken, OAuthEntry.class, session);
		} finally {
			session.close();
		}
	}

	@Override
	public void updateProviderToken(OAuthEntry entry) {
		final DBSession session = this.createSession();
		try {
			this.update("updateProviderToken", entry, session);
		} finally {
			session.close();
		}
	}
	
	@Override
	public void deleteProviderToken(String token) {
		final DBSession session = this.createSession();
		try {
			this.delete("removeProviderToken", token, session);
		} finally {
			session.close();
		}
	}
	
	@Override
	public void removeSpecificAccessToken(String userName, String accessToken) {
		final DBSession session = this.createSession();
		final OAuthParam param = new OAuthParam(userName, accessToken);
		try {
			this.delete("removeSpecificAccessToken", param, session);
		} finally {
			session.close();
		}
	}
	
	@Override
	public List <OAuthUserInfo> getOAuthUserApplication(String username) {
		final DBSession session = this.createSession();
		try {
			return this.queryForList("getUserInfo", username, OAuthUserInfo.class, session);
		} finally {
			session.close();
		}
	}
	
	private static OAuthConsumerInfo makeConsumerInfo(SecurityToken securityToken, String serviceName) {
		final OAuthConsumerInfo consumerInfo = new OAuthConsumerInfo();
		consumerInfo.setGadgetUrl(securityToken.getAppUrl());
		consumerInfo.setServiceName(serviceName);
		return consumerInfo;
	}

	/**
	 * @param defaultCallbackUrl the defaultCallbackUrl to set
	 */
	public void setDefaultCallbackUrl(String defaultCallbackUrl) {
		this.defaultCallbackUrl = defaultCallbackUrl;
	}

	/**
	 * @param oauthSessionFactory the oauthSessionFactory to set
	 */
	public void setOauthSessionFactory(DBSessionFactory oauthSessionFactory) {
		this.oauthSessionFactory = oauthSessionFactory;
	}
}
