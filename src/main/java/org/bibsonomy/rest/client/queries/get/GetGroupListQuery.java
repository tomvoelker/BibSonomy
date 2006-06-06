package org.bibsonomy.rest.client.queries.get;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.exceptions.InvalidXMLException;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.ModelFactory;

/**
 * Use this Class to receive an ordered list of all groups bibsonomy has
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetGroupListQuery extends AbstractQuery
{
	private int start;
	private int end;
	private BibsonomyXML bibsonomyXML;

	/**
	 * Gets bibsonomy's group list
	 */
	public GetGroupListQuery()
	{
		this( 0, 19 );
	}

	/**
	 * Gets bibsonomy's group list
	 * 
	 * @param start start of the list
	 * @param end end of the list
	 */
	public GetGroupListQuery( int start, int end )
	{
		if( start < 0 ) throw new IllegalArgumentException( "start must be >= 0" );
		if( end < start ) throw new IllegalArgumentException( "end must be >= 0 and >= start value" );
		
		this.start = start;
		this.end = end;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public List<Group> getResult()
	{
		if( bibsonomyXML == null ) throw new IllegalStateException( "Execute the query first." );
		
		List<Group> groups = new ArrayList<Group>();

		if( bibsonomyXML.getGroups() != null && bibsonomyXML.getGroups().getGroup() != null )
		{
			for( GroupType xmlGroup: bibsonomyXML.getGroups().getGroup() )
			{
				try
				{
					groups.add( ModelFactory.getInstance().createGroup( xmlGroup ) );
				}
				catch( InvalidXMLException e )
				{
					LOGGER.log( Level.WARNING, e.getMessage(), e );
				}
			}
		}
		return groups;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#doExecute()
	 */
	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		bibsonomyXML = performGetRequest( API_URL + URL_GROUPS + "?start=" + start + "&end=" + end );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 22:20:54  mbork
 * started implementing client api
 *
 */