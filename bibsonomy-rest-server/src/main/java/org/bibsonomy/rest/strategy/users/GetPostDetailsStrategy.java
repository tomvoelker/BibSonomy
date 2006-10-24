package org.bibsonomy.rest.strategy.users;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.model.Post;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetPostDetailsStrategy extends Strategy
{
	private String userName;
	private String resourceHash;

	/**
	 * @param context
	 * @param resourceHash 
	 * @param userName 
	 */
	public GetPostDetailsStrategy( Context context, String userName, String resourceHash )
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
		// TODO check username for existance - or should the request then just return an empty entry?
		// TODO check resourcehash for existance - or should the request then just return an empty entry?
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void perform( HttpServletRequest request, StringWriter writer ) throws InternServerException, NoSuchResourceException
	{
		// delegate to the renderer
		Post post = context.getLogic().getPostDetails( context.getAuthUserName(), resourceHash, userName );
      if( post == null )
      {
         throw new NoSuchResourceException( "The requested post for the hash '" + resourceHash
               + "' of the requested user '" + userName + "' does not exist." );
      }
		context.getRenderer().serializePost( writer, post, new ViewModel() );
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType( String userAgent )
	{
		if( context.apiIsUserAgent( userAgent ) ) return "bibsonomy/post+" + context.getRenderingFormat().toString();
		return RestProperties.getInstance().getContentType();
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.7  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.6  2006/06/13 21:30:41  mbork
 * implemented unit tests for get-strategies; fixed some minor bugs
 *
 * Revision 1.5  2006/06/13 18:07:40  mbork
 * introduced unit tests for servlet using null-pattern for request and response. tested to use cactus/ httpunit, but decided not to use them.
 *
 * Revision 1.4  2006/06/11 15:25:25  mbork
 * removed gatekeeper, changed authentication process
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