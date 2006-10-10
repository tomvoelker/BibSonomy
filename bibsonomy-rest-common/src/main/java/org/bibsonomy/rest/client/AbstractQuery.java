package org.bibsonomy.rest.client;

import java.io.Reader;
import java.util.logging.Logger;

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
   private RenderingFormat renderingFormat = RenderingFormat.XML;
   private ProgressCallback callback;

   protected final Reader performGetRequest( String url ) throws ErrorPerformingRequestException
   {
      GetWorker worker = new GetWorker( username, password, callback );
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
      case HEAD:
         worker = new HeadWorker( username, password );
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
 * Revision 1.1  2006-10-10 12:42:15  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.5  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.4  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.3  2006/06/23 20:50:09  mbork
 * clientlib:
 * - added head request
 * - fixed issues with enums using uppercase letters invoked with toString()
 * serverlib:
 * - fixed some issues
 *
 * Revision 1.2  2006/06/14 18:23:22  mbork
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