package org.bibsonomy.rest.strategy.users;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.InternServerException;
import org.bibsonomy.rest.ValidationException;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * shows all users bibsonomy has
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserListStrategy extends Strategy
{
	public GetUserListStrategy( Context context )
	{
		super( context );
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
		// should be ok for everybody
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(java.io.PrintWriter, java.util.Map)
	 */
	@Override
	public void perform( HttpServletRequest request, HttpServletResponse response ) throws InternServerException
	{
		// setup viewModel
		int start = context.getIntAttribute( "start", 0 );
		int end = context.getIntAttribute( "end", 19 );
		String next = Context.API_URL + "/" + Context.URL_USERS + "?start=" + String.valueOf( end + 1 ) + 
			"&end=" + String.valueOf( end + 10 );
		
		ViewModel viewModel = new ViewModel();
		viewModel.setStartValue( start );
		viewModel.setEndValue( end );
		viewModel.setUrlToNextResources( next );
		
		// delegate to the renderer
		Set<User> users = context.getDatabase().getUsers( context.getAuthUserName(), start, end );
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
	{// TODO: contentType
		if( context.apiIsUserAgent( userAgent ) ) return "bibsonomy/users+xml";
		return Context.DEFAULT_CONTENT_TYPE;
	}

}

/*
 * $Log$
 * Revision 1.1  2006-05-21 20:31:51  mbork
 * continued implementing context
 *
 * Revision 1.1  2006/05/19 21:01:08  mbork
 * started implementing rest api
 *
 */