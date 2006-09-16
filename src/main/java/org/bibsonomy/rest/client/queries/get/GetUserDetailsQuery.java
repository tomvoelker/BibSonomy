package org.bibsonomy.rest.client.queries.get;

import java.io.InputStream;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive details about an user of bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetUserDetailsQuery extends AbstractQuery<User>
{
	private String username;
	private InputStream responseAsStream;

	/**
	 * Gets details of a user.
	 * 
	 * @param username name of the user
    * @throws IllegalArgumentException if username is null or empty
	 */
	public GetUserDetailsQuery( String username ) throws IllegalArgumentException
	{
		if( username == null || username.length() == 0 ) throw new IllegalArgumentException( "no username given" );
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public User getResult() throws BadRequestOrResponseException, IllegalStateException
	{
		if( responseAsStream == null ) throw new IllegalStateException( "Execute the query first." );

		try
		{
			return RendererFactory.getRenderer( getRenderingFormat() ).parseUser( responseAsStream );
		}
		catch( BadRequestOrResponseException e )
		{
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#doExecute()
	 */
	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		responseAsStream = performGetRequest( URL_USERS + "/" + username + "?format=" + getRenderingFormat().toString().toLowerCase() );
	}
}

/*
 * $Log$
 * Revision 1.6  2006-09-16 18:19:15  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.5  2006/06/23 20:50:08  mbork
 * clientlib:
 * - added head request
 * - fixed issues with enums using uppercase letters invoked with toString()
 * serverlib:
 * - fixed some issues
 *
 * Revision 1.4  2006/06/14 18:23:21  mbork
 * refactored usage of username, password and host url
 *
 * Revision 1.3  2006/06/08 13:23:47  mbork
 * improved documentation, added throws statements even for runtimeexceptions, moved abstractquery to prevent users to call execute directly
 *
 * Revision 1.2  2006/06/08 08:02:54  mbork
 * fixed erroneous use of generics
 *
 * Revision 1.1  2006/06/06 22:20:54  mbork
 * started implementing client api
 *
 */