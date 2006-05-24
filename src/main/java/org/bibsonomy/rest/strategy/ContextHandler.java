package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.rest.enums.HttpMethod;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public interface ContextHandler 
{
	//public Strategy createStrategy( Context context, StringTokenizer urlTokens, String httpMethod );
	public Strategy createStrategy( Context context, StringTokenizer urlTokens, HttpMethod httpMethod );
}

/*
 * $Log$
 * Revision 1.2  2006-05-24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 */