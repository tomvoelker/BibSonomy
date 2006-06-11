package org.bibsonomy.rest.strategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.rest.LogicInterface;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.BadRequestException;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class Context
{
	public static final String API_URL = "http://localhost:8080/restTomcat/api/";
	public static final String DEFAULT_CONTENT_TYPE = "text/xml";
	public static final String API_USER_AGENT = "BibsonomyWebServiceClient";

	// URI parts
	public static final String URL_TAGS = "tags";
	public static final String URL_USERS = "users";
	public static final String URL_GROUPS = "groups";
	public static final String URL_POSTS = "posts";
	public static final String URL_POSTS_ADDED = "added";
	public static final String URL_POSTS_POPULAR = "popular";

	private static Map<String, ContextHandler> urlHandlers = new HashMap<String, ContextHandler>();
	static
	{
		Context.urlHandlers.put( URL_TAGS, new TagsHandler() );
		Context.urlHandlers.put( URL_USERS, new UsersHandler() );
		Context.urlHandlers.put( URL_GROUPS, new GroupsHandler() );
		Context.urlHandlers.put( URL_POSTS, new PostsHandler() );
	}

	/**
	 * the authenticated userName, null if none
	 */
	private String authUserName;

	/**
	 * the database
	 */
	private LogicInterface database;
	
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
	 * @param dbAdapter
	 * @param url
	 * @param httpMethod httpMethod used in the request: GET, POST, PUT or DELETE
	 * @param parameterMap map of the attributes
    * @throws BadRequestException if there is no strategy handler for the requested url 
	 */
	public Context( LogicInterface dbAdapter, String httpMethod, String url, Map parameterMap ) throws BadRequestException
	{
		this.database = dbAdapter;
		this.httpMethod = HttpMethod.getHttpMethod(httpMethod);
		this.parameterMap = parameterMap;
		this.urlTokens = new StringTokenizer( url, "/" );
		initStrategy();
      if( this.strategy == null ) throw new BadRequestException( "There is no handler for the requested url: " + url );
	}

	public void initStrategy()
	{
		renderingFormat = RenderingFormat.getRenderingFormat( getStringAttribute( "format", "xml" ) );
		this.renderer = RendererFactory.getRenderer( renderingFormat );

		// choose strategy
		if( urlTokens.countTokens() > 0 )
		{
			ContextHandler contextHandler = Context.urlHandlers.get( urlTokens.nextElement() );
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
	 * @param response the response
	 * @throws InternServerException
	 */
	public void perform( HttpServletRequest request, HttpServletResponse response ) throws InternServerException
	{
		strategy.perform( request, response );
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
		return userAgent != null && userAgent.startsWith( API_USER_AGENT );
	}

	/**
	 * 
	 * @param parameterName parameter (= key) the tags are the value from
	 * @return a list of all tags
	 */
	public Set<String> getTags( String parameterName )
	{
		Set<String> tags = new HashSet<String>();
		
		String param = getStringAttribute( parameterName, "" );
		if( !param.equals( "" ) )
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
	 * @return Returns the database.
	 */
	public LogicInterface getDatabase()
	{
		return database;
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
 * Revision 1.9  2006-06-11 11:51:25  mbork
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