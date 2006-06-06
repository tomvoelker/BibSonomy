package org.bibsonomy.rest.client.exception;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ErrorPerformingRequestException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ErrorPerformingRequestException( Throwable cause )
	{
		super( cause );
	}

	public ErrorPerformingRequestException( String message )
	{
		super( message );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 22:20:55  mbork
 * started implementing client api
 *
 */