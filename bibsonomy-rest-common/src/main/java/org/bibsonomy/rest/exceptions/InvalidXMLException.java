package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class InvalidXMLException extends BadRequestOrResponseException
{
	private static final long serialVersionUID = 1L;

	public InvalidXMLException( String message )
	{
		super( "The body part of the received XML document is not valid: " + message );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-10 12:42:14  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.2  2006/09/16 18:19:15  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.1  2006/06/06 17:39:29  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 */