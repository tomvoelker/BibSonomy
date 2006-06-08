package org.bibsonomy.rest.client.queries.put;

import java.io.StringWriter;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

/**
 * Use this Class to change details of an existing post - change tags, for example
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class ChangePostQuery extends AbstractQuery
{
	private boolean executed = false;
	private String result;
	private Post post;
	private String username;
	private String resourceHash;

	/**
	 * Changes details of an existing post
	 * 
	 * <p/> an {@link IllegalArgumentException} is thrown, if
	 * <ul>
	 * <li>username or resourcehash are not specified</li>
	 * <li>no resource is connected with the post</li>
	 * <li>the resource is a bookmark: if no url is specified</li>
	 * <li>the resource is a bibtex: if no title is specified</li>
	 * <li>no tags are specified or the tags have no names</li>
	 * </ul>
	 * 
	 * @param username
	 *            the username under which the post is to be created
	 * @param resourceHash
	 *            hash of the resource to change
	 * @param post
	 *            the new value for the post
	 */
	public ChangePostQuery( String username, String resourceHash, Post post )
	{
		if( username == null || username.length() == 0 ) throw new IllegalArgumentException( "no username given" );
		if( resourceHash == null || resourceHash.length() == 0 ) throw new IllegalArgumentException( "no resourceHash given" );
		if( post == null ) throw new IllegalArgumentException( "no post specified" );
		if( post.getResource() == null ) throw new IllegalArgumentException( "no resource specified" );
		if( post.getResource() instanceof Bookmark )
		{
			Bookmark bookmark = (Bookmark)post.getResource();
			if( bookmark.getUrl() == null || bookmark.getUrl().length() == 0 ) throw new IllegalArgumentException( "no url specified in bookmark" );
		}
		if( post.getResource() instanceof BibTex )
		{
			BibTex bibtex = (BibTex)post.getResource();
			if( bibtex.getTitle() == null || bibtex.getTitle().length() == 0 ) throw new IllegalArgumentException( "no title specified in bibtex" );
		}
		if( post.getTags() == null || post.getTags().size() == 0) throw new IllegalArgumentException( "no tags specified" );
		for( Tag tag: post.getTags() )
		{
			if( tag.getName().length() == 0 ) throw new IllegalArgumentException( "missing tagname" );
		}
		this.username = username;
		this.resourceHash = resourceHash;
		this.post = post;
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
		StringWriter sw = new StringWriter( 100);
		XMLRenderer.getInstance().serializePost( sw, post, null);
		result = performRequest( HttpMethod.PUT, API_URL + URL_USERS + "/" + username + "/" + URL_POSTS + "/" + resourceHash, sw.toString());
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-08 07:41:11  mbork
 * client api completed
 *
 */