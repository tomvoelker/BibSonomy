package org.bibsonomy.opensocial.security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.AuthenticationMode;
import org.apache.shindig.auth.BasicSecurityToken;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.auth.SecurityTokenCodec;
import org.apache.shindig.auth.SecurityTokenException;
import org.apache.shindig.auth.UrlParameterAuthenticationHandler;
import org.apache.shindig.common.crypto.BlobCrypterException;
import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.AuthenticationUtils;

import com.google.inject.Inject;

public class SpringSecurityAuthenticationHandler extends UrlParameterAuthenticationHandler {
	private static final Log log = LogFactory.getLog(SpringSecurityAuthenticationHandler.class); 

	/**
	 * TODO: use lightweight codec for improving performance
	 * 
	 * @param securityTokenCodec
	 */
	@Inject
	public SpringSecurityAuthenticationHandler(SecurityTokenCodec securityTokenCodec) {
		super(securityTokenCodec);
	}

	public String getName() {
		return AuthenticationMode.COOKIE.name(); 
	}

	@SuppressWarnings("deprecation")
	public SecurityToken getSecurityTokenFromRequest(HttpServletRequest request) throws InvalidAuthenticationException {
	    Map<String, String> parameters = getMappedParameters(request);
	    try {
	      if (parameters.get(SecurityTokenCodec.SECURITY_TOKEN_NAME) == null) {
	        return null;
	      }
	      
	      User loginUser = AuthenticationUtils.getUser();
	      String viewer  = loginUser.getName();
	      
	      SecurityToken st = getSecurityTokenCodec().createToken(parameters);
	      BasicSecurityToken newToken = new BasicSecurityToken(
	    		  st.getOwnerId(), 
	    		  viewer,
	    		  st.getAppId(), 
	    		  st.getDomain(), 
	    		  st.getAppUrl(), 
	    		  Long.toString(st.getModuleId()),
	    		  st.getContainer(), 
	    		  st.getActiveUrl(),
	    		  st.getExpiresAt());
	      
	      return (newToken);
	    } catch (Exception e) {
		      throw new InvalidAuthenticationException("Malformed security token " + parameters.get(SecurityTokenCodec.SECURITY_TOKEN_NAME), e);
		} 
	    
	}

}
