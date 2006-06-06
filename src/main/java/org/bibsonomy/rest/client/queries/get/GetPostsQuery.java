package org.bibsonomy.rest.client.queries.get;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bibsonomy.model.Post;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.enums.ResourceType;
import org.bibsonomy.rest.exceptions.InvalidXMLException;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.ModelFactory;
import org.bibsonomy.rest.renderer.xml.PostType;

/**
 * Use this Class to receive an ordered list of all posts
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetPostsQuery extends AbstractQuery
{
	private String username;
	private int start;
	private int end;
	private BibsonomyXML bibsonomyXML;
	private ResourceType resourceType;
	private List<String> tags;

	/**
	 * Gets bibsonomy's posts list
	 */
	public GetPostsQuery( String username )
	{
		this( username, 0, 19 );
	}

	/**
	 * Gets bibsonomy's posts list
	 * 
	 * @param start start of the list
	 * @param end end of the list
	 */
	public GetPostsQuery( String username, int start, int end )
	{
		if( username == null || username.length() == 0 ) throw new IllegalArgumentException( "no username given" );
		if( start < 0 ) throw new IllegalArgumentException( "start must be >= 0" );
		if( end < start ) throw new IllegalArgumentException( "end must be >= 0 and >= start value" );
		
		this.username = username;
		this.start = start;
		this.end = end;
	}

	/**
	 * set the resource type of the resources of the posts 
	 * @param type
	 */
	public void setResourceType( ResourceType type )
	{
		this.resourceType = type;
	}
	
	/**
	 * @param tags
	 */
	public void setTags( List<String> tags )
	{
		this.tags = tags;
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
		String url = API_URL + URL_USERS + "/" +username + "/posts?start=" + start + "&end=" + end;
		if( resourceType != ResourceType.ALL )
		{
			url += "&resourcetype=" + resourceType.toString();
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
		bibsonomyXML = performGetRequest( url );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 22:20:54  mbork
 * started implementing client api
 *
 */