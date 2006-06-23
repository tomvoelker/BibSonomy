package org.bibsonomy.rest.client.queries;

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class CheckLoginQuery extends AbstractQuery<String>
{  
   private boolean executed = false;
   private String result;

   /* (non-Javadoc)
    * @see org.bibsonomy.rest.client.AbstractQuery#doExecute()
    */
   @Override
   protected void doExecute() throws ErrorPerformingRequestException
   {
      executed = true;
      result = performRequest( HttpMethod.HEAD, URL_GROUPS, null );
   }

   /* (non-Javadoc)
    * @see org.bibsonomy.rest.client.AbstractQuery#getResult()
    */
   @Override
   public String getResult()
   {
      if( !executed) throw new IllegalStateException( "Execute the query first." );
      return result;
   }
}

/*
 * $Log$
 * Revision 1.1  2006-06-23 20:50:09  mbork
 * clientlib:
 * - added head request
 * - fixed issues with enums using uppercase letters invoked with toString()
 * serverlib:
 * - fixed some issues
 *
 */