package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class AuthenticationException extends RuntimeException
{
   private static final long serialVersionUID = 1L;

   public AuthenticationException( String message )
   {
      super( message );
   }
}

/*
 * $Log$
 * Revision 1.1  2006-06-11 15:25:26  mbork
 * removed gatekeeper, changed authentication process
 *
 */