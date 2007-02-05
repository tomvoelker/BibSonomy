package org.bibsonomy.gen_model;


import java.util.HashSet;

import org.bibsonomy.gen_model.Group;
import org.bibsonomy.gen_model.User;

import de.uni_kassel.assocs.impl.DefaultSetRole;
import de.uni_kassel.assocs.SetRole;
import de.uni_kassel.assocs.impl.LinkHandler;

/**
 * generated API doc for class User
 * 
 **/
public class User {

   private String email;
    
    public void setEmail (String value)
   {
         this.email = value;
   }

   public String getEmail ()
   {
      return this.email;
   }

   private String homepage;
    
    public void setHomepage (String value)
   {
         this.homepage = value;
   }

   public String getHomepage ()
   {
      return this.homepage;
   }

   private String name;
    
    public void setName (String value)
   {
         this.name = value;
   }

   public String getName ()
   {
      return this.name;
   }

   private String password;
    
    public void setPassword (String value)
   {
         this.password = value;
   }

   public String getPassword ()
   {
      return this.password;
   }

   private String realname;
    
    public void setRealname (String value)
   {
         this.realname = value;
   }

   public String getRealname ()
   {
      return this.realname;
   }

   private long timestamp;
    
    public void setTimestamp (long value)
   {
         this.timestamp = value;
   }

   public long getTimestamp ()
   {
      return this.timestamp;
   }

   /**
    * generated API doc for method toString
    * 
    **/
   public String toString ()
   {

      return getName();

   }

   /**
    * <pre>
    *           0..1     posts     0..n 
    * User ------------------------- Post
    *           user      posts 
    * </pre>
    */
   private  SetRole<User, Post> posts;

   private static final LinkHandler<User, Post> POSTS_HANDLER =
         new LinkHandler<User, Post>()
         {
            public void connect( Post target, User source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.setUser (source);
             }

            public void disconnect( Post target, User source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.setUser (null);
             }
         };


   public SetRole<User, Post> getPosts()
   {
      if (posts == null)
      {
         posts = new DefaultSetRole <User, Post>(new HashSet<Post> (), this, "posts" , POSTS_HANDLER,
                null,
               null);
      }
      return posts;
   }
   /**
    * <pre>
    *           0..n     isMemberOf     0..n 
    * User ------------------------- Group
    *           users      groups 
    * </pre>
    */
   private  SetRole<User, Group> groups;

   private static final LinkHandler<User, Group> GROUPS_HANDLER =
         new LinkHandler<User, Group>()
         {
            public void connect( Group target, User source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getUsers().add (source);
             }

            public void disconnect( Group target, User source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getUsers().remove (source);
             }
         };


   public SetRole<User, Group> getGroups()
   {
      if (groups == null)
      {
         groups = new DefaultSetRole <User, Group>(new HashSet<Group> (), this, "groups" , GROUPS_HANDLER,
                null,
               null);
      }
      return groups;
   }
   public void removeYou()
   {
      this.getPosts().clear();
      this.getGroups().clear();
   }
}

