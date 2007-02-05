package org.bibsonomy.rest.renderer.impl;

import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import org.bibsonomy.gen_model.Group;
import org.bibsonomy.gen_model.Post;
import org.bibsonomy.gen_model.Tag;
import org.bibsonomy.gen_model.User;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.renderer.Renderer;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class HTMLRenderer implements Renderer 
{

	public void serializePosts( Writer writer, Set<Post> posts, ViewModel viewModel ) throws InternServerException
	{
		// TODO Auto-generated method stub
	}

	public void serializePost( Writer writer, Post post, ViewModel model )
	{
		// TODO Auto-generated method stub
	}

	public void serializeUsers( Writer writer, Set<User> users, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeUser( Writer writer, User user, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeTags( Writer writer, Set<Tag> tags, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeTag( Writer writer, Tag tag, ViewModel model )
	{
		// TODO Auto-generated method stub
	}

	public void serializeGroups( Writer writer, Set<Group> groups, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeGroup( Writer writer, Group group, ViewModel model )
	{
		// TODO Auto-generated method stub
	}

	public User parseUser( Reader reader )
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Post parsePost( Reader reader )
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Group parseGroup( Reader reader )
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<Group> parseGroupList( Reader reader ) throws BadRequestOrResponseException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<Post> parsePostList( Reader reader ) throws BadRequestOrResponseException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<Tag> parseTagList( Reader reader ) throws BadRequestOrResponseException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> parseUserList( Reader reader ) throws BadRequestOrResponseException 
	{
		// TODO Auto-generated method stub
		return null;
	}
}

/*
 * $Log$
 * Revision 1.2  2007-02-05 10:35:54  cschenk
 * Distributed code from the spielwiese among the modules
 *
 * Revision 1.1  2006/10/10 12:42:16  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.7  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.6  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.5  2006/06/08 13:23:48  mbork
 * improved documentation, added throws statements even for runtimeexceptions, moved abstractquery to prevent users to call execute directly
 *
 * Revision 1.4  2006/06/07 19:37:28  mbork
 * implemented post queries
 *
 * Revision 1.3  2006/06/06 17:39:29  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 * Revision 1.2  2006/06/05 14:14:11  mbork
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