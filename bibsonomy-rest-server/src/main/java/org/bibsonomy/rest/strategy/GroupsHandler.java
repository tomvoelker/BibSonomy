package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.groups.AddGroupStrategy;
import org.bibsonomy.rest.strategy.groups.AddUserToGroupStrategy;
import org.bibsonomy.rest.strategy.groups.DeleteGroupStrategy;
import org.bibsonomy.rest.strategy.groups.GetGroupStrategy;
import org.bibsonomy.rest.strategy.groups.GetListOfGroupsStrategy;
import org.bibsonomy.rest.strategy.groups.GetUserListOfGroupStrategy;
import org.bibsonomy.rest.strategy.groups.RemoveUserFromGroupStrategy;
import org.bibsonomy.rest.strategy.groups.UpdateGroupDetailsStrategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GroupsHandler implements ContextHandler 
{
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.ContextHandler#createStrategy(java.lang.StringBuffer)
	 */
	public Strategy createStrategy( Context context, StringTokenizer urlTokens, HttpMethod httpMethod )
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
				if( RestProperties.getInstance().getUsersUrl().equalsIgnoreCase( urlTokens.nextToken() ) )
				{
					// /groups/[groupname]/users
					return createUserPostsStrategy( context, httpMethod, groupName );
				}
				break;
			case 3:
				// /groups/[groupname]/users/[username]
				groupName = urlTokens.nextToken();
				if( RestProperties.getInstance().getUsersUrl().equalsIgnoreCase( urlTokens.nextToken() ) )
				{
					if( HttpMethod.DELETE == httpMethod )
					{
						return new RemoveUserFromGroupStrategy( context, groupName, urlTokens.nextToken() );
					}
				}
				break;
		}
		throw new UnsupportedOperationException( "no strategy for url " );
	}

	private Strategy createGroupListStrategy( Context context, HttpMethod httpMethod )
	{
		switch( httpMethod )
		{
		case GET:
			return new GetListOfGroupsStrategy( context );
		case POST:
			return new AddGroupStrategy( context );
		default:
			throw new UnsupportedHttpMethodException( httpMethod, "GroupList" );
		}
	}

	private Strategy createGroupStrategy( Context context, HttpMethod httpMethod, String groupName )
	{
		switch( httpMethod )
		{
		case GET:
			return new GetGroupStrategy( context, groupName );
		case PUT:
			return new UpdateGroupDetailsStrategy( context, groupName );
		case DELETE:
			return new DeleteGroupStrategy( context, groupName );
		default:
			throw new UnsupportedHttpMethodException( httpMethod, "Group" );
		}
	}

	private Strategy createUserPostsStrategy( Context context, HttpMethod httpMethod, String groupName )
	{
		switch( httpMethod )
		{
		case GET:
			return new GetUserListOfGroupStrategy( context, groupName );
		case POST:
			return new AddUserToGroupStrategy( context, groupName );
		default:
			throw new UnsupportedHttpMethodException( httpMethod, "Group" );
		}
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:13  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.6  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.5  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 * Revision 1.4  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.3  2006/05/22 10:42:25  mbork
 * implemented context chooser for /tags
 *
 * Revision 1.2  2006/05/22 10:34:38  mbork
 * implemented context chooser for /groups
 *
 * Revision 1.1  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 */