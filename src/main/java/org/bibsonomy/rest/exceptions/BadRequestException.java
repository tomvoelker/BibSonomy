package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class BadRequestException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public BadRequestException()
	{
		super();
	}

	public BadRequestException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public BadRequestException( String message )
	{
		super( message );
	}

	public BadRequestException( Throwable cause )
	{
		super( cause );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 17:39:29  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 */