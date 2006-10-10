package org.bibsonomy.rest.enums;

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
 * Revision 1.1  2006-10-10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.1  2006/06/07 18:27:04  mbork
 * moved enum
 *
 * Revision 1.3  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 */