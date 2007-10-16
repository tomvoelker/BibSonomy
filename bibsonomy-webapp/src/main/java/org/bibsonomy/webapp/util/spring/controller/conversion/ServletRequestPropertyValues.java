/*
 * Created on 02.10.2007
 */
package org.bibsonomy.webapp.util.spring.controller.conversion;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;

public class ServletRequestPropertyValues extends MutablePropertyValues {
	private static final long serialVersionUID = 2531207290915126465L;

	public ServletRequestPropertyValues(ServletRequest request) {
		super(getParametersAndAttributesStartingWith(request, null));
	}

	private static Map<String,Object> getParametersAndAttributesStartingWith(final ServletRequest request, final String prefix) {
		final Map<String, Object> m = (Map<String, Object>) WebUtils.getParametersStartingWith(request, null);
		addAttributesStartingWith(m, request, null);
		return m;
	}
	
	/**
	 * Fills a map with all attributes whose name starts with the given
	 * prefix.
	 * Maps single values to String and multiple values to String array.
	 * <p>For example, with a prefix of "spring_", "spring_param1" and
	 * "spring_param2" result in a Map with "param1" and "param2" as keys.
	 * <p>Similar to Servlet 2.3's <code>ServletRequest.getParameterMap</code>,
	 * but more flexible and compatible with Servlet 2.2.
	 * @param params destination map
	 * @param request HTTP request in which to look for parameters
	 * @param prefix the beginning of parameter names
	 * (if this is null or the empty string, all parameters will match)
	 * @see javax.servlet.ServletRequest#getParameterNames
	 * @see javax.servlet.ServletRequest#getParameterValues
	 * @see javax.servlet.ServletRequest#getParameterMap
	 */
	public static void addAttributesStartingWith(final Map<? super String, Object> params, final ServletRequest request, String prefix) {
		Assert.notNull(request, "Request must not be null");
		Enumeration paramNames = request.getAttributeNames();
		if (prefix == null) {
			prefix = "";
		}
		final int prefixLength = prefix.length();
		while (paramNames != null && paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if ((prefixLength == 0) || paramName.startsWith(prefix)) {
				String unprefixed = paramName.substring(prefixLength);
				params.put(unprefixed, request.getAttribute(paramName));
			}
		}
	}
}
