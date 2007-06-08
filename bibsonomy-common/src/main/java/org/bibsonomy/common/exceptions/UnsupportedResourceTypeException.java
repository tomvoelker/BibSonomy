package org.bibsonomy.common.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class UnsupportedResourceTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnsupportedResourceTypeException(final String type) {
		super("Resource-Type ('" + type + "') is not supported");
	}
}