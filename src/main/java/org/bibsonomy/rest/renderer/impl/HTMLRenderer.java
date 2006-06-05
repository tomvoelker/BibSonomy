package org.bibsonomy.rest.renderer.impl;

import java.io.PrintWriter;
import java.util.Set;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.renderer.Renderer;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class HTMLRenderer implements Renderer 
{

	public void serializePosts( PrintWriter writer, Set<Post> posts, ViewModel viewModel ) throws InternServerException
	{
		// TODO Auto-generated method stub
	}

	public void serializePost( PrintWriter writer, Post post, ViewModel model )
	{
		// TODO Auto-generated method stub
	}

	public void serializeUsers( PrintWriter writer, Set<User> users, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeUser( PrintWriter writer, User user, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeTags( PrintWriter writer, Set<Tag> tags, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeTag( PrintWriter writer, Tag tag, ViewModel model )
	{
		// TODO Auto-generated method stub
	}

	public void serializeGroups( PrintWriter writer, Set<Group> groups, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeGroup( PrintWriter writer, Group group, ViewModel model )
	{
		// TODO Auto-generated method stub
	}
}

/*
 * $Log$
 * Revision 1.2  2006-06-05 14:14:11  mbork
 * implemented GET strategies
 *
 * Revision 1.1  2006/05/24 15:18:08  cschenk
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