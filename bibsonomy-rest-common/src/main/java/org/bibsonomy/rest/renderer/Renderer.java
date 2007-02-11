package org.bibsonomy.rest.renderer;

import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.InternServerException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public interface Renderer
{
	/**
	 * serializes a list of posts
	 * 
	 * @param writer
	 * @param posts
	 * @param viewModel
	 */
	public void serializePosts( Writer writer, Set<Post<Resource>> posts, ViewModel viewModel ) throws InternServerException;
	
	/**
	 * serializes one post 
	 * @param writer
	 * @param post
	 * @param model
	 */
	public void serializePost( Writer writer, Post<Resource> post, ViewModel model );
	
	/**
	 * serializes a list of users
	 * 
	 * @param writer
	 * @param users
	 * @param viewModel
	 */
	public void serializeUsers( Writer writer, Set<User> users, ViewModel viewModel );
	
	/**
	 * serializes one user
	 * 
	 * @param writer
	 * @param user
	 * @param viewModel
	 */
	public void serializeUser( Writer writer, User user, ViewModel viewModel );
	
	/**
	 * serializes a list of tags
	 * 
	 * @param writer
	 * @param tags
	 * @param viewModel
	 */
	public void serializeTags( Writer writer, Set<Tag> tags, ViewModel viewModel );

	/**
	 * serializes a tag's details, including list of subtags, list of supertags and list of correlated tags
	 * 
	 * @param writer
	 * @param tag
	 * @param model
	 */
	public void serializeTag( Writer writer, Tag tag, ViewModel model );
	
	/**
	 * serializes a list of groups
	 * 
	 * @param writer
	 * @param groups
	 * @param viewModel
	 */
	public void serializeGroups( Writer writer, Set<Group> groups, ViewModel viewModel );

	/**
	 * serializes one group
	 * 
	 * @param writer
	 * @param group
	 * @param model
	 */
	public void serializeGroup( Writer writer, Group group, ViewModel model );

	public List<User> parseUserList( Reader reader ) throws BadRequestOrResponseException;
	public User parseUser( Reader reader ) throws BadRequestOrResponseException;
	
	public List<Post<Resource>> parsePostList( Reader reader ) throws BadRequestOrResponseException;
	public Post<Resource> parsePost( Reader reader ) throws BadRequestOrResponseException;
	
	public List<Group> parseGroupList( Reader reader ) throws BadRequestOrResponseException;
	public Group parseGroup( Reader reader ) throws BadRequestOrResponseException;
	
	public List<Tag> parseTagList( Reader reader ) throws BadRequestOrResponseException;
}

/*
 * $Log$
 * Revision 1.3  2007-02-11 17:55:39  mbork
 * switched REST-api to the 'new' datamodel, which does not deserve the name...
 *
 * Revision 1.2  2007/02/05 10:35:55  cschenk
 * Distributed code from the spielwiese among the modules
 *
 * Revision 1.1  2006/10/10 12:42:15  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.11  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.10  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.9  2006/06/08 13:33:19  mbork
 * reorganized imports
 *
 * Revision 1.8  2006/06/08 13:23:48  mbork
 * improved documentation, added throws statements even for runtimeexceptions, moved abstractquery to prevent users to call execute directly
 *
 * Revision 1.7  2006/06/07 19:37:29  mbork
 * implemented post queries
 *
 * Revision 1.6  2006/06/06 17:39:30  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 * Revision 1.5  2006/06/05 14:14:11  mbork
 * implemented GET strategies
 *
 * Revision 1.4  2006/05/24 15:18:08  cschenk
 * Introduced a rendering format and a factory that produces renderers (for xml, rdf, html)
 *
 * Revision 1.3  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.2  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 * Revision 1.1  2006/05/19 21:01:08  mbork
 * started implementing rest api
 *
 */