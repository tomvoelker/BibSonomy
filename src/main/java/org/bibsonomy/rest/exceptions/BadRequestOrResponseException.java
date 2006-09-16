package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class BadRequestOrResponseException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public BadRequestOrResponseException()
	{
		super();
	}

	public BadRequestOrResponseException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public BadRequestOrResponseException( String message )
	{
		super( message );
	}

	public BadRequestOrResponseException( Throwable cause )
	{
		super( cause );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-09-16 18:19:15  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.1  2006/06/06 17:39:29  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 */