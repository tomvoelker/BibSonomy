 package org.bibsonomy.webapp.util.auth;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.RequestLogic;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.ParameterList;
import org.openid4java.message.sreg.SRegMessage;
import org.openid4java.message.sreg.SRegRequest;
import org.openid4java.message.sreg.SRegResponse;

/**
 * Class providing features for OpenID authentication
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class OpenID implements Serializable {
	/**
	 * Logger
	 */
	private static final Log log = LogFactory.getLog(OpenID.class);

	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 437358812087062133L;
	
	/**
	 * name of session attribute 
	 */
	public static final String OPENID_SESSION_ATTRIBUTE = "openIDSessionValid";
	
	/**
	 * name of the session attribute to store the openid logic instance
	 */
	public static final String OPENID_LOGIC_SESSION_ATTRIBUTE = "openid-logic";
	
	/**
	 * name of session attribute to store the discovery information
	 */
	public static final String OPENID_DISCOVERY_SESSION_ATTRIBUTE = "openid-disc";
	
	
	/**
	 * manager for openID authentication
	 */
	private OpenIdConsumerManager manager;
	
	/**
	 * Authenticates an OpenID request and forwards the user to her 
	 * OpenID provider for authentication
	 * 	 
	 * @param requestLogic object which wraps the HTTP-Request
	 * @param openID the users OpenID
	 * @param realm A "realm" is a pattern that represents the part of URL-space for which an OpenID Authentication request is valid
	 * 		  (see http://openid.net/specs/openid-authentication-2_0.html#realms)
	 * @param returnURL url the user is forwared after authentication
	 * @param retrieveProfileInformation if the OpenID provider should send profile information of the user
	 * @return The url of OpenID provider where forward the user to
	 * @throws OpenIDException 
	 */
	public String authOpenIdRequest(final RequestLogic requestLogic, final String openID, final String realm, String returnURL, boolean retrieveProfileInformation) throws OpenIDException {
		/*
		 *  perform discovery on the user-supplied identifier
		 */
		@SuppressWarnings("unchecked")
		List discoveries = manager.discover(openID);
		manager.setNonceVerifier(new InMemoryNonceVerifier(100000));
		
		/*
		 *  attempt to associate with the OpenID provider
		 *	and retrieve one service endpoint for authentication
		 */
		DiscoveryInformation discovered = manager.associate(discoveries);
		
		/*
		 *  store the discovery information in the user's session
		 * 	httpReq.getSession().setAttribute("openid-disc", discovered);
		 */
		requestLogic.setSessionAttribute(OPENID_DISCOVERY_SESSION_ATTRIBUTE, discovered);
		
		/*
		 *  obtain a AuthRequest message to be sent to the OpenID provider
		 */
		AuthRequest authReq = manager.authenticate(discovered, returnURL);
		
		/*
		 *  Attribute Exchange
		 */
		SRegRequest sregReq = SRegRequest.createFetchRequest();
		
		if (retrieveProfileInformation) {
			/*
			 *  required attributes
			 */
			sregReq.addAttribute("nickname", true);
			sregReq.addAttribute("email", true);
			
			/*
			 * optional attributes
			 */
			sregReq.addAttribute("fullname", false);
			sregReq.addAttribute("gender", false);
			sregReq.addAttribute("language", false);
			sregReq.addAttribute("country", false);
		} 
		
		/*
		 *  attach the extension to the authentication request
		 */
		authReq.addExtension(sregReq);
		
		/*
		 * set root domain to trust
		 */
		authReq.setRealm(realm);
		
		/*
		 * save instance of openID logic in session
		 */
		requestLogic.setSessionAttribute(OPENID_LOGIC_SESSION_ATTRIBUTE, this);

		/*
		 * return redirect url to provider
		 */
		return authReq.getDestinationUrl(true);		
	}
	

	/**
	 * Verifies an incoming OpenID response
	 * 
	 * @param requestLogic HTTP request
	 * @param retrieveProfileInformation if profile information should be added to user object
	 * @return A user object (containing profile information provided by OpenID provider if wanted)  
	 */
	public User verifyResponse(final RequestLogic requestLogic, boolean retrieveProfileInformation) {
		try {
			/*
			 *  extract the parameters from the authentication response
			 *  (which comes in as a HTTP request from the OpenID provider)
			 */
			ParameterList response = new ParameterList(requestLogic.getParameterMap());

			/*
			 *  retrieve the previously stored discovery information
			 */
			DiscoveryInformation discovered = (DiscoveryInformation) requestLogic.getSessionAttribute(OPENID_DISCOVERY_SESSION_ATTRIBUTE);

				
			/*
			 * verify the response
			 */
			VerificationResult verification = manager.verify(requestLogic.getCompleteRequestURL(), response, discovered);

			/*
			 * examine the verification result and extract the verified
			 * identifier
			 */
			Identifier verified = verification.getVerifiedId();
			
			/*
			 * get OpenID from identity attribute
			 */
			String openID = requestLogic.getParameter("openid.identity");
			
			/*
			 *  successful verified
			 */
			if (verified != null) {
				User user = new User();
				user.setOpenID(openID);
				AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
				
				if (authSuccess.hasExtension(SRegMessage.OPENID_NS_SREG) && retrieveProfileInformation) {
					MessageExtension ext = authSuccess.getExtension(SRegMessage.OPENID_NS_SREG);

				    if (ext instanceof SRegResponse) {
				        SRegResponse sregResp = (SRegResponse) ext;
				        
				        String nickName = sregResp.getAttributeValue("nickname");
				        String email = sregResp.getAttributeValue("email");
				        String fullName = sregResp.getAttributeValue("fullname");
				        String gender = sregResp.getAttributeValue("gender");
				        String language = sregResp.getAttributeValue("language");
				        String country = sregResp.getAttributeValue("country");				        
				        				       
				        user.setName(nickName);
				        user.setEmail(email);
				        user.setRealname(fullName);
				        user.setGender(gender);
				        user.getSettings().setDefaultLanguage(language);
				        user.setPlace(country);
				    }
				}				
				return user; 
			} 
		} catch (OpenIDException e) {
			log.error("OpenID verification failed: ", e);
		}
		return null;
	}	
		
	/**
	 * extends the expiration time of the OpenID session
	 * @param session http session
	 * @param openID the openID
	 */
	public void extendOpenIDSession(HttpSession session, final String openID) {
		log.debug("extend OpenID session");		
		session.setAttribute(OpenID.OPENID_SESSION_ATTRIBUTE, openID);
	}
	
	/**
	 * @param manager OpenID ConsumerManager
	 */
	public void setManager(OpenIdConsumerManager manager) {
		this.manager = manager;
	}	
}