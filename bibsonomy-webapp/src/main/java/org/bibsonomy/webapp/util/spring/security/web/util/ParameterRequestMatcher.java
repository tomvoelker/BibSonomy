package org.bibsonomy.webapp.util.spring.security.web.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.RequestMatcher;

/**
 * {@link RequestMatcher} that checks if a certain request-parameter is set to a specific value
 * 
 * @author jensi
 * @version $Id$
 */
public class ParameterRequestMatcher implements RequestMatcher {
	
	private String parameterName;
	
	private String parameterValue;
	
	@Override
	public boolean matches(HttpServletRequest request) {
		return parameterValue.equals(request.getParameter(parameterName));
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
	 * @return the parameterValue
	 */
	public String getParameterValue() {
		return this.parameterValue;
	}

	/**
	 * @param parameterValue the parameterValue to set
	 */
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	
}
