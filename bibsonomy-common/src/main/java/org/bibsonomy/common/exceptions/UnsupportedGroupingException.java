package org.bibsonomy.common.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id: UnsupportedGroupingException.java,v 1.3 2007-10-30 17:37:35
 *          jillig Exp $
 */
public class UnsupportedGroupingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new unsupported grouping exception with the specified
	 * grouping.
	 * 
	 * @param grouping
	 *            the grouping which is not supported. This is written into a
	 *            detail message which is saved for later retrieval by the
	 *            {@link #getMessage()} method.
	 */
	public UnsupportedGroupingException(final String grouping) {
		super("Grouping ('" + grouping + "') is not supported");
	}
}