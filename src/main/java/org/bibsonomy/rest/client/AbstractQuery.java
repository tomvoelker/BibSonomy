package org.bibsonomy.rest.client;

import java.util.logging.Logger;

import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;
import org.bibsonomy.rest.client.worker.impl.DeleteWorker;
import org.bibsonomy.rest.client.worker.impl.GetWorker;
import org.bibsonomy.rest.client.worker.impl.PostWorker;
import org.bibsonomy.rest.client.worker.impl.PutWorker;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;


/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public abstract class AbstractQuery<T>
{
	public static final Logger LOGGER = Logger.getLogger( Bibsonomy.class.getName() );
	
	protected static final String URL_TAGS = "tags";
	protected static final String URL_USERS = "users";
	protected static final String URL_GROUPS = "groups";
	protected static final String URL_POSTS = "posts";
	protected static final String URL_POSTS_ADDED = "added";
	protected static final String URL_POSTS_POPULAR = "popular";
	
	private String password;
	private String username;
   private String apiURL;
	private int statusCode = -1;

   protected final BibsonomyXML performGetRequest( String url ) throws ErrorPerformingRequestException
   {
      GetWorker worker = new GetWorker( username, password );
      BibsonomyXML bibsonomyXML = worker.perform( url );
      statusCode = worker.getHttpResult();
      return bibsonomyXML;
   }

   protected final String performRequest( HttpMethod method, String url, String requestBody )
         throws ErrorPerformingRequestException
   {
      HttpWorker worker;
      String result;
      switch( method )
      {
      case POST:
         worker = new PostWorker( username, password );
         result = ( (PostWorker)worker ).perform( apiURL + url, requestBody );
         statusCode = worker.getHttpResult();
         break;
      case DELETE:
         worker = new DeleteWorker( username, password );
         result = ( (DeleteWorker)worker ).perform( apiURL + url );
         statusCode = worker.getHttpResult();
         break;
      case PUT:
         worker = new PutWorker( username, password );
         result = ( (PutWorker)worker ).perform( apiURL + url, requestBody );
         break;
      case GET:
         throw new UnsupportedOperationException( "use AbstractQuery::performGetRequest( String url)" );
      default:
         throw new UnsupportedOperationException( "unsupported operation: " + method.toString() );
      }
      statusCode = worker.getHttpResult();
      return result;
   }

    /**
       * execute this query. the query blocks until a result from the server is received.
       * 
       * @param username
       *           username at bibsonomy.org
       * @param password
       *           the user's password
       * @throws ErrorPerformingRequestException
       *            if something fails, eg an ioexception occurs (see the cause)
       */
   final void execute( String username, String password ) throws ErrorPerformingRequestException
   {
      this.username = username;
      this.password = password;
      doExecute();
   }
	
	protected abstract void doExecute() throws ErrorPerformingRequestException;
	
	/**
     * @return the HTTP status code this query had (only available after execution).
     * @throws IllegalStateException if query has not yet been executed. 
     */
    public final int getHttpStatusCode() throws IllegalStateException
    {
    	if( this.statusCode == -1 ) throw new IllegalStateException( "Execute the query first." );
    	return statusCode;
    }
    
	/**
    * @return the result of this query, if there is one.
    * @throws ErrorPerformingRequestException
    *            if something fails, eg an ioexception occurs (see the cause).
    */
	public abstract T getResult() throws ErrorPerformingRequestException;

   /**
    * @param apiURL The apiURL to set.
    */
   public void setApiURL( String apiURL )
   {
      this.apiURL = apiURL;
   }
}

/*
 * $Log$
 * Revision 1.2  2006-06-14 18:23:22  mbork
 * refactored usage of username, password and host url
 *
 * Revision 1.1  2006/06/08 13:23:48  mbork
 * improved documentation, added throws statements even for runtimeexceptions, moved abstractquery to prevent users to call execute directly
 *
 * Revision 1.6  2006/06/08 07:55:23  mbork
 * moved classes for clearness
 *
 * Revision 1.5  2006/06/08 07:44:36  mbork
 * made two methods final
 *
 * Revision 1.4  2006/06/08 07:41:12  mbork
 * client api completed
 *
 * Revision 1.3  2006/06/07 19:37:28  mbork
 * implemented post queries
 *
 * Revision 1.2  2006/06/07 18:27:04  mbork
 * moved enum
 *
 * Revision 1.1  2006/06/06 22:20:54  mbork
 * started implementing client api
 *
 */