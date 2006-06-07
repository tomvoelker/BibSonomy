package org.bibsonomy.rest.enums;

import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.InternServerException;

import junit.framework.TestCase;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class RenderingFormatTest extends TestCase
{
	public void testGetRenderingFormat()
	{
		assertEquals( RenderingFormat.XML, RenderingFormat.getRenderingFormat( "" ) );
		assertEquals( RenderingFormat.XML, RenderingFormat.getRenderingFormat( "xml" ) );
		assertEquals( RenderingFormat.XML, RenderingFormat.getRenderingFormat( "hurz" ) );

		assertEquals( RenderingFormat.RDF, RenderingFormat.getRenderingFormat( "rdf" ) );
		assertEquals( RenderingFormat.HTML, RenderingFormat.getRenderingFormat( "html" ) );

		assertEquals( RenderingFormat.RDF, RenderingFormat.getRenderingFormat( "RdF" ) );
		assertEquals( RenderingFormat.HTML, RenderingFormat.getRenderingFormat( "hTmL" ) );
		assertEquals( RenderingFormat.RDF, RenderingFormat.getRenderingFormat( "  RdF  " ) );

		try
		{
			RenderingFormat.getRenderingFormat( null );
			fail( "Should throw exception" );
		}
		catch( final InternServerException ex )
		{
		}
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-07 18:27:04  mbork
 * moved enum
 *
 * Revision 1.2  2006/06/05 14:14:13  mbork
 * implemented GET strategies
 *
 */