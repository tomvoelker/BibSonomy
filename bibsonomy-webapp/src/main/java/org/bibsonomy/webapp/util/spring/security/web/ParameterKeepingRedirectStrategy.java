package org.bibsonomy.webapp.util.spring.security.web;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.security.web.DefaultRedirectStrategy;

/**
 * Adds Parameters from the previous request to the redirect url
 * 
 * @author jensi
  */
public class ParameterKeepingRedirectStrategy extends DefaultRedirectStrategy {
	private Collection<String> parameterNames;
	
	@Override
	public void sendRedirect(final HttpServletRequest request, final HttpServletResponse response, String url) throws IOException {
		
		super.sendRedirect(request, new HttpServletResponseWrapper(response) {
			@Override
			public String encodeRedirectURL(String url) {
				StringBuilder sb = new StringBuilder(url);
				char sep = (url.indexOf('?') >= 0) ? '&' : '?';
				for (String parameterName : parameterNames) {
					String param = request.getParameter(parameterName);
					if (param != null) {
						sb.append(sep).append(parameterName).append('=').append(param);
						sep = '&';
					}
				}
				return super.encodeRedirectURL(sb.toString());
			}
		}, url);
	}

	/**
	 * @return the parameterNames
	 */
	public Collection<String> getParameterNames() {
		return this.parameterNames;
	}

	/**
	 * @param parameterNames the parameterNames to set
	 */
	public void setParameterNames(Collection<String> parameterNames) {
		this.parameterNames = parameterNames;
	}
}
