package org.bibsonomy.rest.strategy.users;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetPostDetailsStrategy extends Strategy
{
	private String userName;
	private String resourceHash;

	/**
	 * @param context
	 * @param resourceHash 
	 * @param userName 
	 */
	public GetPostDetailsStrategy( Context context, String userName, String resourceHash )
	{
		super( context );
		this.userName = userName;
		this.resourceHash = resourceHash;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
		// TODO check username for existance
		// TODO check resourcehash for existance
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void perform( HttpServletRequest request, HttpServletResponse response )
			throws InternServerException
	{
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType( String userAgent )
	{
		// TODO content type
		return null;
	}

}

/*
 * $Log$
 * Revision 1.2  2006-05-24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 */