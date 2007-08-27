package org.bibsonomy.common.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(final String resourceId) {
		super("The requested resource (with ID " + resourceId + ") was not found. \nMaybe it has been deleted or its ID has changed, because it has been modfied via the webinterface or another application.");
	}
}