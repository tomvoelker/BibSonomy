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

import java.util.List;

import net.oauth.OAuthServiceProvider;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;
import org.apache.shindig.gadgets.oauth.OAuthRequestException;
import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;
import org.apache.shindig.gadgets.oauth.OAuthStore.TokenInfo;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthUserInfo;

/**
 * Interface for accessing the OAuthDataStore
 * 
 * @author fei
 */
public interface OAuthLogic {
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
	 * @return the consumer info
	 * @throws OAuthRequestException 
	 */
	public ConsumerInfo readAuthentication(SecurityToken securityToken, String serviceName, OAuthServiceProvider provider) throws OAuthRequestException;

	//------------------------------------------------------------------------
	// OAuth token interface
	//------------------------------------------------------------------------
	/**
	 * builds and stores an according gadget
	 * @param securityToken 
	 * @param consumerInfo 
	 * @param serviceName 
	 * @param tokenName 
	 * @param tokenInfo 
	 * @return TODO
	 */
	public TokenInfo createToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName, TokenInfo tokenInfo);
	
	/**
	 * read security token from database 
	 * @param securityToken 
	 * @param consumerInfo 
	 * @param serviceName 
	 * @param tokenName 
	 * @return TODO
	 */
	public TokenInfo readToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName);
	
	/**
	 * delete security token from database 
	 * @param securityToken 
	 * @param consumerInfo 
	 * @param serviceName 
	 * @param tokenName 
	 */
	public void deleteToken(SecurityToken securityToken, ConsumerInfo consumerInfo, String serviceName, String tokenName);
	
	//------------------------------------------------------------------------
	// OAuthDataStore interface
	//------------------------------------------------------------------------
	/**
	 * read OAuth consumer information from database
	 * 
	 * @param consumerInfo
	 */
	public void createConsumer(OAuthConsumerInfo consumerInfo);
	
	/**
	 * read OAuth consumer information from database
	 * 
	 * @param consumerKey
	 * @return TODO
	 */
	public OAuthConsumerInfo readConsumer(String consumerKey);

	/**
	 * delete OAuth consumer information from database
	 * 
	 * @param consumerKey
	 */
	public void deleteConsumer(String consumerKey);
	
	/**
	 * list all registered consumers
	 * 
	 * @return list of all registered consumers
	 */
	public List<OAuthConsumerInfo> listConsumers();

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
	 * @return TODO
	 */
	public OAuthEntry readProviderToken(String oauthToken);
	
	/**
	 * remove an OauthAccess using the AccessToken and Username to query
	 * @param userName 
	 * @param accessToken 
	 */
	public void removeSpecificAccessToken(String userName, String accessToken);
	
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

	/**
	 * @param username
	 * @return list which is never null but may be immutable
	 */
	public List<OAuthUserInfo> getOAuthUserApplication(String username);
}
