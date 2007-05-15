package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;
import java.util.List;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive an ordered list of all users belonging to a given group.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetUserListOfGroupQuery extends AbstractQuery<List<User>>
{
	private String groupname;
	private int start;
	private int end;
	private Reader downloadedDocument;

	/**
	 * Gets an user list of a group
	 */
	public GetUserListOfGroupQuery( String groupname )
	{
		this( groupname, 0, 19 );
	}

	/**
    * Gets an user list of a group.
    * 
    * @param start
    *           start of the list
    * @param end
    *           end of the list
    * @throws IllegalArgumentException
    *            if the groupname is null or empty
    */
	public GetUserListOfGroupQuery( String groupname, int start, int end ) throws IllegalArgumentException
	{
		if( groupname == null || groupname.length() == 0 ) throw new IllegalArgumentException( "no groupname given" );
		if( start < 0 ) start = 0;
		if( end < start ) end = start;
		
		this.groupname = groupname;
		this.start = start;
		this.end = end;
	}

	@Override
	public List<User> getResult() throws BadRequestOrResponseException, IllegalStateException
	{
		if( downloadedDocument == null ) throw new IllegalStateException( "Execute the query first." );
		return RendererFactory.getRenderer( getRenderingFormat() ).parseUserList( downloadedDocument );
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		downloadedDocument = performGetRequest( URL_GROUPS + "/" + groupname + "/" + URL_USERS + "?start=" + start
				+ "&end=" + end + "&format=" + getRenderingFormat().toString().toLowerCase()  );
	}
}

/*
 * $Log$
 * Revision 1.4  2007-05-15 08:45:56  mbork
 * code walk-through
 *
 * Revision 1.3  2007/02/11 17:55:34  mbork
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
 * Revision 1.6  2006/09/24 21:26:20  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.5  2006/09/16 18:19:15  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
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