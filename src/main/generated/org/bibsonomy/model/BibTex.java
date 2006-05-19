package org.bibsonomy.model;



/**
 * generated API doc for class BibTex
 * 
 **/
public class BibTex extends Resource {

   private String authors;
    
    public void setAuthors (String value)
   {
         this.authors = value;
   }

   public String getAuthors ()
   {
      return this.authors;
   }

   private String editors;
    
    public void setEditors (String value)
   {
         this.editors = value;
   }

   public String getEditors ()
   {
      return this.editors;
   }

   private String title;
    
    public void setTitle (String value)
   {
         this.title = value;
   }

   public String getTitle ()
   {
      return this.title;
   }

   private String type;
    
    public void setType (String value)
   {
         this.type = value;
   }

   public String getType ()
   {
      return this.type;
   }

   private String year;
    
    public void setYear (String value)
   {
         this.year = value;
   }

   public String getYear ()
   {
      return this.year;
   }

   /**
    * generated API doc for method toString
    * 
    **/
   public String toString ()
   {

      return getTitle();

   }

}

