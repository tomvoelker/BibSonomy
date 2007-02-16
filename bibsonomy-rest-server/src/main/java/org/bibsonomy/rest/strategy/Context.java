package org.bibsonomy.rest.strategy;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.rest.LogicInterface;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class Context
{
	private static Map<String, ContextHandler> urlHandlers = new HashMap<String, ContextHandler>();
	static
	{
		Context.urlHandlers.put( RestProperties.getInstance().getTagsUrl(), new TagsHandler() );
		Context.urlHandlers.put( RestProperties.getInstance().getUsersUrl(), new UsersHandler() );
		Context.urlHandlers.put( RestProperties.getInstance().getGroupsUrl(), new GroupsHandler() );
		Context.urlHandlers.put( RestProperties.getInstance().getPostsUrl(), new PostsHandler() );
	}

	/**
	 * the authenticated userName, null if none
	 */
	private String authUserName;

	/**
	 * the logic
	 */
	private LogicInterface logic;
	
	/**
	 * the renderer by which the output gets rendered
	 */
	private Renderer renderer;
	
	/**
	 * the currently set strategy
	 */
	private Strategy strategy;
	
	private StringTokenizer urlTokens;
	private Map parameterMap;
	private final HttpMethod httpMethod;
	private RenderingFormat renderingFormat;

	/**
	 * @param logic
	 * @param url
	 * @param httpMethod httpMethod used in the request: GET, POST, PUT or DELETE
	 * @param parameterMap map of the attributes
    * @throws NoSuchResourceException if the requested url doesnt exist 
    * @throws ValidationException if '/' is requested
	 */
	public Context( LogicInterface logic, HttpMethod httpMethod, String url, Map parameterMap ) throws ValidationException, NoSuchResourceException
	{
		this.logic = logic;
		this.httpMethod = httpMethod;
		this.parameterMap = parameterMap;
      if( url == null || "/".equals( url ) ) throw new ValidationException( "It is forbidden to access '/'." );
		this.urlTokens = new StringTokenizer( url, "/" );
		initStrategy();
      if( this.strategy == null ) throw new NoSuchResourceException( "The requested resource does not exist: " + url );
	}

	private void initStrategy()
	{
		renderingFormat = RenderingFormat.getRenderingFormat( getStringAttribute( "format", "xml" ) );
		this.renderer = RendererFactory.getRenderer( renderingFormat );
      
		// choose strategy
		if( urlTokens.countTokens() > 0 )
		{
			String nextElement = (String)urlTokens.nextElement();
         ContextHandler contextHandler = Context.urlHandlers.get( nextElement );
			if( contextHandler != null )
			{
				this.strategy = contextHandler.createStrategy( this, urlTokens, httpMethod );
			}
		}
	}

	/**
	 * validates a strategy: correct userName, etc
	 * @throws ValidationException
	 */
	public void validate() throws ValidationException
	{
		strategy.validate();
	}

	/**
	 * @param request the request
	 * @param responseAdapter the response
	 * @throws InternServerException
	 */
	public void perform( HttpServletRequest request, StringWriter writer ) throws InternServerException
	{
		strategy.perform( request, writer );
	}
	
	/**
	 * @param userAgent
	 * @return the contentType a depending on the userName agent
	 */
	public String getContentType( String userAgent )
	{
		return strategy.getContentType( userAgent );
	}
	
	/**
	 * @param userAgent
	 * @return true if the client uses this webservice api, false if its a browser for example
	 */
	public boolean apiIsUserAgent( String userAgent )
	{
		return userAgent != null && userAgent.startsWith( RestProperties.getInstance().getApiUserAgent() );
	}

	/**
	 * 
	 * @param parameterName parameter (= key) the tags are the value from
	 * @return a list of all tags
	 */
	public List<String> getTags( String parameterName )
	{
      List<String> tags = new LinkedList<String>();
		
		String param = getStringAttribute( parameterName, null );
		if( param != null )
		{
			String[] params = param.split( "\\+" );
			for( int i = 0; i < params.length; ++i )
			{
				tags.add( params[ i ] );
			}
		}
		return tags;
	}
	
	/**
	 * @param parameterName name of the parameter
	 * @param defaultValue
	 * @return paramter value
	 */
	public int getIntAttribute( String parameterName, int defaultValue ) 
	{
		if( parameterMap.containsKey( parameterName ) ) 
		{
			Object obj = parameterMap.get( parameterName );
			if( obj instanceof String[] ) 
			{
				String[] tmp = (String[])obj;
				if( tmp.length == 1 ) 
				{
					try
					{
						int tmpStart = Integer.valueOf( tmp[ 0 ] );
						return tmpStart;
					} 
					catch( NumberFormatException e ) 
					{
						// TODO ignore or throw exception ?
						return defaultValue;
					}
				}
			}
		}
		return defaultValue;
	}
	
	/**
	 * @param parameterName name of the parameter
	 * @param defaultValue
	 * @return paramter value
	 */
	public String getStringAttribute( String parameterName, String defaultValue ) 
	{
		if( parameterMap.containsKey( parameterName ) ) 
		{
			Object obj = parameterMap.get( parameterName );
			if( obj instanceof String[] ) 
			{
				String[] tmp = (String[])obj;
				if( tmp.length == 1 ) 
				{
					return tmp[ 0 ];
				}
			}
		}
		return defaultValue;
	}

	/**
	 * @return Returns the authUserName.
	 */
	public String getAuthUserName()
	{
		return authUserName;
	}

	/**
	 * @param authUserName The authUserName to set.
	 */
	public void setAuthUserName( String authUserName )
	{
		this.authUserName = authUserName;
	}

	/**
	 * @return Returns the renderer.
	 */
	public Renderer getRenderer()
	{
		return renderer;
	}

	/**
	 * @return Returns the logic.
	 */
	public LogicInterface getLogic()
	{
		return logic;
	}

	/**
	 * do not use, only for junit tests
	 * @return Returns the strategy.
	 */
	Strategy getStrategy()
	{
		return strategy;
	}

	/**
	 * @return Returns the renderingFormat.
	 */
	public RenderingFormat getRenderingFormat()
	{
		return renderingFormat;
	}
}

/*
 * $Log$
 * Revision 1.3  2007-02-16 16:11:28  mbork
 * changed default value from "" to null
 *
 * Revision 1.2  2007/02/15 10:29:09  mbork
 * the LogicInterface now uses Lists instead of Sets
 * fixed use of generics
 *
 * Revision 1.1  2006/10/24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:14  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.15  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.14  2006/07/05 15:27:51  mbork
 * place constants on left side of comparison
 *
 * Revision 1.13  2006/06/28 15:36:13  mbork
 * started implementing other http methods
 *
 * Revision 1.12  2006/06/13 21:30:41  mbork
 * implemented unit tests for get-strategies; fixed some minor bugs
 *
 * Revision 1.11  2006/06/13 18:07:39  mbork
 * introduced unit tests for servlet using null-pattern for request and response. tested to use cactus/ httpunit, but decided not to use them.
 *
 * Revision 1.10  2006/06/11 15:25:25  mbork
 * removed gatekeeper, changed authentication process
 *
 * Revision 1.9  2006/06/11 11:51:25  mbork
 * removed todo strategy, throws exception on wrong request url
 *
 * Revision 1.8  2006/06/07 18:27:04  mbork
 * moved enum
 *
 * Revision 1.7  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 * Revision 1.6  2006/05/26 14:02:03  cschenk
 * Forgot to clean up
 *
 * Revision 1.5  2006/05/24 20:09:02  jillig
 * renamed DbInterface to RESTs LogicInterface
 *
 * Revision 1.4  2006/05/24 15:18:08  cschenk
 * Introduced a rendering format and a factory that produces renderers (for xml, rdf, html)
 *
 * Revision 1.3  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.2  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 * Revision 1.1  2006/05/19 21:01:08  mbork
 * started implementing rest api
 *
*/