package org.bibsonomy.opensocial.oauth.database;

import java.util.Date;

import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;
import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;
import org.apache.shindig.gadgets.oauth.OAuthStore.TokenInfo;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;

/**
 * Interface for accessing the OAuthDataStore
 * 
 * @author fei
 */
public interface IOAuthLogic {
	//------------------------------------------------------------------------
	// authentication information interface
	//------------------------------------------------------------------------
	/**
	 * add given authentication to the system
	 * 
	 * @param gadgetUrl
	 * @param server
	 * @param consumerKey
	 * @param consumerSecret
	 * @param keyType
	 */
	public void createAuthentication(String gadgetUrl, String server, String consumerKey, String consumerSecret, KeyType keyType);

	/**
	 * get authentication from the system
	 * 
	 * @param securityToken
	 * @param serviceName
	 * @param provider
	 * @return
	 */
	public ConsumerInfo readAuthentication(SecurityToken securityToken, String serviceName, OAuthServiceProvider provider);

	//------------------------------------------------------------------------
	// OAuth token interface
	//------------------------------------------------------------------------
	/**
	 * builds and stores an according gadget
	 */
	public TokenInfo createToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName, TokenInfo tokenInfo);
	
	/**
	 * read security token from database 
	 *  
	 * @param gadgetUrl
	 * @param server
	 * @param ownerId
	 * @param viewerId
	 * @return
	 */
	public TokenInfo readToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName);
	
	/**
	 * delete security token from database 
	 *  
	 * @param gadgetUrl
	 * @param server
	 * @param ownerId
	 * @param viewerId
	 * @return
	 */
	public void deleteToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName);
	
	//------------------------------------------------------------------------
	// OAuthDataStore interface
	//------------------------------------------------------------------------
	/**
	 * read OAuth consumer information from database
	 * 
	 * @param consumerKey
	 * @return
	 */
	public OAuthConsumerInfo readConsumer(String consumerKey);

	/**
	 * create an OAuth token 
	 * 
	 * @param entry
	 */
	public void createProviderToken(OAuthEntry entry);

	/**
	 * read an OAuth token 
	 * 
	 * @param oauthToken
	 */
	public OAuthEntry readProviderToken(String oauthToken);
	
	/**
	 * update given OAuth token entry (e.g. authorize)
	 * 
	 * @param entry
	 */
	public void updateProviderToken(OAuthEntry entry);

	/**
	 * remove the given provider token from the database
	 * @param token
	 */
	public void deleteProviderToken(String token);
	
}
