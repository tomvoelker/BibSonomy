package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.users.DeletePostStrategy;
import org.bibsonomy.rest.strategy.users.DeleteUserStrategy;
import org.bibsonomy.rest.strategy.users.GetPostDetailsStrategy;
import org.bibsonomy.rest.strategy.users.GetUserListStrategy;
import org.bibsonomy.rest.strategy.users.GetUserPostsStrategy;
import org.bibsonomy.rest.strategy.users.GetUserStrategy;
import org.bibsonomy.rest.strategy.users.PostPostStrategy;
import org.bibsonomy.rest.strategy.users.PostUserStrategy;
import org.bibsonomy.rest.strategy.users.PutPostStrategy;
import org.bibsonomy.rest.strategy.users.PutUserStrategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class UsersHandler implements ContextHandler 
{
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.ContextHandler#createStrategy(java.lang.StringBuffer)
	 */
	public Strategy createStrategy( Context context, StringTokenizer urlTokens, HttpMethod httpMethod ) 
	{
		int numTokensLeft = urlTokens.countTokens();
		String userName;
		switch( numTokensLeft )
		{
			case 0:
				// /users
				return createUserListStrategy( context, httpMethod );
			case 1:
				// /users/[username]
				return createUserStrategy( context, httpMethod, urlTokens.nextToken() );
			case 2:
				userName = urlTokens.nextToken();
				String req = urlTokens.nextToken();
				if( Context.URL_POSTS.equalsIgnoreCase( req ) )
				{
					// /users/[username]/posts
					return createUserPostsStrategy( context, httpMethod, userName );
				}
				break;
			case 3:
				// /users/[username]/posts/[resourceHash]
				userName = urlTokens.nextToken();
				if( Context.URL_POSTS.equalsIgnoreCase( urlTokens.nextToken() ) )
				{
					String resourceHash = urlTokens.nextToken();
					return createUserPostStrategy( context, httpMethod, userName, resourceHash );
				}
				break;
		}
		throw new UnsupportedOperationException( "no strategy for url " );
	}

	private Strategy createUserListStrategy( Context context, HttpMethod httpMethod ) 
	{
		switch (httpMethod) {
		case GET:
			return new GetUserListStrategy(context);
		case POST:
			return new PostUserStrategy(context);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "UserList");
		}
	}

	private Strategy createUserStrategy( Context context, HttpMethod httpMethod, String userName ) 
	{
		switch (httpMethod) {
		case GET:
			return new GetUserStrategy(context, userName);
		case PUT:
			return new PutUserStrategy(context, userName);
		case DELETE:
			return new DeleteUserStrategy(context, userName);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "User");
		}
	}

	private Strategy createUserPostsStrategy( Context context, HttpMethod httpMethod, String userName ) 
	{
		switch (httpMethod) {
		case GET:
			return new GetUserPostsStrategy(context, userName);
		case POST:
			return new PostPostStrategy(context, userName);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "User-Post");
		}
	}

	private Strategy createUserPostStrategy( Context context, HttpMethod httpMethod, String userName, String resourceHash ) 
	{
		switch (httpMethod) {
		case GET:
			return new GetPostDetailsStrategy(context, userName, resourceHash);
		case PUT:
			return new PutPostStrategy(context, userName, resourceHash);
		case DELETE:
			return new DeletePostStrategy(context, userName, resourceHash);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "User");
		}
	}
}

/*
 * $Log$
 * Revision 1.3  2006-05-24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.2  2006/05/22 10:34:38  mbork
 * implemented context chooser for /groups
 *
 * Revision 1.1  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 */