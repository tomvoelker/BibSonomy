package org.bibsonomy.opensocial.oauth.database;

import java.io.Reader;
import java.sql.SQLException;

import net.oauth.OAuth;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;
import net.oauth.signature.RSA_SHA1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;
import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;
import org.apache.shindig.gadgets.oauth.OAuthStore.TokenInfo;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthTokenIndex;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthTokenInfo;
import static org.bibsonomy.util.ValidationUtils.present;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class IbatisOAuthLogic implements IOAuthLogic {
	private static final Log log = LogFactory.getLog(IbatisOAuthLogic.class);

	//------------------------------------------------------------------------
	// database logic interface
	//------------------------------------------------------------------------
	/**
	 * Initialize iBatis layer.
	 */
	private final SqlMapClient sqlMap;

	private String defaultCallbackUrl;

	private static IOAuthLogic instance = null;

	private IbatisOAuthLogic() {
		try {
			// initialize database client
			String resource = "SqlMapConfig_OpenSocial.xml";
			Reader reader = Resources.getResourceAsReader (resource);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			log.info("OpenSocial database connection initialized.");

		} catch (Exception e) {
			throw new RuntimeException ("Error initializing DBAccess class. Cause: " + e);
		}
	}

	/**
	 * @return An instance of this implementation of {@link IOAuthLogic}
	 */
	public static IOAuthLogic getInstance() {
		if (instance == null) instance = new IbatisOAuthLogic();
		return instance;
	}

	//------------------------------------------------------------------------
	// IOAuthLogic interface
	//------------------------------------------------------------------------
	public void createAuthentication(String gadgetUrl, String server, String consumerKey, String consumerSecret, KeyType keyType) {
		// TODO Auto-generated method stub
		throw new RuntimeException("METHOD NOT IMPLEMENTED");
	}

	public ConsumerInfo readAuthentication(SecurityToken securityToken, String serviceName, OAuthServiceProvider provider) {
		OAuthConsumerInfo consumerParam = makeConsumerInfo(securityToken, serviceName);
		OAuthConsumerInfo consumerInfo = null;
		try {
			consumerInfo = (OAuthConsumerInfo)this.sqlMap.queryForObject("getAuthentication", consumerParam);
		} catch (SQLException e) {
			log.error("No consumer information found for '"+securityToken.getActiveUrl()+"' on '"+serviceName+"'");
		}
	    if (!present(consumerInfo)) {
	        throw new RuntimeException("No key for gadget " + securityToken.getAppUrl() + " and service " + serviceName);
	    }
		//String key, String secret, KeyType type, String name,String callbackUrl
		BasicOAuthStoreConsumerKeyAndSecret cks = 
			new BasicOAuthStoreConsumerKeyAndSecret(
					consumerInfo.getConsumerKey(),
					consumerInfo.getConsumerSecret(),
					consumerInfo.getKeyType(), 
					consumerInfo.getKeyName(), 
					consumerInfo.getCallbackUrl()
			);
		OAuthConsumer consumer = null;
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
		String callback = (cks.getCallbackUrl() != null ? cks.getCallbackUrl() : getDefaultCallbackUrl());
		return new ConsumerInfo(consumer, cks.getKeyName(), callback);
	}

	public TokenInfo createToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName, TokenInfo tokenInfo) {
		OAuthTokenIndex tokenIndex = makeTokenIndex(securityToken, serviceName);
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

	public void deleteToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName) {
		throw new RuntimeException("METHOD NOT IMPLEMENTED");
	}

	public TokenInfo readToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName) {
		OAuthTokenIndex tokenIndex = makeTokenIndex(securityToken, serviceName);
		
		OAuthTokenInfo tokenInfo = null;
		try {
			tokenInfo = (OAuthTokenInfo)this.sqlMap.queryForObject("getToken", tokenIndex);
		} catch (SQLException e) {
			log.error("Error fetching token for viewer '"+tokenIndex.getUserId()+"' on gadget '"+tokenIndex.getGadgetUri()+"'", e);
		}
		
		// we have to construct the temporary parameter object as the TokenInfo class has no default constructor
		// FIXME: Ibatis supports pre-initialized parameter objects
		TokenInfo retVal = null;
		if (present(tokenInfo)) {
			retVal = new TokenInfo(tokenInfo.getAccessToken(), tokenInfo.getTokenSecret(), tokenInfo.getSessionHandle(), tokenInfo.getTokenExpireMillis());
		}
		return retVal;
	}

	private OAuthTokenIndex makeTokenIndex(SecurityToken securityToken,
			String serviceName) {
		OAuthTokenIndex tokenIndex = new OAuthTokenIndex();
		tokenIndex.setGadgetUri(securityToken.getAppUrl());
		tokenIndex.setServiceName(serviceName);
		tokenIndex.setModuleId(securityToken.getModuleId());
		tokenIndex.setUserId(securityToken.getViewerId());
		return tokenIndex;
	}

	//------------------------------------------------------------------------
	// private helper
	//------------------------------------------------------------------------
	private OAuthConsumerInfo makeConsumerInfo(SecurityToken securityToken, String serviceName) {
		OAuthConsumerInfo consumerInfo = new OAuthConsumerInfo();
		consumerInfo.setGadgetUrl(securityToken.getAppUrl());
		consumerInfo.setServiceName(serviceName);
		return consumerInfo;
	}

	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setDefaultCallbackUrl(String defaultCallbackUrl) {
		this.defaultCallbackUrl = defaultCallbackUrl;
	}

	public String getDefaultCallbackUrl() {
		return defaultCallbackUrl;
	}
}
