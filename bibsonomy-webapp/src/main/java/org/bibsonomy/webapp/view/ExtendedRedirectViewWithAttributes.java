package org.bibsonomy.webapp.view;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * a redirect view that can keep attributes
 *
 * @author dzo
 */
public class ExtendedRedirectViewWithAttributes extends ExtendedRedirectView {
	
	/** to transfer errors while redirect */
	public static final String ERRORS_KEY = "errors";
	
	/**
	 * @param redirectURI
	 */
	public ExtendedRedirectViewWithAttributes(String redirectURI) {
		super(redirectURI);
	}
	
	/**
	 * adds an attribute to the redirect view
	 * @param key
	 * @param value
	 */
	public void addAttribute(final String key, final Object value) {
		final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		RequestContextUtils.getOutputFlashMap(requestAttributes.getRequest()).put(key, value);
	}
}
