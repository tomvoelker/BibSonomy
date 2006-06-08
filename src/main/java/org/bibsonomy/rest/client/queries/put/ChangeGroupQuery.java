package org.bibsonomy.rest.client.queries.put;

import java.io.StringWriter;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

/**
 * Use this Class to change details of an existing group in bibsonomy
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
	 * Changes details of an existing group in bibsonomy <p/> both groupname of
	 * the existing group and groupname as parameter for the uri must be
	 * specified, else an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param groupName
	 *            name of the group to be changed
	 * @param group
	 *            new values
	 */
	public ChangeGroupQuery( String groupName, Group group )
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
		XMLRenderer.getInstance().serializeGroup( sw, group, null );
		result = performRequest( HttpMethod.PUT, API_URL + URL_GROUPS + "/" + groupName, sw.toString() );
	}
}

/*
 * $Log$
 * Revision 1.2  2006-06-08 08:02:54  mbork
 * fixed erroneous use of generics
 *
 * Revision 1.1  2006/06/08 07:41:11  mbork
 * client api completed
 *
 * Revision 1.1  2006/06/07 19:37:29  mbork
 * implemented post queries
 *
 */