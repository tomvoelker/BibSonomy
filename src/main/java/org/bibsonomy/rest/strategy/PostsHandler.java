package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

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
	public Strategy createStrategy( Context context, StringTokenizer urlTokens, String httpMethod )
	{
		int numTokensLeft = urlTokens.countTokens();
		switch( numTokensLeft )
		{
		case 0:
			// /posts
			if( Context.HTTP_GET.equalsIgnoreCase( httpMethod ) )
			{
				return new GetListOfPostsStrategy( context );
			}
			break;
		case 1:
			// /posts/added
			if( Context.HTTP_GET.equalsIgnoreCase( httpMethod ) )
			{
				String path = urlTokens.nextToken();
				if( Context.URL_POSTS_ADDED.equalsIgnoreCase( path ) )
				{
					return new GetNewPostsStrategy( context );
				}
				else if( Context.URL_POSTS_POPULAR.equalsIgnoreCase( path ) )
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
 * Revision 1.2  2006-05-22 10:52:46  mbork
 * implemented context chooser for /posts
 * Revision 1.1 2006/05/21 20:31:51 mbork continued
 * implementing context
 * 
 */