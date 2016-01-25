/**
 * BibSonomy-OpenSocial - Implementation of the Opensocial specification and OAuth Security Handling
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import net.oauth.OAuth;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;
import net.oauth.signature.RSA_SHA1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;
import org.apache.shindig.gadgets.oauth.OAuthError;
import org.apache.shindig.gadgets.oauth.OAuthRequestException;
import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;
import org.apache.shindig.gadgets.oauth.OAuthStore.TokenInfo;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthParam;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthTokenIndex;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthTokenInfo;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthUserInfo;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * TODO: use {@link AbstractDatabaseManager}
 * 
 * @author fei
 */
public class IbatisOAuthLogic implements OAuthLogic {
	private static final Log log = LogFactory.getLog(IbatisOAuthLogic.class);
	
	/** Initialize iBatis layer. */
	private SqlMapClient sqlMap;

	private String defaultCallbackUrl;
	
	@Override
	public void createAuthentication(String gadgetUrl, String server, String consumerKey, String consumerSecret, KeyType keyType) {
		// TODO Auto-generated method stub
		throw new RuntimeException("METHOD NOT IMPLEMENTED");
	}

	@Override
	public ConsumerInfo readAuthentication(SecurityToken securityToken, String serviceName, OAuthServiceProvider provider) throws OAuthRequestException {
		OAuthConsumerInfo consumerParam = makeConsumerInfo(securityToken, serviceName);
		OAuthConsumerInfo consumerInfo = null;
		try {
			consumerInfo = (OAuthConsumerInfo) this.sqlMap.queryForObject("getAuthentication", consumerParam);
		} catch (SQLException e) {
			log.error("No consumer information found for '"+securityToken.getActiveUrl()+"' on '"+serviceName+"'");
		}
		
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
	}

	@Override
	public TokenInfo createToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName, TokenInfo tokenInfo) {
		final OAuthTokenIndex tokenIndex = makeTokenIndex(securityToken, serviceName);
		tokenIndex.setTokenSecret(tokenInfo.getTokenSecret());
		tokenIndex.setTokenExpireMillis(tokenInfo.getTokenExpireMillis());
		tokenIndex.setAccessToken(tokenInfo.getAccessToken());
		tokenIndex.setSessionHandle(tokenInfo.getSessionHandle());
		
		try {
			this.sqlMap.insert("setToken", tokenIndex);
		} catch (SQLException e) {
			log.error("Error setting token for viewer '"+tokenIndex.getUserId()+"' on gadget '"+tokenIndex.getGadgetUri()+"'");
		}
		
		return tokenInfo;
	}

	@Override
	public void deleteToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName) {
		final OAuthTokenIndex tokenIndex = makeTokenIndex(securityToken, serviceName);
		
		try {
			this.sqlMap.delete("removeToken", tokenIndex);
		} catch (SQLException e) {
			log.error("Error removing token for viewer '"+tokenIndex.getUserId()+"' on gadget '"+tokenIndex.getGadgetUri()+"'");
		}
	}

	@Override
	public TokenInfo readToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName) {
		final OAuthTokenIndex tokenIndex = makeTokenIndex(securityToken, serviceName);
		
		try {
			final OAuthTokenInfo tokenInfo = (OAuthTokenInfo) this.sqlMap.queryForObject("getToken", tokenIndex);
			// we have to construct the temporary parameter object as the TokenInfo class has no default constructor
			// FIXME: Ibatis supports pre-initialized parameter objects
			if (present(tokenInfo)) {
				return new TokenInfo(tokenInfo.getAccessToken(), tokenInfo.getTokenSecret(), tokenInfo.getSessionHandle(), tokenInfo.getTokenExpireMillis());
			}
		} catch (SQLException e) {
			log.error("Error fetching token for viewer '"+tokenIndex.getUserId()+"' on gadget '"+tokenIndex.getGadgetUri()+"'", e);
		}
		
		return null;
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
		try {
			this.sqlMap.insert("setProviderToken", entry);
		} catch (SQLException e) {
			log.error("Error creating provider token for '"+entry.getAppId()+"'", e);
		}
	}

	@Override
	public void createConsumer(OAuthConsumerInfo consumerInfo) {
		try {
			// if the key name is given, RSA is used for request signing
			if (present(consumerInfo.getKeyName())) {
				consumerInfo.setKeyType(KeyType.RSA_PRIVATE);
			}
			// if RSA is used for request signing, the consumer's public key is stored
			if (!present(consumerInfo.getKeyName()) && KeyType.RSA_PRIVATE.equals(consumerInfo.getKeyType())) {
				consumerInfo.setKeyName(RSA_SHA1.PUBLIC_KEY);
			}
			this.sqlMap.insert("setConsumerInfo", consumerInfo);
		} catch (SQLException e) {
			throw new RuntimeException("Error creating consumer info", e);
		}
	}
	
	@Override
	public OAuthConsumerInfo readConsumer(String consumerKey) {
		try {
			return (OAuthConsumerInfo ) this.sqlMap.queryForObject("getConsumerInfo", consumerKey);
		} catch (SQLException e) {
			log.error("Error fetching consumer info for consumer key '" + consumerKey + "'", e);
		}
		
		return null;
	}
	
	@Override
	public void deleteConsumer(String consumerKey) {
		try {
			this.sqlMap.delete("removeConsumerInfo", consumerKey);
		} catch (SQLException e) {
			log.error("Error removing consumerInfo for consumerKey '" + consumerKey + "'", e);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<OAuthConsumerInfo> listConsumers() {
		try {
			return this.sqlMap.queryForList("listConsumerInfo");
		} catch (SQLException e) {
			log.error("Error listing consumer info", e);
		}
		
		return null;
	}

	@Override
	public OAuthEntry readProviderToken(String oauthToken) {
		try {
			return (OAuthEntry) this.sqlMap.queryForObject("getProviderToken", oauthToken);
		} catch (SQLException e) {
			log.error("Error retrieving token details for token '"+oauthToken+"'", e);
		}
		return null;
	}

	@Override
	public void updateProviderToken(OAuthEntry entry) {
		try {
			this.sqlMap.insert("updateProviderToken", entry);
		} catch (SQLException e) {
			log.error("Error updating provider token for '"+entry.getAppId()+"'", e);
		}
	}
	
	@Override
	public void deleteProviderToken(String token) {
		try {
			this.sqlMap.delete("removeProviderToken", token);
		} catch (SQLException e) {
			log.error("Error removing token '"+token+"'", e);
		}
	}
	
	@Override
	public void removeSpecificAccessToken(String userName, String accessToken){
		final OAuthParam param = new OAuthParam(userName, accessToken);
		try{
			this.sqlMap.delete("removeSpecificAccessToken", param);
		} catch (SQLException e) {
			log.error("Error removing token '"+param.getAccessToken()+"'", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List <OAuthUserInfo> getOAuthUserApplication (String username) {
		try {
			 return this.sqlMap.queryForList("getUserInfo", username);
		} catch (SQLException e) {
			log.error("No user information found about OAuth for '"+username+"'");
		}
		return Collections.emptyList();
	}
	
	//------------------------------------------------------------------------
	// private helper
	//------------------------------------------------------------------------
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
	 * @param sqlMap the sqlMap to set
	 */
	public void setSqlMap(SqlMapClient sqlMap) {
		this.sqlMap = sqlMap;
	}
}
