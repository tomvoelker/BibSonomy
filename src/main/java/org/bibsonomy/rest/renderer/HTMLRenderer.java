package org.bibsonomy.rest.renderer;

import java.io.PrintWriter;
import java.util.Set;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.InternServerException;
import org.bibsonomy.rest.ViewModel;

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
 * Revision 1.2  2006-05-21 20:31:51  mbork
 * continued implementing context
 *
 * Revision 1.1  2006/05/19 21:01:08  mbork
 * started implementing rest api
 *
 */