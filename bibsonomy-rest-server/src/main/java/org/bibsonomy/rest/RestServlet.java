package org.bibsonomy.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.database.LogicInterface;
import org.bibsonomy.database.managers.RestDatabaseManager;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.AuthenticationException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;

import sun.misc.BASE64Decoder;


/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class RestServlet extends HttpServlet
{
   private static final long serialVersionUID = 1L;
	
	private LogicInterface logic;

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException
	{
		super.init();
		// instantiate the bibsonomy database connection
      logic = RestDatabaseManager.getInstance();
	}
   
   /**
    * Use this setter in junit tests to initialize the test database.
    */
   void setLogicInterface( LogicInterface logicInterface )
   {
	   this.logic = logicInterface;
   }
   
   /**
    * Use this class in junit tests to access the test-database
    * 
    * @return the {@link LogicInterface}
    */
   LogicInterface getLogic()
   {
      return logic;
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
      handle( request, response, HttpMethod.GET );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
   public void doPut( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
      handle( request, response, HttpMethod.PUT );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
   public void doDelete( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
      handle( request, response, HttpMethod.DELETE );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
   public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
      handle( request, response, HttpMethod.POST );
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
   public void doHead( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		validateAuthorization( request.getHeader( "Authorization" ) );
	}


   /**
    * @param request the servletrequest
    * @param response the servletresponse
    * @param method httpMethod to use, see {@link HttpMethod}
    * @throws IOException
    */
   private void handle( HttpServletRequest request, HttpServletResponse response, HttpMethod method ) throws IOException
   {
      try
      {
         // validate the requesting user's authorization
         String username = validateAuthorization( request.getHeader( "Authorization" ) );

         // create Context 
         Context context = new Context( this.logic, method, request.getPathInfo(), request.getParameterMap() );
         context.setAuthUserName( username );

         // validate request
         context.validate();

         // set some response headers
         response.setContentType( context.getContentType( request.getHeader( "User-Agent" ) ) );
         response.setCharacterEncoding( "UTF-8" );

         // send answer
         response.setStatus( HttpServletResponse.SC_OK );
         ByteArrayOutputStream cachingStream = new ByteArrayOutputStream();
         Writer writer = new OutputStreamWriter( cachingStream, Charset.forName( "UTF-8" ) );

         context.perform( request, writer );
         // attention: cachingStream.size() != cachingStream.toString().length() !!
         // the correct value is the first one!
         response.setContentLength( cachingStream.size() );
         response.getOutputStream().print( cachingStream.toString( "UTF-8" ) );
      }
      catch( AuthenticationException e )
      {
         response.setHeader( "WWW-Authenticate", "Basic realm=\"BibsonomyWebService\"" );
         response.sendError( HttpURLConnection.HTTP_UNAUTHORIZED, e.getMessage() );
      }
      catch( InternServerException e )
      {
         response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
      }
      catch( NoSuchResourceException e )
      {
         response.sendError( HttpServletResponse.SC_NOT_FOUND, e.getMessage() );
      }
      catch( BadRequestOrResponseException e )
      {
         response.sendError( HttpServletResponse.SC_BAD_REQUEST, e.getMessage() );
      }
      catch( ValidationException e )
      {
         response.sendError( HttpServletResponse.SC_FORBIDDEN, e.getMessage() );
      }
   }
   
	/**
	 * @param authentication Authentication-value of the header's request
	 * @throws IOException
	 */
	String validateAuthorization( String authentication ) throws AuthenticationException
	{
      if( authentication == null || !authentication.startsWith( "Basic " ) )
      {
         throw new AuthenticationException( "Please authenticate yourself." );
      }
      String basicCookie;
      
      try
      {
         BASE64Decoder decoder = new BASE64Decoder();
         basicCookie = new String( decoder.decodeBuffer( authentication.substring( 6 ) ) );
      }
      catch( IOException e )
      {
         throw new BadRequestOrResponseException( "error decoding authorization header: " + e.toString() );
      }
      int i = basicCookie.indexOf( ':' );
      if( i < 0 )
      {
         throw new BadRequestOrResponseException( "error decoding authorization header: syntax error" );
      }
      String username = basicCookie.substring( 0, i );
      String apiKey = basicCookie.substring( i + 1 );
      
      // check username and password
      if( !logic.validateUserAccess( username, apiKey ) )
      {
         throw new AuthenticationException( "Please authenticate yourself." );
      }
      return username;
   }
}

/*
 * $Log$
 * Revision 1.13  2007-05-10 20:25:40  mbork
 * api key implemented
 *
 * Revision 1.12  2007/04/19 19:42:46  mbork
 * added the apikey-mechanism to the rest api and added a method to the LogicInterface to validate it.
 *
 * Revision 1.11  2007/04/19 16:47:54  rja
 * introduced bug
 *
 * Revision 1.9  2007/04/19 13:30:40  rja
 * fixed a bug concerning Tests
 *
 * Revision 1.8  2007/04/15 11:05:39  mbork
 * fixed a bug concerning UTF-8 characters. Added a test
 *
 * Revision 1.7  2007/04/03 14:18:53  rja
 * changed name
 *
 * Revision 1.6  2007/02/21 14:08:35  mbork
 * - included code generation of the schema in the maven2 build-lifecycle
 * - removed circular dependencies among the modules
 * - cleaned up the poms of the modules
 * - fixed failing unit-tests
 *
 * Revision 1.5  2007/02/16 16:11:28  mbork
 * changed default value from "" to null
 *
 * Revision 1.3  2007/02/12 12:08:16  mgrahl
 * *** empty log message ***
 *
 * Revision 1.2  2007/02/11 19:38:00  mbork
 * successfully switched to the database interface.
 *
 * Revision 1.1  2006/10/24 21:39:53  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:15  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.10  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.9  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.8  2006/06/28 15:36:13  mbork
 * started implementing other http methods
 *
 * Revision 1.7  2006/06/13 18:07:40  mbork
 * introduced unit tests for servlet using null-pattern for request and response. tested to use cactus/ httpunit, but decided not to use them.
 *
 * Revision 1.6  2006/06/11 15:25:26  mbork
 * removed gatekeeper, changed authentication process
 *
 * Revision 1.5  2006/06/11 11:51:25  mbork
 * removed todo strategy, throws exception on wrong request url
 *
 * Revision 1.4  2006/06/06 17:39:30  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 * Revision 1.3  2006/05/24 20:09:03  jillig
 * renamed DbInterface to RESTs LogicInterface
 *
 * Revision 1.2  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/19 21:01:09  mbork
 * started implementing rest api
 *
 */
