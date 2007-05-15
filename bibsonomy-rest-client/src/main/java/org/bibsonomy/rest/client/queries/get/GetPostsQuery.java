package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive an ordered list of all posts.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetPostsQuery extends AbstractQuery<List<Post<? extends Resource>>>
{
	private int start;
	private int end;
	private Reader downloadedDocument;
	private Class<? extends Resource> resourceType;
	private List<String> tags;
	private GroupingEntity grouping = GroupingEntity.ALL;
	private String groupingValue;
	private String resourceHash;

	/**
	 * Gets bibsonomy's posts list.
	 */
	public GetPostsQuery()
	{
		this( 0, 19 );
	}

	/**
	 * Gets bibsonomy's posts list.
	 * 
	 * @param start start of the list
	 * @param end end of the list
	 */
	public GetPostsQuery( int start, int end )
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
	 * set the resource type of the resources of the posts.
	 * @param type the type to set
	 */
	public void setResourceType( Class<? extends Resource> type )
	{
		this.resourceType = type;
	}
	
	/**
	 * @param resourceHash The resourceHash to set.
	 */
	public void setResourceHash( String resourceHash )
	{
		this.resourceHash = resourceHash;
	}
	
	/**
	 * @param tags the tags to set
	 */
	public void setTags( List<String> tags )
	{
		this.tags = tags;
	}
	
	@Override
	public List<Post<? extends Resource>> getResult() throws BadRequestOrResponseException, IllegalStateException
	{
		if( downloadedDocument == null ) throw new IllegalStateException( "Execute the query first." );
		try
		{
			return RendererFactory.getRenderer( getRenderingFormat() ).parsePostList( downloadedDocument );
		}
      catch( InternServerException ex )
      {
         throw new BadRequestOrResponseException( ex );
      }
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		String url = URL_POSTS + "?start=" + start + "&end=" + end;
		
		if( resourceType != Resource.class )
		{
			url += "&resourcetype=" + Resource.toString( resourceType ).toLowerCase();
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
		
		if( tags != null && tags.size() > 0 )
		{
			boolean first = true;
			for( String tag: tags )
			{
				if( first )
				{
					url += "&tags=" + tag;
					first = false;
				}
				else
				{
					url += "+" + tag;
				}
			}
		}
		
		if( resourceHash != null && resourceHash.length() > 0 )
		{
			url += "&resource=" + resourceHash;
		}
		downloadedDocument = performGetRequest( url + "&format=" + getRenderingFormat().toString().toLowerCase() );
	}
}

/*
 * $Log$
 * Revision 1.9  2007-05-15 08:45:56  mbork
 * code walk-through
 *
 * Revision 1.8  2007/05/05 20:40:36  mbork
 * fixed a bug caused by the removal of the ResourceType enum which was not covered by a test
 *
 * Revision 1.7  2007/05/01 22:26:56  jillig
 * ->more type-safety with class as resourcetype
 *
 * Revision 1.6  2007/04/19 16:12:20  mbork
 * throw BadRequestOrResponseException on InternServerException in REST client
 *
 * Revision 1.5  2007/02/21 14:08:34  mbork
 * - included code generation of the schema in the maven2 build-lifecycle
 * - removed circular dependencies among the modules
 * - cleaned up the poms of the modules
 * - fixed failing unit-tests
 *
 * Revision 1.4  2007/02/15 10:29:10  mbork
 * the LogicInterface now uses Lists instead of Sets
 * fixed use of generics
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
 * Revision 1.9  2006/09/24 21:26:20  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.8  2006/09/16 18:19:15  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.7  2006/07/05 21:30:56  mbork
 * bugfix: attributes are lowercase
 *
 * Revision 1.6  2006/06/23 20:50:08  mbork
 * clientlib:
 * - added head request
 * - fixed issues with enums using uppercase letters invoked with toString()
 * serverlib:
 * - fixed some issues
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
 * Revision 1.2  2006/06/07 18:22:31  mbork
 * client api: finished implementing get and delete requests
 *
 * Revision 1.1  2006/06/06 22:20:54  mbork
 * started implementing client api
 *
 */