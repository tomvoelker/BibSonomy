package org.bibsonomy.common.exceptions;

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
	public InternServerException( Throwable cause )
	{
		super( cause );
	}
}

/*
 * $Log$
 * Revision 1.1  2007-02-21 14:42:37  mbork
 * somehow forgot to commit ^^
 *
 * Revision 1.1  2006/10/10 12:42:14  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.2  2006/06/05 14:14:11  mbork
 * implemented GET strategies
 *
 * Revision 1.1  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/19 21:01:09  mbork
 * started implementing rest api
 *
 */