package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.rest.strategy.groups.AddUserToGroupStrategy;
import org.bibsonomy.rest.strategy.groups.DeleteGroupStrategy;
import org.bibsonomy.rest.strategy.groups.GetUserListOfGroupStrategy;
import org.bibsonomy.rest.strategy.groups.RemoveUserFromGroupStrategy;
import org.bibsonomy.rest.strategy.groups.GetListOfGroupsStrategy;
import org.bibsonomy.rest.strategy.groups.GetDetailsOfGroupStrategy;
import org.bibsonomy.rest.strategy.groups.AddGroupStrategy;
import org.bibsonomy.rest.strategy.groups.UpdateGroupDetailsStrategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GroupsHandler implements ContextHandler {

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.ContextHandler#createStrategy(java.lang.StringBuffer)
	 */
	public Strategy createStrategy( Context context, StringTokenizer urlTokens, String httpMethod )
	{
		int numTokensLeft = urlTokens.countTokens();
		String groupName;
		switch( numTokensLeft )
		{
			case 0:
				// /groups
				return createGroupListStrategy( context, httpMethod );
			case 1:
				// /groups/[groupname]
				return createGroupStrategy( context, httpMethod, urlTokens.nextToken() );
			case 2:
				groupName = urlTokens.nextToken();
				if( Context.URL_USERS.equalsIgnoreCase( urlTokens.nextToken() ) )
				{
					// /groups/[groupname]/users
					return createUserPostsStrategy( context, httpMethod, groupName );
				}
				break;
			case 3:
				// /groups/[groupname]/users/[username]
				groupName = urlTokens.nextToken();
				if( Context.URL_USERS.equalsIgnoreCase( urlTokens.nextToken() ) )
				{
					if( Context.HTTP_DELETE.equalsIgnoreCase( httpMethod ) )
					{
						return new RemoveUserFromGroupStrategy( context, groupName, urlTokens.nextToken() );
					}
				}
				break;
		}
		throw new UnsupportedOperationException( "no strategy for url " );
	}

	private Strategy createGroupListStrategy( Context context, String httpMethod )
	{
		if( Context.HTTP_GET.equalsIgnoreCase( httpMethod) )
		{
			return new GetListOfGroupsStrategy( context );
		}
		else if( Context.HTTP_POST.equalsIgnoreCase( httpMethod) )
		{
			return new AddGroupStrategy( context );
		}
		else
		{
			throw new UnsupportedOperationException( "HTTP-" + httpMethod + 
					" not implemented for the GroupList Resource " );
		}
	}

	private Strategy createGroupStrategy( Context context, String httpMethod, String groupName )
	{
		if( Context.HTTP_GET.equalsIgnoreCase( httpMethod) )
		{
			return new GetDetailsOfGroupStrategy( context, groupName );
		}
		else if( Context.HTTP_PUT.equalsIgnoreCase( httpMethod) )
		{
			return new UpdateGroupDetailsStrategy( context, groupName );
		}
		else if( Context.HTTP_DELETE.equalsIgnoreCase( httpMethod) )
		{
			return new DeleteGroupStrategy( context, groupName );
		}
		else
		{
			throw new UnsupportedOperationException( "HTTP-" + httpMethod + 
			" not implemented for the Group Resource" );
		}
	}

	private Strategy createUserPostsStrategy( Context context, String httpMethod, String groupName )
	{
		if( Context.HTTP_GET.equalsIgnoreCase( httpMethod) )
		{
			return new GetUserListOfGroupStrategy( context, groupName );
		}
		else if( Context.HTTP_POST.equalsIgnoreCase( httpMethod) )
		{
			return new AddUserToGroupStrategy( context, groupName );
		}
		else
		{
			throw new UnsupportedOperationException( "HTTP-" + httpMethod + 
			" not implemented for the Group Resource" );
		}
	}
}

/*
 * $Log$
 * Revision 1.2  2006-05-22 10:34:38  mbork
 * implemented context chooser for /groups
 *
 * Revision 1.1  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 */