package org.bibsonomy.rest.exceptions;

import org.bibsonomy.rest.enums.HttpMethod;

/**
 * Is thrown if the HTTP-Method is not supported.
 *
 * @author Christian Schenk
 * @version $Id$
 */
public class UnsupportedHttpMethodException extends RuntimeException {

	public UnsupportedHttpMethodException(final HttpMethod httpMethod, final String resourceName) {
		super("HTTP-Method ('" + httpMethod.name() + "') not implemented for the " + resourceName + " Resource ");
	}
}