package org.bibsonomy.rest.renderer;

import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.renderer.impl.HTMLRenderer;
import org.bibsonomy.rest.renderer.impl.RDFRenderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

/**
 * A factory to get implementations of the
 * {@link org.bibsonomy.rest.renderer.Renderer}-interface.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class RendererFactory
{
	/**
	 * Returns the renderer for the given format.
	 */
	public static Renderer getRenderer( RenderingFormat renderingFormat )
	{
		if( renderingFormat == null ) throw new InternServerException( "RenderingFormat is null" );
		switch( renderingFormat )
		{
		case HTML:
			return new HTMLRenderer();
		case RDF:
			return new RDFRenderer();
		case XML:
		default:
			return new XMLRenderer();
		}
	}
}

/*
 * $Log$
 * Revision 1.3  2006-06-07 18:27:04  mbork
 * moved enum
 *
 * Revision 1.2  2006/06/05 14:14:11  mbork
 * implemented GET strategies
 *
 */