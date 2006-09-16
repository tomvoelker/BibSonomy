package org.bibsonomy.rest.client.queries.put;

import java.io.StringWriter;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to change details of an existing group in bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class ChangeGroupQuery extends AbstractQuery<String>
{
	private boolean executed = false;
	private String result;
	private Group group;
	private String groupName;
	
	/**
    * Changes details of an existing group in bibsonomy.
    * 
    * @param groupName
    *           name of the group to be changed
    * @param group
    *           new values
    * @throws IllegalArgumentException
    *            if groupname is null or empty, or if the group has no name specified
    */
	public ChangeGroupQuery( String groupName, Group group ) throws IllegalArgumentException
   {
      if( groupName == null || groupName.length() == 0 ) throw new IllegalArgumentException( "no groupName given" );
      if( group == null ) throw new IllegalArgumentException( "no group specified" );
      if( group.getName() == null || group.getName().length() == 0 ) throw new IllegalArgumentException( "no groupname specified" );
      this.groupName = groupName;
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
		result = performRequest( HttpMethod.PUT, URL_GROUPS + "/" + groupName + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString() );
	}
}

/*
 * $Log$
 * Revision 1.5  2006-09-16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.4  2006/06/14 18:23:21  mbork
 * refactored usage of username, password and host url
 *
 * Revision 1.3  2006/06/08 13:23:48  mbork
 * improved documentation, added throws statements even for runtimeexceptions, moved abstractquery to prevent users to call execute directly
 *
 * Revision 1.2  2006/06/08 08:02:54  mbork
 * fixed erroneous use of generics
 *
 * Revision 1.1  2006/06/08 07:41:11  mbork
 * client api completed
 *
 * Revision 1.1  2006/06/07 19:37:29  mbork
 * implemented post queries
 *
 */