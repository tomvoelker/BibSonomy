package org.bibsonomy.common.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id: ResourceNotFoundException.java,v 1.2 2007-10-30 17:37:35 jillig
 *          Exp $
 */
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new resource not found exception with the specified resource
	 * id.
	 * 
	 * @param resourceId
	 *            the id of the resource that was not found. This is written
	 *            into a detail message which is saved for later retrieval by
	 *            the {@link #getMessage()} method.
	 */
	public ResourceNotFoundException(final String resourceId) {
		super("The requested resource (with ID " + resourceId + ") was not found. \nMaybe it has been deleted or its ID has changed, because it has been modfied via the webinterface or another application.");
	}
}