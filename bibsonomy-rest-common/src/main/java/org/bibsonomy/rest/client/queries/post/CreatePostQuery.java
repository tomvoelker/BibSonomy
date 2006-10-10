package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to post a post. ;)
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class CreatePostQuery extends AbstractQuery<String>
{
	private boolean executed = false;
	private String result;
	private Post post;
	private String username;

	/**
    * Creates a new post in bibsonomy.
    * 
    * @param username
    *           the username under which the post is to be created
    * @param post
    *           the post to be created
    * @throws IllegalArgumentException
    *            if
    *            <ul>
    *            <li>the username is null or empty</li>
    *            <li>no resource is connected with the post</li>
    *            <li>the resource is a bookmark: if no url is specified</li>
    *            <li>the resource is a bibtex: if no title is specified</li>
    *            <li>no tags are specified or the tags have no names</li>
    *            </ul>
    */
   public CreatePostQuery( String username, Post post ) throws IllegalArgumentException
	{
		if( username == null || username.length() == 0 ) throw new IllegalArgumentException( "no username given" );
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
		StringWriter sw = new StringWriter( 100 );
		RendererFactory.getRenderer( getRenderingFormat() ).serializePost( sw, post, null );
		result = performRequest( HttpMethod.POST, URL_USERS + "/" + username + "/" + URL_POSTS + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString() );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-10 12:42:16  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.6  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
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
 * Revision 1.2  2006/06/08 07:41:12  mbork
 * client api completed
 *
 * Revision 1.1  2006/06/07 19:37:29  mbork
 * implemented post queries
 *
 */