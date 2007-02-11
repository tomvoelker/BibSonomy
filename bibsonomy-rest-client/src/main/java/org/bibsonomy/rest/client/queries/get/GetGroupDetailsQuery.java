package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive details about an group of bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetGroupDetailsQuery extends AbstractQuery<Group>
{
	private String groupname;
	private Reader downloadedDocument;

	/**
	 * Gets details of a group.
	 * 
	 * @param groupname name of the user
    * @throws IllegalArgumentException if groupname is null or empty
	 */
	public GetGroupDetailsQuery( String groupname ) throws IllegalArgumentException
	{
		if( groupname == null || groupname.length() == 0 ) throw new IllegalArgumentException( "no groupname given" );
		this.groupname = groupname;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public Group getResult() throws BadRequestOrResponseException, IllegalStateException
	{
		if( downloadedDocument == null ) throw new IllegalStateException( "Execute the query first." );

		try
		{
			return RendererFactory.getRenderer( getRenderingFormat() ).parseGroup( downloadedDocument );
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
		downloadedDocument = performGetRequest( URL_GROUPS + "/" + groupname + "?format=" + getRenderingFormat().toString().toLowerCase() );
	}
}

/*
 * $Log$
 * Revision 1.3  2007-02-11 17:55:34  mbork
 * switched REST-api to the 'new' datamodel, which does not deserve the name...
 *
 * Revision 1.2  2007/02/05 10:35:53  cschenk
 * Distributed code from the spielwiese among the modules
 *
 * Revision 1.1  2006/10/24 21:39:22  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.7  2006/09/24 21:26:20  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.6  2006/09/16 18:19:15  mbork
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