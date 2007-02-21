package org.bibsonomy.common.exceptions;

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
 * Revision 1.1  2007-02-21 14:42:37  mbork
 * somehow forgot to commit ^^
 *
 * Revision 1.1  2006/10/10 12:42:14  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.1  2006/06/05 14:14:11  mbork
 * implemented GET strategies
 *
 */