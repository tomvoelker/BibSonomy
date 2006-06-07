package org.bibsonomy.rest.renderer;

import junit.framework.TestCase;

import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.renderer.impl.HTMLRenderer;
import org.bibsonomy.rest.renderer.impl.RDFRenderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class RendererFactoryTest extends TestCase
{
	public void testGetRenderer()
	{
		assertTrue( RendererFactory.getRenderer( RenderingFormat.XML ) instanceof XMLRenderer );
		assertTrue( RendererFactory.getRenderer( RenderingFormat.RDF ) instanceof RDFRenderer );
		assertTrue( RendererFactory.getRenderer( RenderingFormat.HTML) instanceof HTMLRenderer );

		try
		{
			RendererFactory.getRenderer( null );
			fail( "Should throw exception" );
		}
		catch( final InternServerException ex )
		{
		}
	}
}

/*
 * $Log$
 * Revision 1.3  2006-06-07 18:27:04  mbork
 * moved enum
 *
 * Revision 1.2  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 */