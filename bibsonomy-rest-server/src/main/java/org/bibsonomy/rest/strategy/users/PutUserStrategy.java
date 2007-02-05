package org.bibsonomy.rest.strategy.users;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.gen_model.User;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PutUserStrategy extends Strategy
{
	private String userName;

   /**
	 * @param context
	 * @param userName 
	 */
	public PutUserStrategy( Context context, String userName )
	{
		super( context );
      this.userName = userName;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
	   // ensure username equals auth-username
      if( !userName.equals( context.getAuthUserName() ) ) throw new ValidationException( "The operation is for the logged-in user not permitted." );
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void perform( HttpServletRequest request, StringWriter writer ) throws InternServerException
	{
      try
      {
         User user = context.getRenderer().parseUser( new InputStreamReader( request.getInputStream() ) );
         // ensure to use the right user name
         user.setName( userName );
         context.getLogic().storeUser( user, true );
      }
      catch( IOException e )
      {
         throw new InternServerException( e );
      }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.rest.strategy.Strategy#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType( String userAgent )
	{
		// TODO no content-contenttype
		return null;
	}
}

/*
 * $Log$
 * Revision 1.2  2007-02-05 10:35:54  cschenk
 * Distributed code from the spielwiese among the modules
 *
 * Revision 1.1  2006/10/24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.5  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.4  2006/07/05 15:20:13  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 * Revision 1.3  2006/06/05 14:14:11  mbork
 * implemented GET strategies
 *
 * Revision 1.2  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 */