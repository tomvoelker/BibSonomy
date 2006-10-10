package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class NoSuchResourceException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public NoSuchResourceException( String message )
	{
		super( message );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-10 12:42:14  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.1  2006/06/13 18:07:40  mbork
 * introduced unit tests for servlet using null-pattern for request and response. tested to use cactus/ httpunit, but decided not to use them.
 *
 */