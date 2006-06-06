package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class InvalidXMLException extends BadRequestException
{
	private static final long serialVersionUID = 1L;

	public InvalidXMLException( String message )
	{
		super( "The body part of the received XML document is not valid: " + message );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 17:39:29  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 */