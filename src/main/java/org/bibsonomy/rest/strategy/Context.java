package org.bibsonomy.rest.strategy;

import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.database.DbInterface;
import org.bibsonomy.rest.InternServerException;
import org.bibsonomy.rest.ValidationException;
import org.bibsonomy.rest.renderer.HTMLRenderer;
import org.bibsonomy.rest.renderer.RDFRenderer;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.XMLRenderer;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class Context
{
	public static final String API_URL = "http://localhost:8080/restTomcat/api/";
	public static final String DEFAULT_CONTENT_TYPE = "text/xml";
	public static final String API_USER_AGENT = "BibsonomyWebServiceClient";
	
	// some HTTP methods
	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";
	public static final String HTTP_PUT = "PUT";
	public static final String HTTP_DELETE = "DELETE";
	
	// URI parts
	public static final String URL_TAGS = "tags";
	public static final String URL_USERS = "users";
	public static final String URL_GROUPS = "groups";
	public static final String URL_POSTS = "posts";
	public static final String URL_POSTS_ADDED = "added";
	public static final String URL_POSTS_POPULAR = "popular";
	
	/**
	 * the authenticated userName, null if none
	 */
	private String authUserName;

	/**
	 * the database
	 */
	private DbInterface database;
	
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
	private String httpMethod;
	
	/**
	 * @param dbAdapter
	 * @param url
	 * @param httpMethod httpMethod used in the request: GET, POST, PUT or DELETE
	 * @param parameterMap map of the attributes 
	 */
	public Context( DbInterface dbAdapter, String httpMethod, String url, Map parameterMap )
	{
		this.database = dbAdapter;
		this.httpMethod = httpMethod;
		this.parameterMap = parameterMap;
		this.urlTokens = new StringTokenizer( url, "/" );
		initStrategy();
	}

	public void initStrategy()
	{
		// determine which renderer to use
		String format = getStringAttribute( "format", "xml" );
		if( Renderer.FORMAT_HTML.equalsIgnoreCase( format ) )
		{
			this.renderer = new HTMLRenderer();
		}
		else if( Renderer.FORMAT_RDF.equalsIgnoreCase( format ) )
		{
			this.renderer = new RDFRenderer();
		}
		else 
		{
			this.renderer = new XMLRenderer();
		}
		
		// choose strategy
		int numTokens = urlTokens.countTokens();
		String token;
		switch( numTokens ) 
		{
			case 1:
				token = urlTokens.nextToken();
				if( URL_USERS.equalsIgnoreCase( token ) )
				{
					// users => www.bibsonomy.org/ .. /users
					this.strategy = new UserListStrategy( this ); 
				}
				else if( URL_GROUPS.equalsIgnoreCase( token ) )
				{
					// groups => www.bibsonomy.org/ .. /groups
//					this.strategy = new GroupListStrategy( this ); 
				}
				else if( URL_TAGS.equalsIgnoreCase( token ) )
				{
					// tags => www.bibsonomy.org/ .. /tags
//					this.strategy = new TagListStrategy( this ); 
				}
				else if( URL_POSTS.equalsIgnoreCase( token ) )
				{
					// posts => www.bibsonomy.org/ .. /posts
//					this.strategy = new PostListStrategy( this ); 
				}
				break;
			case 2:
				token = urlTokens.nextToken();
				if( URL_USERS.equalsIgnoreCase( token ) )
				{
					// users => www.bibsonomy.org/ .. /users/[username]
//					this.strategy = new UserDetailsStrategy( this, urlTokens.nextToken() ); 
				}
				else if( URL_GROUPS.equalsIgnoreCase( token ) )
				{
					// groups => www.bibsonomy.org/ .. /groups/[groupname]
//					this.strategy = new GroupDetailsStrategy( this, urlTokens.nextToken() ); 
				}
				else if( URL_TAGS.equalsIgnoreCase( token ) )
				{
					// tags => www.bibsonomy.org/ .. /tags/substitute
//					this.strategy = new TagSubstituteStrategy( this ); 
				}
				else if( URL_POSTS.equalsIgnoreCase( token ) )
				{
					if( URL_POSTS_ADDED.equalsIgnoreCase( token ) )
					{
						// posts => www.bibsonomy.org/ .. /posts/added
//						this.strategy = new PostListStrategy( this ); 
					}
					else if( URL_POSTS_POPULAR.equalsIgnoreCase( token ) )
					{
						// posts => www.bibsonomy.org/ .. /posts/popular
//						this.strategy = new PostListStrategy( this );
					}
				}
				break;
			case 3:
				break;
			case 4:
				break;
		}

		if( strategy == null ) strategy = new TodoStrategy( this );
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
	protected boolean apiIsUserAgent( String userAgent )
	{
		return userAgent != null && userAgent.startsWith( API_USER_AGENT );
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
	protected String getAuthUserName()
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
	public DbInterface getDatabase()
	{
		return database;
	}
	
	/**
	 * @return Returns the httpMethod.
	 */
	public String getHttpMethod() {
		return httpMethod;
	}
}

/*
 * $Log$
 * Revision 1.1  2006-05-19 21:01:08  mbork
 * started implementing rest api
 *
*/