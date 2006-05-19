package org.bibsonomy.rest;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class InternServerException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public InternServerException( String message )
	{
		super( message );
	}

	/**
	 * @param cause
	 */
	public InternServerException(Throwable cause) 
	{
		super( cause );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-05-19 21:01:09  mbork
 * started implementing rest api
 *
 */