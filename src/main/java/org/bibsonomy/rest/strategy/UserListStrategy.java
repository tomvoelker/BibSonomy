package org.bibsonomy.rest.strategy;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.InternServerException;
import org.bibsonomy.rest.ValidationException;

/**
 * shows all users bibsonomy has
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class UserListStrategy extends Strategy
{

	public UserListStrategy( Context context )
	{
		super( context );
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
		// should be ok
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(java.io.PrintWriter, java.util.Map)
	 */
	@Override
	public void perform( HttpServletRequest request, HttpServletResponse response ) throws InternServerException
	{
		int start = context.getIntAttribute( "start", 1 );
		int end = context.getIntAttribute( "end", 10 );
		String next = "?start=" + String.valueOf( end + 1 ) + "&end=" + String.valueOf( end + 10 ); // TODO
		
		Set<User> users = context.getDatabase().getUsers( context.getAuthUserName(), start - 1 , end - 1 );
		try 
		{
			context.getRenderer().serializeUsers( response.getWriter(), users, start, end, next );
		} 
		catch (IOException e) 
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
		if( context.apiIsUserAgent( userAgent ) ) return "bibsonomy/users+xml";
		return Context.DEFAULT_CONTENT_TYPE;
	}

}

/*
 * $Log$
 * Revision 1.1  2006-05-19 21:01:08  mbork
 * started implementing rest api
 *
 */