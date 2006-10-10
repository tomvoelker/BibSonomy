package org.bibsonomy.model;



/**
 * generated API doc for class Bookmark
 * 
 **/
public class Bookmark extends Resource {

   private String url;
    
    public void setUrl (String value)
   {
         this.url = value;
   }

   public String getUrl ()
   {
      return this.url;
   }

   /**
    * generated API doc for method toString
    * 
    **/
   public String toString ()
   {

      return getUrl();

   }

   /**
    * generated API doc for method getInterHash
    * 
    **/
   public String getInterHash ()
   {

      return this.getIntraHash();

   }

}

