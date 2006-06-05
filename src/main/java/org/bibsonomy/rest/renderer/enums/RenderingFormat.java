package org.bibsonomy.rest.renderer.enums;

import org.bibsonomy.rest.exceptions.InternServerException;

/**
 * The supported rendering formats.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public enum RenderingFormat
{
	XML, RDF, HTML;

	/**
	 * Returns the rendering format to the given string. It defaults to XML.
	 */
	public static RenderingFormat getRenderingFormat( final String renderingFormat )
	{
		if( renderingFormat == null ) throw new InternServerException( "RenderingFormat is null" );
		final String format = renderingFormat.toLowerCase().trim();
		if( "rdf".equals( format ) )
		{
			return RDF;
		}
		else if( "html".equals( format ) )
		{
			return HTML;
		}
		else
		{
			return XML;
		}
	}
}

/*
 * $Log$
 * Revision 1.3  2006-06-05 14:14:12  mbork
 * implemented GET strategies
 *
 */