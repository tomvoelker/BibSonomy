package org.bibsonomy.common.enums;

import junit.framework.TestCase;

import org.bibsonomy.common.enums.ResourceType;
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
		assertEquals( ResourceType.ALL, ResourceType.getResourceType( "all" ) );
		assertEquals( ResourceType.BIBTEX, ResourceType.getResourceType( "bibtex" ) );
		assertEquals( ResourceType.BOOKMARK, ResourceType.getResourceType( "bookmark" ) );

		assertEquals( ResourceType.ALL, ResourceType.getResourceType( " All" ) );
		assertEquals( ResourceType.BIBTEX, ResourceType.getResourceType( "BIBTEX" ) );
		assertEquals( ResourceType.BOOKMARK, ResourceType.getResourceType( "BookMark " ) );
		
		try
		{
			ResourceType.getResourceType( "foo bar" );
			fail( "Should throw exception" );
		}
		catch( final UnsupportedResourceTypeException ex )
		{
		}

		try
		{
			ResourceType.getResourceType( "" );
			fail( "Should throw exception" );
		}
		catch( final UnsupportedResourceTypeException ex )
		{
		}

		try
		{
			ResourceType.getResourceType( null );
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
		assertEquals( "BIBTEX", ResourceType.BIBTEX.toString() );
		assertEquals( "BOOKMARK", ResourceType.BOOKMARK.toString() );
		assertEquals( "ALL", ResourceType.ALL.toString() );
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