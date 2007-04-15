package org.bibsonomy.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class NullResponse implements HttpServletResponse
{
   private ServletOutputStream servletOutputStream;
   private PrintWriter printWriter;
   private StringWriter content;
   
   public String getContent()
   {
      return content.toString();
   }

   public ServletOutputStream getOutputStream() throws IOException
   {
      if( servletOutputStream == null )
      {
         servletOutputStream = new ServletOutputStream()
         {
            @Override
            public void write( int b ) throws IOException
            {
               getWriter().write( b );
            }
         };
      }
      return servletOutputStream;
   }
   
   public PrintWriter getWriter() throws IOException
   {
      if( printWriter == null )
      {
         content = new StringWriter(); 
         printWriter = new PrintWriter( content );
      }
      return printWriter;
   }

   private int contentLength;

   public void setContentLength( int arg0 )
   {
      contentLength = arg0;
   }

   /**
    * @return the contentLength
    */
   public int getContentLength()
   {
      return contentLength;
   }
   
   public void addCookie( Cookie arg0 )
   {
   }

   public boolean containsHeader( String arg0 )
   {
      return false;
   }

   public String encodeURL( String arg0 )
   {
      return null;
   }

   public String encodeRedirectURL( String arg0 )
   {
      return null;
   }

   public String encodeUrl( String arg0 )
   {
      return null;
   }

   public String encodeRedirectUrl( String arg0 )
   {
      return null;
   }

   public void sendError( int arg0, String arg1 ) throws IOException
   {
      throw new RuntimeException( "code: " + arg0 + " message: " + arg1 );
   }

   public void sendError( int arg0 ) throws IOException
   {
      throw new RuntimeException( "code: " + arg0 );
   }

   public void sendRedirect( String arg0 ) throws IOException
   {

   }

   public void setDateHeader( String arg0, long arg1 )
   {

   }

   public void addDateHeader( String arg0, long arg1 )
   {

   }

   public void setHeader( String arg0, String arg1 )
   {

   }

   public void addHeader( String arg0, String arg1 )
   {

   }

   public void setIntHeader( String arg0, int arg1 )
   {

   }

   public void addIntHeader( String arg0, int arg1 )
   {

   }

   public void setStatus( int arg0 )
   {

   }

   public void setStatus( int arg0, String arg1 )
   {

   }

   public String getCharacterEncoding()
   {
      return null;
   }

   public String getContentType()
   {
      return null;
   }

   public void setCharacterEncoding( String arg0 )
   {

   }

   public void setContentType( String arg0 )
   {

   }

   public void setBufferSize( int arg0 )
   {

   }

   public int getBufferSize()
   {
      return 0;
   }

   public void flushBuffer() throws IOException
   {

   }

   public void resetBuffer()
   {

   }

   public boolean isCommitted()
   {
      return false;
   }

   public void reset()
   {

   }

   public void setLocale( Locale arg0 )
   {

   }

   public Locale getLocale()
   {
      return null;
   }
}

/*
 * $Log$
 * Revision 1.2  2007-04-15 11:05:39  mbork
 * fixed a bug concerning UTF-8 characters. Added a test
 *
 * Revision 1.1  2006/10/24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:14  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.2  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.1  2006/06/13 18:07:40  mbork
 * introduced unit tests for servlet using null-pattern for request and response. tested to use cactus/ httpunit, but decided not to use them.
 *
 */