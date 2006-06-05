package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class UnsupportedResourceTypeException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public UnsupportedResourceTypeException( final String type )
	{
		super( "Resource-Type ('" + type + "') is not supported" );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-05 14:14:11  mbork
 * implemented GET strategies
 *
 */