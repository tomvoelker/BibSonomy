package org.bibsonomy.webapp.util.spring.security.web.logouthandler;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * @author Jens Illig
 */
public class RedirectHandler implements LogoutHandler {
	private static final Log log = LogFactory.getLog(RedirectHandler.class);
	private String parameterName;
	private Map<String, String> redirectUrls;
	
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if ((parameterName == null) || (redirectUrls == null)) {
			return;
		}
		String paramValue = request.getParameter(parameterName);
		if (paramValue == null) {
			return;
		}
		String redirectUrl = redirectUrls.get(paramValue);
		if (redirectUrl == null) {
			return;
		}
		try {
			response.sendRedirect(redirectUrl);
		} catch (IOException ex) {
			log.error("cannot redirect to '" + redirectUrl + "'", ex);
			// ok, we better go on instead of throwing an exception
		}
	}


	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return this.parameterName;
	}


	/**
	 * @param parameterName the parameterName to set
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}


	/**
	 * @return the redirectUrls
	 */
	public Map<String, String> getRedirectUrls() {
		return this.redirectUrls;
	}


	/**
	 * @param redirectUrls the redirectUrls to set
	 */
	public void setRedirectUrls(Map<String, String> redirectUrls) {
		this.redirectUrls = redirectUrls;
	}

	
}
