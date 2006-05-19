package org.bibsonomy.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import sun.misc.BASE64Decoder;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class Gatekeeper
{
	private Map<String, String> database;
	
	public Gatekeeper()
	{
		database = new HashMap<String, String>();
		database.put( "mbork", "test" );
	}

	public int isAccessAllowed( HttpServletRequest request )
	{
		Auth auth = new Auth();
		int code = auth.isAuthorized( request );
		if( code != HttpURLConnection.HTTP_OK )
		{
			return code;
		}
		return checkPassword( auth );
	}
	
	/**
	 * @param request
	 * @return the username or null, if there is something wrong
	 * @throws BadRequestException
	 */
	public String getLoggedInUsersName( HttpServletRequest request )
	{
		Auth auth = new Auth();
		int code = auth.isAuthorized( request );
		if( code != HttpURLConnection.HTTP_OK )
		{
			return null;
		}
		
		if( checkPassword( auth ) != HttpURLConnection.HTTP_OK ) return null;
		return auth.username;
	}

	private int checkPassword( Auth auth )
	{
		if( auth.username == null ) return HttpURLConnection.HTTP_UNAUTHORIZED;
		if( auth.password == null ) return HttpURLConnection.HTTP_UNAUTHORIZED;
		String truePassword = database.get( auth.username );
		if( truePassword != null && truePassword.equals( auth.password ) )
		{
			return HttpURLConnection.HTTP_OK;
		}
		return HttpURLConnection.HTTP_UNAUTHORIZED;
	}
	
	private class Auth
	{
		private String username;
		private String password;
		
		private int isAuthorized( HttpServletRequest request )
		{
			String authorization = request.getHeader( "Authorization" );
			if( authorization == null || !authorization.startsWith( "Basic " ) )
			{
				return HttpURLConnection.HTTP_UNAUTHORIZED;
			}
			synchronized( this )
			{
				// update database here
//				long modificationTime = m_databaseFile.lastModified();
//				if( modificationTime <= 0 || modificationTime > m_databaseFileModificationTime )
//				{
//					loadDatabase();
//				}
//				database = m_database;
			}
			String basicCookie;
			try
			{
				BASE64Decoder decoder = new BASE64Decoder();
				basicCookie = new String( decoder.decodeBuffer( authorization.substring( 6 ) ) );
			}
			catch( IOException e )
			{
//				throw new BadRequestException( "[" + getClass().getName() + ".checkPassword] error "
//						+ "decoding authorization header: " + e.toString() );
				return HttpURLConnection.HTTP_BAD_REQUEST;
			}
			int i = basicCookie.indexOf( ':' );
			if( i < 0 )
			{
//				throw new BadRequestException( "[" + getClass().getName() + ".checkPassword] error "
//						+ "decoding authorization header: syntax error" );
				return HttpURLConnection.HTTP_BAD_REQUEST;
			}
			this.username = basicCookie.substring( 0, i );
			this.password = basicCookie.substring( i + 1 );
			return HttpURLConnection.HTTP_OK;
		}
	}
}

/*
 * $Log$
 * Revision 1.1  2006-05-19 21:01:09  mbork
 * started implementing rest api
 *
 */