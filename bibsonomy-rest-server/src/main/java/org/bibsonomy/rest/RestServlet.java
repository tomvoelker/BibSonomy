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

import org.apache.log4j.Logger;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.database.LogicInterface;
import org.bibsonomy.database.managers.RestDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.AuthenticationException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
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
	  final Logger log = Logger.getLogger(RestServlet.class);	  
	  log.debug("Incoming URL:" + request.getRequestURL());
	   
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
         sendError( request, response, HttpURLConnection.HTTP_UNAUTHORIZED, e.getMessage() );
      }
      catch( InternServerException e )
      {
         sendError( request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
      }
      catch( NoSuchResourceException e )
      {
         sendError( request, response, HttpServletResponse.SC_NOT_FOUND, e.getMessage() );
      }
      catch( BadRequestOrResponseException e )
      {
         sendError( request, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage() );
      }
      catch( ValidationException e )
      {
         sendError( request, response, HttpServletResponse.SC_FORBIDDEN, e.getMessage() );
      }
      catch( Exception e )
      {
         // well, lets fetch each error..
         sendError( request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
      }
   }
   
   /**
    * Sends an error to the client.
    * 
    * @param request the current {@link HttpServletRequest} object.
    * @param response the current {@link HttpServletResponse} object.
    * @param code the error code to send.
    * @param message the message to send.
    * @throws IOException 
    * @throws  
    */
   private void sendError( HttpServletRequest request, HttpServletResponse response, int code, String message ) throws IOException
   {
      // get renderer
      String renderingFormatName = Context.getStringAttribute( request.getParameterMap(), "format", "xml" );
      RenderingFormat renderingFormat = RenderingFormat.getRenderingFormat( renderingFormatName );
      Renderer renderer = RendererFactory.getRenderer( renderingFormat );
      
      // send error
      response.setStatus( code );
      ByteArrayOutputStream cachingStream = new ByteArrayOutputStream();
      Writer writer = new OutputStreamWriter( cachingStream, Charset.forName( "UTF-8" ) );
      renderer.serializeError( writer, message );
      response.setContentLength( cachingStream.size() );
      response.getOutputStream().print( cachingStream.toString( "UTF-8" ) );
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