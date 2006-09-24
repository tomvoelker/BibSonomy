package org.bibsonomy.rest.strategy.users;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.model.Post;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PutPostStrategy extends Strategy
{
	private String userName;
   private String resourceHash;

   /**
	 * @param context
	 * @param resourceHash 
	 * @param userName 
	 */
	public PutPostStrategy( Context context, String userName, String resourceHash )
	{
		super( context );
      this.userName = userName;
      this.resourceHash = resourceHash;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
      if( !userName.equals( context.getAuthUserName() ) ) throw new ValidationException( "You are not authorized to perform the requested operation" );
   }

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void perform( HttpServletRequest request, StringWriter writer ) throws InternServerException, BadRequestOrResponseException
   {
      try
      {
         Post post = context.getRenderer().parsePost( new InputStreamReader( request.getInputStream() ) );
         // ensure using the right resource...
         if( !post.getResource().getIntraHash().equals( resourceHash ) ) throw new BadRequestOrResponseException( "wrong resource" );
         context.getLogic().storePost( userName, post, true );
      }
      catch( IOException e )
      {
         throw new InternServerException( e );
      }
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType( String userAgent )
	{
      //  TODO no content-contenttype
      return null;
	}
}

/*
 * $Log$
 * Revision 1.7  2006-09-24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.6  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.5  2006/07/05 15:20:13  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 * Revision 1.4  2006/06/28 15:36:13  mbork
 * started implementing other http methods
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