package org.bibsonomy.rest.client;

import java.io.Reader;
import java.util.logging.Logger;

import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;
import org.bibsonomy.rest.client.worker.impl.DeleteWorker;
import org.bibsonomy.rest.client.worker.impl.GetWorker;
import org.bibsonomy.rest.client.worker.impl.HeadWorker;
import org.bibsonomy.rest.client.worker.impl.PostWorker;
import org.bibsonomy.rest.client.worker.impl.PutWorker;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;


/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public abstract class AbstractQuery<T>
{
	public static final Logger LOGGER = Logger.getLogger( Bibsonomy.class.getName() );
	
	protected static final String URL_TAGS = RestProperties.getInstance().getTagsUrl();
	protected static final String URL_USERS = RestProperties.getInstance().getUsersUrl();
	protected static final String URL_GROUPS = RestProperties.getInstance().getGroupsUrl();
	protected static final String URL_POSTS = RestProperties.getInstance().getPostsUrl();
	protected static final String URL_POSTS_ADDED = RestProperties.getInstance().getAddedPostsUrl();
	protected static final String URL_POSTS_POPULAR = RestProperties.getInstance().getPopularPostsUrl();
	
   private String apiKey;
   private String username;
   private String apiURL;
   private int statusCode = -1;
   private RenderingFormat renderingFormat = RenderingFormat.XML;
   private ProgressCallback callback;


   protected final Reader performGetRequest( String url ) throws ErrorPerformingRequestException
   {
      GetWorker worker = new GetWorker( username, apiKey, callback );
      Reader downloadedDocument = worker.perform( apiURL + url );
      statusCode = worker.getHttpResult();
      return downloadedDocument;
   }

   protected final String performRequest( HttpMethod method, String url, String requestBody )
         throws ErrorPerformingRequestException
   {
      HttpWorker worker;
      String result;
      switch( method )
      {
      case POST:
         worker = new PostWorker( username, apiKey );
         result = ( (PostWorker)worker ).perform( apiURL + url, requestBody );
         statusCode = worker.getHttpResult();
         break;
      case DELETE:
         worker = new DeleteWorker( username, apiKey );
         result = ( (DeleteWorker)worker ).perform( apiURL + url);
         statusCode = worker.getHttpResult();
         break;
      case PUT:
         worker = new PutWorker( username, apiKey );
         result = ( (PutWorker)worker ).perform( apiURL + url, requestBody );
         break;
      case HEAD:
         worker = new HeadWorker( username, apiKey );
         result = ( (HeadWorker)worker ).perform( apiURL + url );
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
       * @param apiKey
       *           the user's password
       * @throws ErrorPerformingRequestException
       *            if something fails, eg an ioexception occurs (see the cause)
       */
   final void execute( String username, String apiKey ) throws ErrorPerformingRequestException
   {
      this.username = username;
      this.apiKey = apiKey;
      doExecute();
   }
	
   /**
    * @throws ErrorPerformingRequestException
    *            if something fails, eg an ioexception occurs (see the cause).
    */
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
    * @throws {@link BadRequestOrResponseException}
    *            if the received data is not valid.
    * @throws {@link IllegalStateException}
    *            if @link {@link #getResult()} gets called before @link {@link Bibsonomy#executeQuery(AbstractQuery)}
    */
	public abstract T getResult() throws BadRequestOrResponseException, IllegalStateException;

   /**
    * @param apiURL The apiURL to set.
    */
   void setApiURL( String apiURL )
   {
      this.apiURL = apiURL;
   }
   
   /**
    * @return the {@link RenderingFormat} to use.
    */
   protected RenderingFormat getRenderingFormat()
   {
	   return this.renderingFormat;
   }

   /**
    * @param renderingFormat the {@link RenderingFormat} to use.
    */
   void setRenderingFormat(RenderingFormat renderingFormat)
   {
	   this.renderingFormat = renderingFormat;
   }

   /**
    * @param callback the {@link ProgressCallback} to inform
    */
   void setProgressCallback( ProgressCallback callback )
   {
      this.callback = callback;
   }
}

/*
 * $Log$
 * Revision 1.3  2007-05-10 20:25:40  mbork
 * api key implemented
 *
 * Revision 1.2  2007/04/19 19:42:46  mbork
 * added the apikey-mechanism to the rest api and added a method to the LogicInterface to validate it.
 *
 * Revision 1.1  2006/10/24 21:39:23  mbork
 * split up rest api into correct modules. verified with junit tests.
 */