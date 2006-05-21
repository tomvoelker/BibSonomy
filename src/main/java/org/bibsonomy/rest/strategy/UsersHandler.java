package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

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
import org.bibsonomy.rest.strategy.users.GetUserTagsStrategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class UsersHandler implements ContextHandler 
{
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.ContextHandler#createStrategy(java.lang.StringBuffer)
	 */
	public Strategy createStrategy( Context context, StringTokenizer urlTokens, String httpMethod ) 
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
				if( Context.URL_TAGS.equalsIgnoreCase( req ) )
				{
					// /users/[username]/tags (only GET)
					return new GetUserTagsStrategy( context, userName );
				}
				else if( Context.URL_POSTS.equalsIgnoreCase( req ) )
				{
					// /users/[username]/posts
					return createUserPostsStrategy( context, httpMethod, userName );
				}
				break;
			case 3:
				// /users/[username]/posts/[resourceHash]
				userName = urlTokens.nextToken();
				urlTokens.nextToken();
				String resourceHash = urlTokens.nextToken();
				return createUserPostStrategy( context, httpMethod, userName, resourceHash );
		}
		throw new UnsupportedOperationException( "no strategy for url " );
	}

	private Strategy createUserListStrategy( Context context, String httpMethod ) 
	{
		if( Context.HTTP_GET.equalsIgnoreCase( httpMethod) )
		{
			return new GetUserListStrategy( context );
		}
		else if( Context.HTTP_POST.equalsIgnoreCase( httpMethod) )
		{
			return new PostUserStrategy( context );
		}
		else
		{
			throw new UnsupportedOperationException( "HTTP-" + httpMethod + 
					" not implemented for the UserList Resource " );
		}
	}
	

	private Strategy createUserStrategy( Context context, String httpMethod, String userName ) 
	{
		if( Context.HTTP_GET.equalsIgnoreCase( httpMethod) )
		{
			return new GetUserStrategy( context, userName );
		}
		else if( Context.HTTP_PUT.equalsIgnoreCase( httpMethod) )
		{
			return new PutUserStrategy( context, userName );
		}
		else if( Context.HTTP_DELETE.equalsIgnoreCase( httpMethod) )
		{
			return new DeleteUserStrategy( context, userName );
		}
		else
		{
			throw new UnsupportedOperationException( "HTTP-" + httpMethod + 
			" not implemented for the User Resource" );
		}
	}
	

	private Strategy createUserPostsStrategy( Context context, String httpMethod, String userName ) 
	{
		if( Context.HTTP_GET.equalsIgnoreCase( httpMethod) )
		{
			return new GetUserPostsStrategy( context, userName );
		}
		else if( Context.HTTP_POST.equalsIgnoreCase( httpMethod) )
		{
			return new PostPostStrategy( context, userName );
		}
		else
		{
			throw new UnsupportedOperationException( "HTTP-" + httpMethod + 
			" not implemented for the User-Post Resource" );
		}
	}
	
	private Strategy createUserPostStrategy( Context context, String httpMethod, String userName, String resourceHash ) 
	{
		if( Context.HTTP_GET.equalsIgnoreCase( httpMethod) )
		{
			return new GetPostDetailsStrategy( context, userName, resourceHash );
		}
		else if( Context.HTTP_PUT.equalsIgnoreCase( httpMethod) )
		{
			return new PutPostStrategy( context, userName, resourceHash );
		}
		else if( Context.HTTP_DELETE.equalsIgnoreCase( httpMethod) )
		{
			return new DeletePostStrategy( context, userName, resourceHash );
		}
		else
		{
			throw new UnsupportedOperationException( "HTTP-" + httpMethod + 
			" not implemented for the User Resource" );
		}
	}
}

/*
 * $Log$
 * Revision 1.1  2006-05-21 20:31:51  mbork
 * continued implementing context
 *
 */