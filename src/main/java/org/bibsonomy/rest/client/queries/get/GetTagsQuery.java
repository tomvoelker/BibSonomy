package org.bibsonomy.rest.client.queries.get;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.GroupingEntity;
import org.bibsonomy.rest.exceptions.InvalidXMLException;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.ModelFactory;
import org.bibsonomy.rest.renderer.xml.TagType;

/**
 * Use this Class to receive an ordered list of all posts.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetTagsQuery extends AbstractQuery<List<Tag>>
{
	private int start;
	private int end;
	private BibsonomyXML bibsonomyXML;
	private String filter = null;
	private GroupingEntity grouping = GroupingEntity.ALL;
	private String groupingValue;
	
	/**
	 * Gets bibsonomy's tags list
	 */
	public GetTagsQuery()
	{
		this( 0, 19 );
	}

	/**
	 * Gets bibsonomy's tags list.
	 * 
	 * @param start start of the list
	 * @param end end of the list
	 */
	public GetTagsQuery(  int start, int end )
	{
      if( start < 0 ) start = 0;
      if( end < start ) end = start;
      
		this.start = start;
		this.end = end;
	}

	/**
	 * Set the grouping used for this query. If {@link GroupingEntity#ALL} is
	 * chosen, the groupingValue isn't evaluated (-> it can be null or empty).
	 * 
	 * @param grouping
	 *            the grouping to use
	 * @param groupingValue
	 *            the value for the chosen grouping; for example the username if
	 *            grouping is {@link GroupingEntity#USER}
	 */
	public void setGrouping( GroupingEntity grouping, String groupingValue )
	{
		if( grouping == GroupingEntity.ALL )
		{
			this.grouping = grouping;
			return;
		}
		if( groupingValue == null || groupingValue.length() == 0 ) throw new IllegalArgumentException( "no grouping value given" );
		this.grouping = grouping;
		this.groupingValue = groupingValue;
	}
	
	/**
	 * @param filter The filter to set.
	 */
	public void setFilter( String filter )
	{
		this.filter = filter;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public List<Tag> getResult()
	{
		if( bibsonomyXML == null ) throw new IllegalStateException( "Execute the query first." );
		
		List<Tag> tags = new ArrayList<Tag>();

		if( bibsonomyXML.getTags() != null && bibsonomyXML.getTags().getTag() != null )
		{
			for( TagType xmlTag: bibsonomyXML.getTags().getTag() )
			{
				try
				{
					tags.add( ModelFactory.getInstance().createTag( xmlTag ) );
				}
				catch( InvalidXMLException e )
				{
					LOGGER.log( Level.WARNING, e.getMessage(), e );
					throw e;
				}
			}
		}
		return tags;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#doExecute()
	 */
	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		String url = API_URL + URL_TAGS + "?start=" + start + "&end=" + end;
		switch( grouping )
		{
		case USER:
			url += "&user=" + groupingValue;
			break;
		case GROUP:
			url += "&group=" + groupingValue;
			break;
		case VIEWABLE:
			url += "&viewable=" + groupingValue;
			break;
		}
			
		if( filter != null && filter.length() > 0 )
		{
			url += "&filter=" + filter;
		}
		bibsonomyXML = performGetRequest( url );
	}
}

/*
 * $Log$
 * Revision 1.3  2006-06-08 13:23:47  mbork
 * improved documentation, added throws statements even for runtimeexceptions, moved abstractquery to prevent users to call execute directly
 *
 * Revision 1.2  2006/06/08 08:02:54  mbork
 * fixed erroneous use of generics
 *
 * Revision 1.1  2006/06/07 18:22:31  mbork
 * client api: finished implementing get and delete requests
 *
 */