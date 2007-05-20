package org.bibsonomy.rest.client.queries.put;

import java.io.StringWriter;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to change details of an existing post - change tags, for example.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class ChangePostQuery extends AbstractQuery<String>
{
	private boolean executed = false;
	private String result;
	private Post<? extends Resource> post;
	private String username;
	private String resourceHash;

	/**
    * Changes details of an existing post.
    * 
    * @param username
    *           the username under which the post is to be created
    * @param resourceHash
    *           hash of the resource to change
    * @param post
    *           the new value for the post
    * @throws IllegalArgumentException
    *            if
    *            <ul>
    *            <li>username or resourcehash are not specified</li>
    *            <li>no resource is connected with the post</li>
    *            <li>the resource is a bookmark: if no url is specified</li>
    *            <li>the resource is a bibtex: if no title is specified</li>
    *            <li>no tags are specified or the tags have no names</li>
    *            </ul>
    */
	public ChangePostQuery( String username, String resourceHash, Post<? extends Resource> post ) throws IllegalArgumentException
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
         if( bibtex.getInterHash() == null || bibtex.getInterHash().length() == 0 )
         {
            throw new IllegalArgumentException( "found an bibtex without interhash assigned." );
         }
         if( bibtex.getIntraHash() == null || bibtex.getIntraHash().length() == 0 )
         {
            throw new IllegalArgumentException( "found an bibtex without intrahash assigned." );
         }
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
		RendererFactory.getRenderer( getRenderingFormat() ).serializePost( sw, post, null);
		result = performRequest( HttpMethod.PUT, URL_USERS + "/" + username + "/" + URL_POSTS + "/" + resourceHash + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString());
	}
}

/*
 * $Log$
 * Revision 1.4  2007-05-20 16:49:06  mbork
 * fixed use of generics
 *
 * Revision 1.3  2007/02/11 17:55:34  mbork
 * switched REST-api to the 'new' datamodel, which does not deserve the name...
 *
 * Revision 1.2  2007/02/05 10:35:55  cschenk
 * Distributed code from the spielwiese among the modules
 *
 * Revision 1.1  2006/10/24 21:39:23  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:14  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.6  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.5  2006/07/09 19:07:12  mbork
 * moved check for hash from renderer to ChangePostQuery, because some queries must not test for the hash
 *
 * Revision 1.4  2006/06/14 18:23:21  mbork
 * refactored usage of username, password and host url
 *
 * Revision 1.3  2006/06/08 13:23:48  mbork
 * improved documentation, added throws statements even for runtimeexceptions, moved abstractquery to prevent users to call execute directly
 *
 * Revision 1.2  2006/06/08 08:02:54  mbork
 * fixed erroneous use of generics
 *
 * Revision 1.1  2006/06/08 07:41:11  mbork
 * client api completed
 *
 */