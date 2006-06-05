package org.bibsonomy.rest.enums;

import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.UnsupportedResourceTypeException;

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
 * Revision 1.1  2006-06-05 14:14:12  mbork
 * implemented GET strategies
 *
 */