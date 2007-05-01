package org.bibsonomy.model;

import junit.framework.TestCase;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ResourceTypeTest extends TestCase
{
	public void testGetResourceType()
	{
		assertEquals( Resource.class , Resource.getResourceType( "all" ) );
		assertEquals( BibTex.class, Resource.getResourceType( "bibtex" ) );
		assertEquals( Bookmark.class, Resource.getResourceType( "bookmark" ) );

		assertEquals( Resource.class, Resource.getResourceType( " All" ) );
		assertEquals( BibTex.class, Resource.getResourceType( "BIBTEX" ) );
		assertEquals( Bookmark.class, Resource.getResourceType( "BookMark " ) );
		
		try
		{
			Resource.getResourceType( "foo bar" );
			fail( "Should throw exception" );
		}
		catch( final UnsupportedResourceTypeException ex )
		{
		}

		try
		{
			Resource.getResourceType( "" );
			fail( "Should throw exception" );
		}
		catch( final UnsupportedResourceTypeException ex )
		{
		}

		try
		{
			Resource.getResourceType( null );
			fail( "Should throw exception" );
		}
		catch( final InternServerException ex )
		{
		}
	}

	/*
	 * We want to make sure that this is the case, because we are relying on it
	 * in our testcases.
	 */
	public void testToString()
	{
		assertEquals( "BIBTEX", Resource.toString(BibTex.class) );
		assertEquals( "BOOKMARK", Resource.toString(Bookmark.class) );
		assertEquals( "ALL", Resource.toString(Resource.class) );
	}
}

/*
 * $Log$
 * Revision 1.1  2007-05-01 22:26:02  jillig
 * ->more type-safety with class as resourcetype
 * ->moved tests for resourcetype-related stuff from common-project herein
 *
 * Revision 1.1  2007/02/21 14:42:37  mbork
 * somehow forgot to commit ^^
 *
 * Revision 1.1  2006/10/10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.1  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 */