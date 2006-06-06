package org.bibsonomy.rest.client.worker;

import java.util.logging.Logger;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.bibsonomy.rest.client.Bibsonomy;

import sun.misc.BASE64Encoder;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public abstract class HttpWorker
{
	public static final Logger LOGGER = Logger.getLogger( Bibsonomy.class.getName() );
	
	public static final String HEADER_USER_AGENT = "User-Agent";
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_AUTH_BASIC = "Basic ";
	
	public static final String USER_AGENT_VALUE = "BibsonomyWebServiceClient";
	public static final String UTF8 = "UTF-8";
	
	private HttpClient httpClient;
	
    protected final String username;
    protected final String password;
    
	public HttpWorker( String username, String password )
	{
        this.username = username;
        this.password = password;
        
        httpClient = new HttpClient();
		HttpClientParams httpClientParams = new HttpClientParams();
		DefaultHttpMethodRetryHandler defaultHttpMethodRetryHandler = new DefaultHttpMethodRetryHandler( 0, false );
		httpClientParams.setParameter( HEADER_USER_AGENT, USER_AGENT_VALUE );
		httpClientParams.setParameter( HttpClientParams.RETRY_HANDLER, defaultHttpMethodRetryHandler );
		httpClientParams.setParameter( HttpClientParams.HTTP_CONTENT_CHARSET, UTF8 );
		httpClientParams.setAuthenticationPreemptive( true );
		httpClient.setParams( httpClientParams );
	}
	
    /**
     * Encode the username and password for BASIC authentication
     *
     * @return Basic + Base64 encoded(username + ':' + password)
     */
    protected String encodeForAuthorization() 
    {
        return HEADER_AUTH_BASIC + new BASE64Encoder().encode( ( username + ":" + password ).getBytes() );
    }

	/**
	 * @return Returns the httpClient.
	 */
    protected HttpClient getHttpClient()
	{
		return httpClient;
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 22:20:54  mbork
 * started implementing client api
 *
 */