package org.bibsonomy.model;


import java.util.HashSet;
import de.uni_kassel.assocs.impl.DefaultSetRole;
import de.uni_kassel.assocs.SetRole;
import de.uni_kassel.assocs.impl.LinkHandler;

/**
 * generated API doc for class Post
 * 
 **/
public class Post {

   private String description;
    
    public void setDescription (String value)
   {
         this.description = value;
   }

   public String getDescription ()
   {
      return this.description;
   }

   private long postingDate;
    
    public void setPostingDate (long value)
   {
         this.postingDate = value;
   }

   public long getPostingDate ()
   {
      return this.postingDate;
   }

   /**
    * <pre>
    *           0..n     has     0..1 
    * Post ------------------------- Resource
    *           posts      resource 
    * </pre>
    */
   private Resource resource;

   public boolean setResource (Resource value)
   {
      boolean changed = false;

      if (this.resource != value)
      {
         
         Resource oldValue = this.resource;
         Post source = this;
         if (this.resource != null)
         {
            this.resource = null;
            oldValue.getPosts().remove (source);
         }
         this.resource = value;

         if (value != null)
         {
            value.getPosts().add (source);
         }
         changed = true;
         
      }
      return changed;
   }

   public Resource getResource ()
   {
      return this.resource;
   }

   /**
    * <pre>
    *           0..n     have     0..n 
    * Post ------------------------- Tag
    *           posts      tags 
    * </pre>
    */
   private  SetRole<Post, Tag> tags;

   private static final LinkHandler<Post, Tag> TAGS_HANDLER =
         new LinkHandler<Post, Tag>()
         {
            public void connect( Tag target, Post source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getPosts().add (source);
             }

            public void disconnect( Tag target, Post source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getPosts().remove (source);
             }
         };


   public SetRole<Post, Tag> getTags()
   {
      if (tags == null)
      {
         tags = new DefaultSetRole <Post, Tag>(new HashSet<Tag> (), this, "tags" , TAGS_HANDLER,
                null,
               null);
      }
      return tags;
   }
   /**
    * <pre>
    *           0..n     posts     0..1 
    * Post ------------------------- User
    *           posts      user 
    * </pre>
    */
   private User user;

   public boolean setUser (User value)
   {
      boolean changed = false;

      if (this.user != value)
      {
         
         User oldValue = this.user;
         Post source = this;
         if (this.user != null)
         {
            this.user = null;
            oldValue.getPosts().remove (source);
         }
         this.user = value;

         if (value != null)
         {
            value.getPosts().add (source);
         }
         changed = true;
         
      }
      return changed;
   }

   public User getUser ()
   {
      return this.user;
   }

   /**
    * <pre>
    *           0..n     viewableFor     0..n 
    * Post ------------------------- Group
    *           posts      groups 
    * </pre>
    */
   private  SetRole<Post, Group> groups;

   private static final LinkHandler<Post, Group> GROUPS_HANDLER =
         new LinkHandler<Post, Group>()
         {
            public void connect( Group target, Post source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getPosts().add (source);
             }

            public void disconnect( Group target, Post source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getPosts().remove (source);
             }
         };


   public SetRole<Post, Group> getGroups()
   {
      if (groups == null)
      {
         groups = new DefaultSetRole <Post, Group>(new HashSet<Group> (), this, "groups" , GROUPS_HANDLER,
                null,
               null);
      }
      return groups;
   }
   public void removeYou()
   {
      this.setResource (null);
      this.getTags().clear();
      this.setUser (null);
      this.getGroups().clear();
   }
}

