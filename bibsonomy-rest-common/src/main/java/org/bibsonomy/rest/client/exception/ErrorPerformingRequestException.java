package org.bibsonomy.rest.client.exception;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ErrorPerformingRequestException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ErrorPerformingRequestException( String message )
	{
		super( message );
	}
	
	public ErrorPerformingRequestException( Throwable cause )
	{
		super( cause );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-10 12:42:16  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.3  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.2  2006/06/23 20:46:18  mbork
 * removed unneeded constructor
 *
 * Revision 1.1  2006/06/06 22:20:55  mbork
 * started implementing client api
 *
 */