package org.bibsonomy.rest.client;

import java.util.logging.Logger;

import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;

/**
 * Bibsonomy is a class for accessing the <a href="http://www.bibsonomy.org/api/">Bibsonomy REST API</a>.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class Bibsonomy
{
   public static final Logger LOGGER = Logger.getLogger( Bibsonomy.class.getName() );
   
   private String apiURL = "http://www.bibsonomy.org/api/";
   private String username;
   private String password;

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
    * @throws IllegalArgumentException
    *            if username or password is null or empty
    */
   public Bibsonomy( String username, String password ) throws IllegalArgumentException
   {
      if( username == null || username.length() == 0 ) throw new IllegalArgumentException( "The given username is not valid." );
      if( password == null || password.length() == 0 ) throw new IllegalArgumentException( "The given password is not valid." );
      this.username = username;
      this.password = password;
   }

   /**
    * executes the given query.
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
      query.setApiURL( this.apiURL );
      query.execute( username, password );
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
    * this is the accessor method for the apiurl. That is the url pointing to the REST webservice. 
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
}

/*
 * $Log$
 * Revision 1.4  2006-06-14 18:23:22  mbork
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