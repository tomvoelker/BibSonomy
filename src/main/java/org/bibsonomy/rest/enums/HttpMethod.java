package org.bibsonomy.rest.enums;

import org.bibsonomy.rest.exceptions.InternServerException;

public enum HttpMethod {
	GET, POST, PUT, DELETE;

	public static HttpMethod getHttpMethod(final String httpMethod) {
		if (httpMethod == null)	throw new InternServerException("HTTP-Method is null");
		final String method = httpMethod.toLowerCase().trim();
		if ("get".equals(method)) {
			return GET;
		} else if ("post".equals(method)) {
			return POST;
		} else if ("put".equals(method)) {
			return PUT;
		} else if ("delete".equals(method)) {
			return DELETE;
		} else {
			throw new InternServerException("HTTP-Method (" + httpMethod + ") is not supported");
		}
	}
}