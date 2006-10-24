package org.bibsonomy.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Some Properties for the REST Webservice.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
@SuppressWarnings( "serial" )
public class RestProperties extends Properties
{
   private static RestProperties properties = null;

   /* key names */
   private static final String PROPERTY_API_URL = "RestApiURL";
   private static final String PROPERTY_CONTENT_TYPE = "DefaultContentType";
   private static final String PROPERTY_API_USER_AGENT = "UserAgentOfAPI";
   private static final String PROPERTY_URL_TAGS = "TagsURL";
   private static final String PROPERTY_URL_USERS = "UsersURL";
   private static final String PROPERTY_URL_GROUPS = "GroupsURL";
   private static final String PROPERTY_URL_POSTS = "PostsURL";
   private static final String PROPERTY_URL_ADDED_POSTS = "AddedPostsURL";
   private static final String PROPERTY_URL_POPULAR_POSTS = "PopularPostsURL";

   /* default values */
   private static final String DEFAULT_API_URL = "http://localhost:8080/restTomcat/api/";
   private static final String DEFAULT_CONTENT_TYPE = "text/xml";
   private static final String DEFAULT_API_USER_AGENT = "BibsonomyWebServiceClient";
   private static final String DEFAULT_URL_TAGS = "tags";
   private static final String DEFAULT_URL_USERS = "users";
   private static final String DEFAULT_URL_GROUPS = "groups";
   private static final String DEFAULT_URL_POSTS = "posts";
   private static final String DEFAULT_URL_ADDED_POSTS = "added";
   private static final String DEFAULT_URL_POPULAR_POSTS = "popular";

   /* some internals */
   private static final String CONFIGFILE = "RestConfig.cfg";

   private RestProperties()
   {
      super();
   }

   private RestProperties( Properties properties )
   {
      super( properties );
      // store();
   }

   public static RestProperties getInstance()
   {
      if( properties == null )
      {
         Properties prop = new Properties();
         try
         {
            File f = new File( CONFIGFILE );
            if( f.exists() )
            {
               prop.load( new FileInputStream( f ) );
            }
            else
            {
//               System.err.println( "RestProperties.getInstance()" );
//               System.err.println( "could not find config file." );
//               System.exit( -1 );
               // f.createNewFile();
            }
         }
         catch( FileNotFoundException e )
         {
            e.printStackTrace();
         }
         catch( IOException e )
         {
            e.printStackTrace();
         }
         properties = new RestProperties( prop );

      }
      return properties;
   }

   public void store()
   {
      try
      {
         super.store( new FileOutputStream( new File( CONFIGFILE ) ), "" );
      }
      catch( FileNotFoundException e )
      {
         e.printStackTrace();
      }
      catch( IOException e )
      {
         e.printStackTrace();
      }
   }

   public String getApiUrl()
   {
      String apiURL = DEFAULT_API_URL;
      if( getProperty( PROPERTY_API_URL ) != null )
      {
         apiURL = getProperty( PROPERTY_API_URL ).trim();
      }
      return apiURL;
   }

   public String getContentType()
   {
      String contentType = DEFAULT_CONTENT_TYPE;
      if( getProperty( PROPERTY_CONTENT_TYPE ) != null )
      {
         contentType = getProperty( PROPERTY_CONTENT_TYPE ).trim();
      }
      return contentType;
   }

   public String getApiUserAgent()
   {
      String apiUserAgent = DEFAULT_API_USER_AGENT;
      if( getProperty( PROPERTY_API_USER_AGENT ) != null )
      {
         apiUserAgent = getProperty( PROPERTY_API_USER_AGENT ).trim();
      }
      return apiUserAgent;
   }

   public String getTagsUrl()
   {
      String urlTags = DEFAULT_URL_TAGS;
      if( getProperty( PROPERTY_URL_TAGS ) != null )
      {
         urlTags = getProperty( PROPERTY_URL_TAGS ).trim();
      }
      return urlTags;
   }

   public String getUsersUrl()
   {
      String urlUsers = DEFAULT_URL_USERS;
      if( getProperty( PROPERTY_URL_USERS ) != null )
      {
         urlUsers = getProperty( PROPERTY_URL_USERS ).trim();
      }
      return urlUsers;
   }

   public String getGroupsUrl()
   {
      String urlGroups = DEFAULT_URL_GROUPS;
      if( getProperty( PROPERTY_URL_GROUPS ) != null )
      {
         urlGroups = getProperty( PROPERTY_URL_GROUPS ).trim();
      }
      return urlGroups;
   }

   public String getPostsUrl()
   {
      String urlPosts = DEFAULT_URL_POSTS;
      if( getProperty( PROPERTY_URL_POSTS ) != null )
      {
         urlPosts = getProperty( PROPERTY_URL_POSTS ).trim();
      }
      return urlPosts;
   }

   public String getAddedPostsUrl()
   {
      String urlAddedPosts = DEFAULT_URL_ADDED_POSTS;
      if( getProperty( PROPERTY_URL_ADDED_POSTS ) != null )
      {
         urlAddedPosts = getProperty( PROPERTY_URL_ADDED_POSTS ).trim();
      }
      return urlAddedPosts;
   }

   public String getPopularPostsUrl()
   {
      String urlPopularPosts = DEFAULT_URL_POPULAR_POSTS;
      if( getProperty( PROPERTY_URL_POPULAR_POSTS ) != null )
      {
         urlPopularPosts = getProperty( PROPERTY_URL_POPULAR_POSTS ).trim();
      }
      return urlPopularPosts;
   }
}

/*
 * $Log$
 * Revision 1.1  2006-10-24 21:39:28  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.4  2005/12/03 11:30:32  manuel
 * alpha
 *
 * Revision 1.3  2005/12/02 00:11:17  manuel
 * release candidate
 *
 * Revision 1.2  2005/11/30 22:45:49  manuel
 * almost stable
 *
 * Revision 1.1  2005/11/29 23:46:27  manuel
 * first programmings
 *
 */