package org.bibsonomy.rest.enums;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;

/**
 * The supported HTTP-Methods.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public enum HttpMethod {

	GET, POST, PUT, DELETE, HEAD;

	/**
	 * Returns the corresponding HttpMethod-enum for the given string.
	 */
	public static HttpMethod getHttpMethod(final String httpMethod) {
		if (httpMethod == null) throw new InternServerException("HTTP-Method is null");

		final String method = httpMethod.toLowerCase().trim();
		if ("get".equals(method)) {
			return GET;
		} else if ("post".equals(method)) {
			return POST;
		} else if ("put".equals(method)) {
			return PUT;
		} else if ("delete".equals(method)) {
			return DELETE;
		} else if ("head".equals(method)) {
			return HEAD;
		} else {
			throw new UnsupportedHttpMethodException(httpMethod);
		}
	}
}