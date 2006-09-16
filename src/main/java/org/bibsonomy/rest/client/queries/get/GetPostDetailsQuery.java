package org.bibsonomy.rest.client.queries.get;

import java.io.InputStream;

import org.bibsonomy.model.Post;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive details about a post of an user.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetPostDetailsQuery extends AbstractQuery<Post>
{
	private String username;
	private String resourceHash;
	private InputStream responseAsStream;

	/**
    * Gets details of a post of an user.
    * 
    * @param username
    *           name of the user
    * @param resourceHash
    *           hash of the resource
    * @throws IllegalArgumentException
    *            if userName or resourceHash are null or empty
    */
	public GetPostDetailsQuery( String username, String resourceHash ) throws IllegalArgumentException
	{
		if( username == null || username.length() == 0 ) throw new IllegalArgumentException( "no username given" );
		if( resourceHash == null || resourceHash.length() == 0 ) throw new IllegalArgumentException( "no resourceHash given" );
		this.username = username;
		this.resourceHash = resourceHash;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public Post getResult() throws BadRequestOrResponseException, IllegalStateException
	{
		if( responseAsStream == null ) throw new IllegalStateException( "Execute the query first." );

		try
		{
			return RendererFactory.getRenderer( getRenderingFormat() ).parsePost( responseAsStream );
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
		responseAsStream = performGetRequest( URL_USERS + "/" + username + "/" + URL_POSTS + "/" + resourceHash  + "?format=" + getRenderingFormat().toString().toLowerCase() );
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