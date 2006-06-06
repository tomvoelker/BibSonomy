package org.bibsonomy.rest.client.queries;

import java.util.logging.Logger;

import org.bibsonomy.rest.client.Bibsonomy;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.GetWorker;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;


/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public abstract class AbstractQuery<T>
{
	public static final Logger LOGGER = Logger.getLogger( Bibsonomy.class.getName() );
	
	protected static final String API_URL = "http://localhost:8080/bibsonomy/api/";
	protected static final String URL_TAGS = "tags";
	protected static final String URL_USERS = "users";
	protected static final String URL_GROUPS = "groups";
	protected static final String URL_POSTS = "posts";
	protected static final String URL_POSTS_ADDED = "added";
	protected static final String URL_POSTS_POPULAR = "popular";
	
	private String password;
	private String username;
	private int statusCode = -1;

    protected BibsonomyXML performGetRequest( String url ) throws ErrorPerformingRequestException
    {
    	GetWorker worker = new GetWorker( username, password );
		BibsonomyXML bibsonomyXML = worker.perform( url );
		statusCode = worker.getHttpResult();
		return bibsonomyXML;
    }

    /**
	 * execute this query. the query blocks until a result from the server is
	 * received
	 * 
     * @param username username at bibsonomy.org
     * @param password the user's password
     * @throws ErrorPerformingRequestException if something fails, eg an ioexception occurs (see the cause)
	 */
	public final void execute( String username, String password ) throws ErrorPerformingRequestException
	{
		this.username = username;
		this.password = password;
		doExecute();
	}
	
	protected abstract void doExecute() throws ErrorPerformingRequestException;
	
	/**
     * @return the HTTP status code this query had (only available after execution) 
     */
    public final int getHttpStatusCode()
    {
    	if( this.statusCode == -1 ) throw new IllegalStateException( "Execute the query first." );
    	return statusCode;
    }
    
	/**
	 * @return the result of this query, if there is one.
	 * @throws ErrorPerformingRequestException 
	 */
	public abstract T getResult() throws ErrorPerformingRequestException;
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 22:20:54  mbork
 * started implementing client api
 *
 */