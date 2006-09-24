package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;
import java.util.List;

import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.GroupingEntity;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

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
	private Reader downloadedDocument;
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
	public List<Tag> getResult() throws BadRequestOrResponseException, IllegalStateException
	{
		if( downloadedDocument == null ) throw new IllegalStateException( "Execute the query first." );
		
		try
		{
			return RendererFactory.getRenderer( getRenderingFormat() ).parseTagList( downloadedDocument );
		}
		catch( BadRequestOrResponseException e )
		{
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#doExecute()
	 */
	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		String url = URL_TAGS + "?start=" + start + "&end=" + end;
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
		downloadedDocument = performGetRequest( url + "&format=" + getRenderingFormat().toString().toLowerCase()  );
	}
}

/*
 * $Log$
 * Revision 1.6  2006-09-24 21:26:20  mbork
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
 * Revision 1.1  2006/06/07 18:22:31  mbork
 * client api: finished implementing get and delete requests
 *
 */