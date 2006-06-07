package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

/**
 * Use this Class to create a new group in bibsonomy
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class CreateGroupQuery extends AbstractQuery
{
	private boolean executed = false;
	private String result;
	private Group group;

	/**
	 * Creates a new group account in bibsonomy
	 * <p/>
	 * an {@link IllegalArgumentException} is thrown, if the groupname is missing
	 * 
	 * @param group
	 *            the group to be created
	 */
	public CreateGroupQuery( Group group )
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
		XMLRenderer.getInstance().serializeGroup( sw, group, null );
		result = performPostRequest( API_URL + URL_GROUPS, sw.toString() );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-07 19:37:29  mbork
 * implemented post queries
 *
 */