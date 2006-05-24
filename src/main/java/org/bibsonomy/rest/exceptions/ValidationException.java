package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ValidationException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public ValidationException( String message )
	{
		super( message );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-05-24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/19 21:01:09  mbork
 * started implementing rest api
 *
 */