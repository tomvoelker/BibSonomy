package org.bibsonomy.common.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class UnsupportedGroupingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnsupportedGroupingException(final String grouping) {
		super("Grouping ('" + grouping + "') is not supported");
	}
}