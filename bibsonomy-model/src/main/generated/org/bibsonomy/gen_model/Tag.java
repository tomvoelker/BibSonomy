package org.bibsonomy.gen_model;


import java.util.HashSet;

import de.uni_kassel.assocs.impl.DefaultSetRole;
import de.uni_kassel.assocs.SetRole;
import de.uni_kassel.assocs.impl.LinkHandler;

/**
 * generated API doc for class Tag
 * 
 **/
public class Tag {

   private int globalcount;
    
    public void setGlobalcount (int value)
   {
         this.globalcount = value;
   }

   public int getGlobalcount ()
   {
      return this.globalcount;
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

   private int usercount;
    
    public void setUsercount (int value)
   {
         this.usercount = value;
   }

   public int getUsercount ()
   {
      return this.usercount;
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
    *           0..n     have     0..n 
    * Tag ------------------------- Post
    *           tags      posts 
    * </pre>
    */
   private  SetRole<Tag, Post> posts;

   private static final LinkHandler<Tag, Post> POSTS_HANDLER =
         new LinkHandler<Tag, Post>()
         {
            public void connect( Post target, Tag source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getTags().add (source);
             }

            public void disconnect( Post target, Tag source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getTags().remove (source);
             }
         };


   public SetRole<Tag, Post> getPosts()
   {
      if (posts == null)
      {
         posts = new DefaultSetRole <Tag, Post>(new HashSet<Post> (), this, "posts" , POSTS_HANDLER,
                null,
               null);
      }
      return posts;
   }
   /**
    * <pre>
    *           0..n     subTagOf     0..n 
    * Tag ------------------------- Tag
    *           superTags      subTags 
    * </pre>
    */
   private  SetRole<Tag, Tag> subTags;

   private static final LinkHandler<Tag, Tag> SUBTAGS_HANDLER =
         new LinkHandler<Tag, Tag>()
         {
            public void connect( Tag target, Tag source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getSuperTags().add (source);
             }

            public void disconnect( Tag target, Tag source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getSuperTags().remove (source);
             }
         };


   public SetRole<Tag, Tag> getSubTags()
   {
      if (subTags == null)
      {
         subTags = new DefaultSetRole <Tag, Tag>(new HashSet<Tag> (), this, "subTags" , SUBTAGS_HANDLER,
                null,
               null);
      }
      return subTags;
   }
   /**
    * <pre>
    *           0..n     subTagOf     0..n 
    * Tag ------------------------- Tag
    *           subTags      superTags 
    * </pre>
    */
   private  SetRole<Tag, Tag> superTags;

   private static final LinkHandler<Tag, Tag> SUPERTAGS_HANDLER =
         new LinkHandler<Tag, Tag>()
         {
            public void connect( Tag target, Tag source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getSubTags().add (source);
             }

            public void disconnect( Tag target, Tag source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.getSubTags().remove (source);
             }
         };


   public SetRole<Tag, Tag> getSuperTags()
   {
      if (superTags == null)
      {
         superTags = new DefaultSetRole <Tag, Tag>(new HashSet<Tag> (), this, "superTags" , SUPERTAGS_HANDLER,
                null,
               null);
      }
      return superTags;
   }
   public void removeYou()
   {
      this.getPosts().clear();
      this.getSubTags().clear();
      this.getSuperTags().clear();
   }
}

