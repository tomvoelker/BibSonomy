package org.bibsonomy.common.enums;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public enum ResourceType
{
	BIBTEX, BOOKMARK, ALL;

	/**
	 * Returns the corresponding HttpMethod-enum for the given string.
	 */
	public static ResourceType getResourceType( final String resourceType )
	{
		if( resourceType == null ) throw new InternServerException( "ResourceType is null" );
		final String type = resourceType.toLowerCase().trim();
		if( "bibtex".equals( type ) )
		{
			return BIBTEX;
		}
		else if( "bookmark".equals( type ) )
		{
			return BOOKMARK;
		}
		else if( "all".equals( type ) )
		{
			return ALL;
		}
		else
		{
			throw new UnsupportedResourceTypeException( resourceType );
		}
	}
}

/*
 * $Log$
 * Revision 1.1  2007-02-21 14:42:37  mbork
 * somehow forgot to commit ^^
 *
 * Revision 1.1  2006/10/10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.1  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 */