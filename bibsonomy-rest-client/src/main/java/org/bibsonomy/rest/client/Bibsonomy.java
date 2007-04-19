package org.bibsonomy.rest.client;

import java.util.logging.Logger;

import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.get.GetPostsQuery;
import org.bibsonomy.rest.enums.RenderingFormat;

/**
 * Bibsonomy is a class for accessing the <a href="http://www.bibsonomy.org/api/">Bibsonomy REST API</a>.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class Bibsonomy
{
   public static final Logger LOGGER = Logger.getLogger( Bibsonomy.class.getName() );
   
   private String apiURL = RestProperties.getInstance().getApiUrl();
   private String username;
   private String password;
   private String apiKey;
   private RenderingFormat renderingFormat = RenderingFormat.XML;


   /**
    * Creates an object to interact with Bibsonomy.
    * Remember to set {@link Bibsonomy#username} and {@link Bibsonomy#password} via
    * their accessor methods.
    */
   public Bibsonomy()
   {
   }
   
   /**
    * Creates an object to interact with Bibsonomy.
    * 
    * @param username
    *           name of the user
    * @param password
    *           password of the user
    * @param apiKey
    *           apikey of the user
    * @throws IllegalArgumentException
    *            if username or password is null or empty
    */
   public Bibsonomy( String username, String password, String apiKey ) throws IllegalArgumentException
   {
      if( username == null || username.length() == 0 ) throw new IllegalArgumentException( "The given username is not valid." );
      if( password == null || password.length() == 0 ) throw new IllegalArgumentException( "The given password is not valid." );
      if( apiKey == null || apiKey.length() == 0 ) throw new IllegalArgumentException( "The given apiKey is not valid." );
      this.username = username;
      this.password = password;
      this.apiKey = apiKey;
   }

   /**
    * Executes the given query.
    * 
    * @param query
    *           the query to execute
    * @throws ErrorPerformingRequestException
    *            if something fails, eg an ioexception occurs (see the cause)
    * @throws IllegalStateException
    *            if the username or the password has not yet been set
    */
   public void executeQuery( AbstractQuery query ) throws ErrorPerformingRequestException, IllegalStateException
   {
      if( username == null ) throw new IllegalStateException( "The username has not yet been set." );
      if( password == null ) throw new IllegalStateException( "The password has not yet been set." );
      query.setRenderingFormat( this.renderingFormat );
      query.setApiURL( this.apiURL );
      query.execute( username, password, apiKey );
   }

   /**
    * Executes the given query and notifies the callback on progress. Note that the callback only
    * gets informed if the Query is kind of a Get-Query, a {@link GetPostsQuery}, for example.
    * 
    * @param query
    *           the query to execute
    * @param callback
    *           the callback object to inform
    * @throws ErrorPerformingRequestException
    *            if something fails, eg an ioexception occurs (see the cause)
    * @throws IllegalStateException
    *            if the username or the password has not yet been set
    */
   public void executeQuery( AbstractQuery query, ProgressCallback callback ) throws ErrorPerformingRequestException, IllegalStateException
   {
      query.setProgressCallback( callback );
      executeQuery( query );
   }
   
   /**
    * @param password
    *           The password to set.
    * @throws IllegalArgumentException
    *            if the given password is null or empty
    */
   public void setPassword( String password ) throws IllegalArgumentException
   {
      if( password == null || password.length() == 0 ) throw new IllegalArgumentException( "The given password is not valid." );
      this.password = password;
   }

   /**
    * @param username
    *           The username to set.
    * @throws IllegalArgumentException
    *            if the given username is null or empty
    */
   public void setUsername( String username ) throws IllegalArgumentException
   {
      if( username == null || username.length() == 0 ) throw new IllegalArgumentException( "The given username is not valid." );
      this.username = username;
   }

   /**
    * @param apiKey the apiKey to set.
    * @throws IllegalArgumentException
    *            if the given apiKey is null or empty
    */
   public void setApiKey( String apiKey ) throws IllegalArgumentException
   {
      if( apiKey == null || apiKey.length() == 0 ) throw new IllegalArgumentException( "The given apiKey is not valid." );
      this.apiKey = apiKey;
   }
   
   /**
    * This is the accessor method for the apiurl. That is the url pointing to the REST webservice. 
    * It defaults to <i>http://www.bibsonomy.org/api/</i>. If no trailing slash is given it is
    * appended automatically
    * 
    * @param apiURL
    *           The apiURL to set.
    * @throws IllegalArgumentException
    *            if the given url is null or empty
    */
   public void setApiURL( String apiURL ) throws IllegalArgumentException
   {
      if( apiURL == null || apiURL.length() == 0 ) throw new IllegalArgumentException( "The given apiURL is not valid." );
      if( apiURL.equals( "/" ) ) throw new IllegalArgumentException( "The given apiURL is not valid." );
      if( !apiURL.endsWith( "/" ) ) apiURL += "/";
      this.apiURL = apiURL;
   }

   /**
    * Sets the {@link RenderingFormat} to use. Note that currently only the {@link RenderingFormat#XML} is
    * supported (which is set by default), so this method is only intended for future releases.
    * <br/>
    * Setting the RenderingFormat to some other value than {@link RenderingFormat#XML} will cause
    * an {@link UnsupportedOperationException} to be thrown, at the moment.
    *  
    * @param renderingFormat The {@link RenderingFormat} to use.
    */
   public void setRenderingFormat(RenderingFormat renderingFormat) 
   {
      if( renderingFormat != RenderingFormat.XML )
      {
         throw new UnsupportedOperationException("Currently only the xml rendering format is implemented.");
      }
      this.renderingFormat = renderingFormat;
   }
}

/*
 * $Log$
 * Revision 1.2  2007-04-19 19:42:46  mbork
 * added the apikey-mechanism to the rest api and added a method to the LogicInterface to validate it.
 *
 * Revision 1.1  2006/10/24 21:39:23  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:15  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.6  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.5  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.4  2006/06/14 18:23:22  mbork
 * refactored usage of username, password and host url
 *
 * Revision 1.3  2006/06/08 16:14:36  mbork
 * Implemented some XMLRenderer functions, including unit-tests. introduced djunitplugin (see http://works.dgic.co.jp/djunit/index.html)
 *
 * Revision 1.2  2006/06/08 13:23:48  mbork
 * improved documentation, added throws statements even for runtimeexceptions, moved abstractquery to prevent users to call execute directly
 *
 * Revision 1.1  2006/06/06 22:20:55  mbork
 * started implementing client api
 *
 */