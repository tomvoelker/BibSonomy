package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class InvalidXMLException extends BadRequestOrResponseException {

	private static final long serialVersionUID = 1L;

	public InvalidXMLException(final String message) {
		super("The body part of the received XML document is not valid: " + message);
	}
}