package org.bibsonomy.rest.strategy.posts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.rest.InternServerException;
import org.bibsonomy.rest.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetPopularPostsStrategy extends Strategy
{

	/**
	 * @param context
	 */
	public GetPopularPostsStrategy( Context context )
	{
		super( context );
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void perform( HttpServletRequest request, HttpServletResponse response )
			throws InternServerException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType( String userAgent )
	{
		// TODO Auto-generated method stub
		return null;
	}

}

/*
 * $Log$
 * Revision 1.1  2006-05-22 10:52:46  mbork
 * implemented context chooser for /posts
 *
 */