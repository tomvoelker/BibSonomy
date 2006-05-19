package org.bibsonomy.rest.strategy;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.rest.InternServerException;
import org.bibsonomy.rest.ValidationException;


/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class TodoStrategy extends Strategy
{
	public TodoStrategy( Context context )
	{
		super( context );
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Context#createXMLAnswer()
	 */
	@Override
	public void perform( HttpServletRequest request, HttpServletResponse response ) throws InternServerException
	{
		PrintWriter writer;
		try 
		{
			writer = response.getWriter();
			writer.write( "<?xml version=\"1.0\"?>\n<error>Not Yet Implemented</error>" );
			writer.flush();
		} 
		catch (IOException e) 
		{
			throw new InternServerException( e );
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Context#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType( String userAgent )
	{
		if( context.apiIsUserAgent( userAgent ) ) return "bibsonomy/todo+xml";
		return Context.DEFAULT_CONTENT_TYPE;
	}
}

/*
 * $Log$
 * Revision 1.1  2006-05-19 21:01:08  mbork
 * started implementing rest api
 *
 */