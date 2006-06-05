package org.bibsonomy.rest.renderer;

import java.io.PrintWriter;
import java.util.Set;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.ViewModel;
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
	public void serializePosts( PrintWriter writer, Set<Post> posts, ViewModel viewModel ) throws InternServerException;
	
	/**
	 * serializes one post 
	 * @param writer
	 * @param post
	 * @param model
	 */
	public void serializePost( PrintWriter writer, Post post, ViewModel model );
	
	/**
	 * serializes a list of users
	 * 
	 * @param writer
	 * @param users
	 * @param viewModel
	 */
	public void serializeUsers( PrintWriter writer, Set<User> users, ViewModel viewModel );
	
	/**
	 * serializes one user
	 * 
	 * @param writer
	 * @param user
	 * @param viewModel
	 */
	public void serializeUser( PrintWriter writer, User user, ViewModel viewModel );
	
	/**
	 * serializes a list of tags
	 * 
	 * @param writer
	 * @param tags
	 * @param viewModel
	 */
	public void serializeTags( PrintWriter writer, Set<Tag> tags, ViewModel viewModel );

	/**
	 * serializes a tag's details, including list of subtags, list of supertags and list of correlated tags
	 * 
	 * @param writer
	 * @param tag
	 * @param model
	 */
	public void serializeTag( PrintWriter writer, Tag tag, ViewModel model );
	
	/**
	 * serializes a list of groups
	 * 
	 * @param writer
	 * @param groups
	 * @param viewModel
	 */
	public void serializeGroups( PrintWriter writer, Set<Group> groups, ViewModel viewModel );

	/**
	 * serializes one group
	 * 
	 * @param writer
	 * @param group
	 * @param model
	 */
	public void serializeGroup( PrintWriter writer, Group group, ViewModel model );
}

/*
 * $Log$
 * Revision 1.5  2006-06-05 14:14:11  mbork
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