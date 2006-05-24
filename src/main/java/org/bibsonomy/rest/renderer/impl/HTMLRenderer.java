package org.bibsonomy.rest.renderer.impl;

import java.io.PrintWriter;
import java.util.Set;

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
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializePosts(java.io.PrintWriter, java.util.Set, int, int, java.lang.String)
	 */
	public void serializePosts( PrintWriter writer, Set<Post> posts, ViewModel viewModel )
			throws InternServerException
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeUsers(java.io.PrintWriter, java.util.Set, int, int, java.lang.String)
	 */
	public void serializeUsers( PrintWriter writer, Set<User> users, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeTags(java.io.PrintWriter, java.util.Set, int, int, java.lang.String)
	 */
	public void serializeTags( PrintWriter writer, Set<Tag> tags, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}
}

/*
 * $Log$
 * Revision 1.1  2006-05-24 15:18:08  cschenk
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