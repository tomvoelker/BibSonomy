package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.posts.GetListOfPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetNewPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetPopularPostsStrategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PostsHandler implements ContextHandler 
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
			// /posts
			if( HttpMethod.GET == httpMethod )
			{
				return new GetListOfPostsStrategy( context );
			}
			break;
		case 1:
			// /posts/added
			if( HttpMethod.GET == httpMethod )
			{
				String path = urlTokens.nextToken();
				if( RestProperties.getInstance().getAddedPostsUrl().equalsIgnoreCase( path ) )
				{
					return new GetNewPostsStrategy( context );
				}
				else if( RestProperties.getInstance().getPopularPostsUrl().equalsIgnoreCase( path ) )
				{
					return new GetPopularPostsStrategy( context );
				}
			}
			break;
		}
		throw new UnsupportedOperationException( "no strategy for url " );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:14  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.3  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.2  2006/05/22 10:52:46  mbork
 * implemented context chooser for /posts
 * Revision 1.1 2006/05/21 20:31:51 mbork continued
 * implementing context
 * 
 */