package org.bibsonomy.rest.enums;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;

/**
 * The supported HTTP-Methods.
 *
 * @author Christian Schenk
 * @version $Id$
 */
public enum HttpMethod
{
	GET, POST, PUT, DELETE, HEAD;

	/**
	 * Returns the corresponding HttpMethod-enum for the given string.
	 */
	public static HttpMethod getHttpMethod( final String httpMethod )
	{
		if( httpMethod == null ) throw new InternServerException( "HTTP-Method is null" );
		final String method = httpMethod.toLowerCase().trim();
		if( "get".equals( method ) )
		{
			return GET;
		}
		else if( "post".equals( method ) )
		{
			return POST;
		}
		else if( "put".equals( method ) )
		{
			return PUT;
		}
		else if( "delete".equals( method ) )
		{
			return DELETE;
		}
      else if( "head".equals( method ) )
      {
         return HEAD;
      }
		else
		{
			throw new UnsupportedHttpMethodException( httpMethod );
		}
	}
}

/*
 * $Log$
 * Revision 1.2  2007-02-21 14:08:33  mbork
 * - included code generation of the schema in the maven2 build-lifecycle
 * - removed circular dependencies among the modules
 * - cleaned up the poms of the modules
 * - fixed failing unit-tests
 *
 * Revision 1.1  2006/10/10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.6  2006/06/23 20:50:09  mbork
 * clientlib:
 * - added head request
 * - fixed issues with enums using uppercase letters invoked with toString()
 * serverlib:
 * - fixed some issues
 *
 * Revision 1.5  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 */