package org.bibsonomy.rest.client.queries.get;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bibsonomy.model.Post;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.GroupingEntity;
import org.bibsonomy.rest.enums.ResourceType;
import org.bibsonomy.rest.exceptions.InvalidXMLException;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.ModelFactory;
import org.bibsonomy.rest.renderer.xml.PostType;

/**
 * Use this Class to receive an ordered list of all posts.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetAddedPostsQuery extends AbstractQuery<List<Post>>
{
	private int start;
	private int end;
	private BibsonomyXML bibsonomyXML;
	private ResourceType resourceType;
	private GroupingEntity grouping = GroupingEntity.ALL;
	private String groupingValue;

	/**
	 * Gets bibsonomy's posts list.
	 */
	public GetAddedPostsQuery()
	{
		this( 0, 19 );
	}

	/**
    * Gets bibsonomy's posts list ordered by adding date.
    * 
    * @param start
    *           start of the list
    * @param end
    *           end of the list
    */
	public GetAddedPostsQuery( int start, int end )
	{
      if( start < 0 ) start = 0;
      if( end < start ) end = start;
      
		this.start = start;
		this.end = end;
	}

	/**
    * Set the grouping used for this query. If {@link GroupingEntity#ALL} is chosen, the
    * groupingValue isn't evaluated (-> it can be null or empty).
    * 
    * @param grouping
    *           the grouping to use
    * @param groupingValue
    *           the value for the chosen grouping; for example the username if grouping is
    *           {@link GroupingEntity#USER}
    * @throws IllegalArgumentException
    *            if grouping is != {@link GroupingEntity#ALL} and groupingValue is null or empty
    */
	public void setGrouping( GroupingEntity grouping, String groupingValue ) throws IllegalArgumentException
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
	 * set the resource type of the resources of the posts 
	 * @param type the type to set
	 */
	public void setResourceType( ResourceType type )
	{
		this.resourceType = type;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public List<Post> getResult()
	{
		if( bibsonomyXML == null ) throw new IllegalStateException( "Execute the query first." );
		
		List<Post> posts = new ArrayList<Post>();

		if( bibsonomyXML.getPosts() != null && bibsonomyXML.getPosts().getPost() != null )
		{
			for( PostType xmlPost: bibsonomyXML.getPosts().getPost() )
			{
				try
				{
					posts.add( ModelFactory.getInstance().createPost( xmlPost ) );
				}
				catch( InvalidXMLException e )
				{
					LOGGER.log( Level.WARNING, e.getMessage(), e );
					throw e;
				}
			}
		}
		return posts;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#doExecute()
	 */
	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		String url = URL_POSTS + "/" + URL_POSTS_ADDED + "?start=" + start + "&end=" + end;
		
		if( resourceType != ResourceType.ALL )
		{
			url += "&resourcetype=" + resourceType.toString();
		}
		
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
		
		bibsonomyXML = performGetRequest( url );
	}
}

/*
 * $Log$
 * Revision 1.4  2006-06-14 18:23:21  mbork
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