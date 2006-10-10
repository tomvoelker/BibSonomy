package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class UnsupportedGroupingException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public UnsupportedGroupingException( final String grouping )
	{
		super( "Grouping ('" + grouping + "') is not supported" );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-10 12:42:14  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.1  2006/06/05 14:14:11  mbork
 * implemented GET strategies
 *
 */