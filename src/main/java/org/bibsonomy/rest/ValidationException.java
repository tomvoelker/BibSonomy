package org.bibsonomy.rest;

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
 * Revision 1.1  2006-05-19 21:01:09  mbork
 * started implementing rest api
 *
 */