package org.bibsonomy.model;


import java.util.HashSet;
import de.uni_kassel.assocs.impl.DefaultSetRole;
import de.uni_kassel.assocs.SetRole;
import de.uni_kassel.assocs.impl.LinkHandler;

/**
 * generated API doc for class Group
 * 
 **/
public class Group {

   private String description;
    
    public void setDescription (String value)
   {
         this.description = value;
   }

   public String getDescription ()
   {
      return this.description;
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

   /**
    * <pre>
    *           0..n     viewableFor     0..n 
    * Group ------------------------- Post
    *           groups      posts 
    * </pre>
    */
   private  SetRole<Group, Post> posts;

   private static final LinkHandler<Group, Post> POSTS_HANDLER =
         new LinkHandler<Group, Post>()
         {
            public void connect( Post target, Group source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getGroups().add (source);
             }

            public void disconnect( Post target, Group source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getGroups().remove (source);
             }
         };


   public SetRole<Group, Post> getPosts()
   {
      if (posts == null)
      {
         posts = new DefaultSetRole <Group, Post>(new HashSet<Post> (), this, "posts" , POSTS_HANDLER,
                null,
               null);
      }
      return posts;
   }
   /**
    * <pre>
    *           0..n     isMemberOf     0..n 
    * Group ------------------------- User
    *           groups      users 
    * </pre>
    */
   private  SetRole<Group, User> users;

   private static final LinkHandler<Group, User> USERS_HANDLER =
         new LinkHandler<Group, User>()
         {
            public void connect( User target, Group source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getGroups().add (source);
             }

            public void disconnect( User target, Group source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getGroups().remove (source);
             }
         };


   public SetRole<Group, User> getUsers()
   {
      if (users == null)
      {
         users = new DefaultSetRole <Group, User>(new HashSet<User> (), this, "users" , USERS_HANDLER,
                null,
               null);
      }
      return users;
   }
   public void removeYou()
   {
      this.getPosts().clear();
      this.getUsers().clear();
   }
}

