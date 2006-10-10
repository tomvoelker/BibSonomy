package org.bibsonomy.model;


import java.util.HashSet;
import de.uni_kassel.assocs.impl.DefaultSetRole;
import de.uni_kassel.assocs.SetRole;
import de.uni_kassel.assocs.impl.LinkHandler;

/**
 * generated API doc for class Resource
 * 
 **/
public abstract class Resource {

   private String interHash;
    
    public void setInterHash (String value)
   {
         this.interHash = value;
   }

   public String getInterHash ()
   {
      return this.interHash;
   }

   private String intraHash;
    
    public void setIntraHash (String value)
   {
         this.intraHash = value;
   }

   public String getIntraHash ()
   {
      return this.intraHash;
   }

   /**
    * <pre>
    *           0..1     has     0..n 
    * Resource ------------------------- Post
    *           resource      posts 
    * </pre>
    */
   private  SetRole<Resource, Post> posts;

   private static final LinkHandler<Resource, Post> POSTS_HANDLER =
         new LinkHandler<Resource, Post>()
         {
            public void connect( Post target, Resource source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.setResource (source);
             }

            public void disconnect( Post target, Resource source, String name ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException, ClassCastException
            {
                target.setResource (null);
             }
         };


   public SetRole<Resource, Post> getPosts()
   {
      if (posts == null)
      {
         posts = new DefaultSetRole <Resource, Post>(new HashSet<Post> (), this, "posts" , POSTS_HANDLER,
                null,
               null);
      }
      return posts;
   }
   public void removeYou()
   {
      this.getPosts().clear();
   }
}

