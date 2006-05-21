package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public interface ContextHandler 
{
	public Strategy createStrategy( Context context, StringTokenizer urlTokens, String httpMethod );
}

/*
 * $Log$
 * Revision 1.1  2006-05-21 20:31:51  mbork
 * continued implementing context
 *
 */