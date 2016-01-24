package org.bibsonomy.webapp.filters;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
/**
 * TODO: add documentation to this class
 *
 * @author niebler
 */
public class RecaptchaResponseFilter implements Filter {
	
    private static final String RECAPTCHA_RESPONSE_ALIAS = "recaptcha_response_field";
    private static final String RECAPTCHA_RESPONSE_ORIGINAL = "g-recaptcha-response";
	
	private static class ModifiedHttpServerRequest extends HttpServletRequestWrapper {

		final Map<String, String[]> parameters;
		
		/**
		 * @param request
		 */
		public ModifiedHttpServerRequest(HttpServletRequest request) {
			super(request);
			this.parameters = new HashMap<>(request.getParameterMap());
			this.parameters.put(RECAPTCHA_RESPONSE_ALIAS, request.getParameterValues(RECAPTCHA_RESPONSE_ORIGINAL));
		}
		
        @Override
        public String getParameter(String name) {
            return parameters.containsKey(name) ? parameters.get(name)[0] : null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return parameters;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(parameters.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
            return parameters.get(name);
        }
    }

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest
                && servletRequest.getParameter(RECAPTCHA_RESPONSE_ORIGINAL) != null) {
            filterChain.doFilter(new ModifiedHttpServerRequest((HttpServletRequest) servletRequest), servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
