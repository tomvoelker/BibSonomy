package org.bibsonomy.rest.strategy.groups;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserListOfGroupStrategy extends Strategy
{

	private String groupName;

	/**
	 * @param context
	 * @param groupName 
	 */
	public GetUserListOfGroupStrategy( Context context, String groupName )
	{
		super( context );
		this.groupName = groupName;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
		// TODO check if the authuser is allowed to the the groupmembers
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void perform( HttpServletRequest request, HttpServletResponse response ) throws InternServerException
	{
		// setup viewModel
		int start = context.getIntAttribute( "start", 0 );
		int end = context.getIntAttribute( "end", 19 );
		String next = Context.API_URL + "/" + Context.URL_GROUPS + "/" + groupName + "/" + Context.URL_USERS
				+ "?start=" + String.valueOf( end + 1 ) + "&end=" + String.valueOf( end + 10 );
		
		ViewModel viewModel = new ViewModel();
		viewModel.setStartValue( start );
		viewModel.setEndValue( end );
		viewModel.setUrlToNextResources( next );
		
		// delegate to the renderer
		Set<User> users = context.getLogic().getUsers( context.getAuthUserName(), groupName, start, end );
		try 
		{
			context.getRenderer().serializeUsers( response.getWriter(), users, viewModel );
		} 
		catch( IOException e ) 
		{
			throw new InternServerException( e );
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType( String userAgent )
	{
		if( context.apiIsUserAgent( userAgent ) ) return "bibsonomy/users+" + context.getRenderingFormat().toString();
		return Context.DEFAULT_CONTENT_TYPE;
	}
}

/*
 * $Log$
 * Revision 1.4  2006-06-11 15:25:25  mbork
 * removed gatekeeper, changed authentication process
 *
 * Revision 1.3  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 * Revision 1.2  2006/05/24 13:02:43  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/22 10:34:38  mbork
 * implemented context chooser for /groups
 *
 */