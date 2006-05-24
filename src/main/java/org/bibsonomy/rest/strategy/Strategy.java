package org.bibsonomy.rest.strategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public abstract class Strategy
{
	protected final Context context;
	
	public Strategy( Context context )
	{
		this.context = context;
	}
	
	/**
	 * validates a state: correct userName, etc
	 * @throws ValidationException
	 */
	public abstract void validate() throws ValidationException;

	/**
	 * @param request
	 * @param response
	 * @throws InternServerException
	 */
	public abstract void perform( HttpServletRequest request, HttpServletResponse response ) throws InternServerException;

	/**
	 * @param userAgent
	 * @return the contentType of the answer document
	 */
	public abstract String getContentType( String userAgent );
}

/*
 * $Log$
 * Revision 1.2  2006-05-24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/19 21:01:08  mbork
 * started implementing rest api
 *
 */