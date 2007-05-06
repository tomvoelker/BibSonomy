package org.bibsonomy.rest.strategy.users;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Post;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
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
	public void perform( HttpServletRequest request, Writer writer ) throws InternServerException, BadRequestOrResponseException
   {
      try
      {
         Post<?> post = context.getRenderer().parsePost( new InputStreamReader( request.getInputStream() ) );
         // ensure using the right resource... 
         // The 'if' is correct, because if one changes an existing post, 
         // neither the client nor the REST API will calculate the new hash - 
         // this will be done by the logic behind the LogicInterface!
         if( !post.getResource().getIntraHash().equals( resourceHash ) ) throw new BadRequestOrResponseException( "wrong resource" );
         context.getLogic().storePost( userName, post );
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
 * Revision 1.9  2007-05-06 01:39:17  jillig
 * ->changed storePost-signature
 *
 * Revision 1.8  2007/05/01 22:28:47  jillig
 * ->more type-safety with class as resourcetype
 *
 * Revision 1.7  2007/04/15 11:05:07  mbork
 * changed method signature to use a more general Writer
 *
 * Revision 1.6  2007/03/17 20:44:00  mbork
 * the 'if' is correct :)
 *
 * Revision 1.5  2007/02/28 15:21:19  mgrahl
 * additon suggestion
 *
 * Revision 1.4  2007/02/21 14:08:36  mbork
 * - included code generation of the schema in the maven2 build-lifecycle
 * - removed circular dependencies among the modules
 * - cleaned up the poms of the modules
 * - fixed failing unit-tests
 */