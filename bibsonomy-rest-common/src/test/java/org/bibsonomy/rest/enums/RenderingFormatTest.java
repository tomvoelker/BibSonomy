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
	
	public void testToString()
	{
		assertEquals( RenderingFormat.XML.toString(), "XML" );
		assertEquals( RenderingFormat.RDF.toString(), "RDF" );
		assertEquals( RenderingFormat.HTML.toString(), "HTML" );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.2  2006/09/16 18:17:51  mbork
 * added some new fake bibtex entries to demonstrate jabref plugin :)
 * fix of tests depiending on fake bibtex entries
 *
 * Revision 1.1  2006/06/07 18:27:04  mbork
 * moved enum
 *
 * Revision 1.2  2006/06/05 14:14:13  mbork
 * implemented GET strategies
 *
 */