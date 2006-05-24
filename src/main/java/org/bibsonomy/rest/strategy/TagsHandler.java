package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.tags.GetListOfTagsStrategy;
import org.bibsonomy.rest.strategy.tags.GetTagDetailsStrategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class TagsHandler implements ContextHandler
{
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.ContextHandler#createStrategy(java.lang.StringBuffer)
	 */
	public Strategy createStrategy( Context context, StringTokenizer urlTokens, HttpMethod httpMethod )
	{
		int numTokensLeft = urlTokens.countTokens();
		switch( numTokensLeft )
		{
			case 0:
				// /tags
				if( HttpMethod.GET == httpMethod )
				{
					return new GetListOfTagsStrategy( context );
				}
				break;
			case 1:
				// /tags/[tag]
				if( HttpMethod.GET == httpMethod )
				{
					return new GetTagDetailsStrategy( context, urlTokens.nextToken() );
				}
				break;
		}
		throw new UnsupportedOperationException( "no strategy for url " );
	}
}

/*
 * $Log$
 * Revision 1.3  2006-05-24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.2  2006/05/22 10:42:25  mbork
 * implemented context chooser for /tags
 *
 * Revision 1.1  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 */