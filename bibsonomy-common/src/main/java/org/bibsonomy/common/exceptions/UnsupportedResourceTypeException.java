package org.bibsonomy.common.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class UnsupportedResourceTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnsupportedResourceTypeException() {
		super("Please specify a resource-type by appending '?resourcetype=bibtex' or '?resourcetype=bookmark' to the requested URL");
	}
	
	public UnsupportedResourceTypeException(String message) {
		super(message);
	}	
}