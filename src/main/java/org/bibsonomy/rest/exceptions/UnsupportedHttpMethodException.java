package org.bibsonomy.rest.exceptions;

import org.bibsonomy.rest.enums.HttpMethod;

/**
 * Is thrown if the HTTP-Method is not supported.
 *
 * @author Christian Schenk
 * @version $Id$
 */
public class UnsupportedHttpMethodException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public UnsupportedHttpMethodException( final String httpMethod )
	{
		super( "HTTP-Method ('" + httpMethod + "') is not supported" );
	}

	public UnsupportedHttpMethodException( final HttpMethod httpMethod, final String resourceName )
	{
		super( "HTTP-Method ('" + httpMethod.name() + "') is not supported for the " + resourceName + " Resource" );
	}
}

/*
 * $Log$
 * Revision 1.3  2006-06-05 14:14:11  mbork
 * implemented GET strategies
 *
 */