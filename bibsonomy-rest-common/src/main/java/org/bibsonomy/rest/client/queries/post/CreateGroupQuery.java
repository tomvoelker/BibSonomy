package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to create a new group in bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class CreateGroupQuery extends AbstractQuery<String>
{
	private boolean executed = false;
	private String result;
	private Group group;

	/**
    * Creates a new group account in bibsonomy.
    * 
    * @param group
    *           the group to be created
    * @throws IllegalArgumentException
    *            if the group has no name is defined
    */
	public CreateGroupQuery( Group group ) throws IllegalArgumentException
	{
		if( group == null ) throw new IllegalArgumentException( "no group specified" );
		if( group.getName() == null || group.getName().length() == 0 ) throw new IllegalArgumentException( "no groupname specified" );
		this.group = group;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public String getResult()
	{
		if( !executed) throw new IllegalStateException( "Execute the query first." );
		return result;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#doExecute()
	 */
	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		executed = true;
		StringWriter sw = new StringWriter( 100 );
		RendererFactory.getRenderer( getRenderingFormat() ).serializeGroup( sw, group, null );
		result = performRequest( HttpMethod.POST, URL_GROUPS + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString() );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-10 12:42:16  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.6  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.5  2006/06/14 18:23:21  mbork
 * refactored usage of username, password and host url
 *
 * Revision 1.4  2006/06/08 13:23:47  mbork
 * improved documentation, added throws statements even for runtimeexceptions, moved abstractquery to prevent users to call execute directly
 *
 * Revision 1.3  2006/06/08 08:02:54  mbork
 * fixed erroneous use of generics
 *
 * Revision 1.2  2006/06/08 07:41:12  mbork
 * client api completed
 *
 * Revision 1.1  2006/06/07 19:37:29  mbork
 * implemented post queries
 *
 */