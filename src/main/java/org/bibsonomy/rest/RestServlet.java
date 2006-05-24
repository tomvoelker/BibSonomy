package org.bibsonomy.rest;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.database.DbInterface;
import org.bibsonomy.database.TestDatabase;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;


/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class RestServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private Gatekeeper gatekeeper;
	private DbInterface dbAdapter;

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException
	{
		super.init();
		gatekeeper = new Gatekeeper();
		dbAdapter = new TestDatabase();
	}
	
	/**
	 * Respond to a GET request for the content produced by this servlet.
	 * 
	 * @param request              The servlet request we are processing
	 * @param response             The servlet response we are producing
	 * 
	 * @exception IOException      if an input/output error occurs
	 * @exception ServletException if a servlet error occurs
	 */
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException
	{
		// validate the requesting user's authorization
		if( !validateAuthorization( request, response ) ) return;
		
		// create Context 
		Context context = new Context( this.dbAdapter, "GET", request.getPathInfo(),request.getParameterMap() );
		context.setAuthUserName( gatekeeper.getLoggedInUsersName( request ) );
		
		try
		{
			// validate request
			context.validate();
			
			// set some response headers
			response.setContentType( context.getContentType( request.getHeader( "User-Agent" ) ) );
			response.setCharacterEncoding( "UTF-8" );
			
			// send answer
			context.perform( request, response );
		}
		catch( InternServerException e )
		{
			response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
		}
		catch( ValidationException e )
		{
			response.sendError( HttpServletResponse.SC_FORBIDDEN, e.getMessage() );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		validateAuthorization( request, response );
		super.doPut( request, response );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doDelete( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		validateAuthorization( request, response );
		super.doDelete( request, response );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		validateAuthorization( request, response );
		super.doPost( request, response );
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		validateAuthorization( request, response );
	}

	/**
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private boolean validateAuthorization( HttpServletRequest request, HttpServletResponse response ) throws IOException
	{
		int code = gatekeeper.isAccessAllowed( request );
		if( code != HttpURLConnection.HTTP_OK )
		{
			response.setHeader( "WWW-Authenticate", "Basic realm=\"BibsonomyWebService\"" );
			response.sendError( gatekeeper.isAccessAllowed( request ) );
			return false;
		}
		response.setStatus( HttpServletResponse.SC_OK );
		return true;
	}
}

/*
 * $Log$
 * Revision 1.2  2006-05-24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/19 21:01:09  mbork
 * started implementing rest api
 *
 */
