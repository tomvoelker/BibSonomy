package org.bibsonomy.rest.renderer.impl;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.Renderer;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class HTMLRenderer implements Renderer 
{

	public void serializePosts( Writer writer, List<? extends Post<? extends Resource>> posts, ViewModel viewModel ) throws InternServerException
	{
		// TODO Auto-generated method stub
	}

	public void serializePost( Writer writer, Post<? extends Resource> post, ViewModel model )
	{
		// TODO Auto-generated method stub
	}

	public void serializeUsers( Writer writer, List<User> users, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeUser( Writer writer, User user, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeTags( Writer writer, List<Tag> tags, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeTag( Writer writer, Tag tag, ViewModel model )
	{
		// TODO Auto-generated method stub
	}

	public void serializeGroups( Writer writer, List<Group> groups, ViewModel viewModel )
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

	public Post<? extends Resource> parsePost( Reader reader )
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

	public List<Post<? extends Resource>> parsePostList( Reader reader ) throws BadRequestOrResponseException 
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

   public void serializeError( Writer writer, String errorMessage )
   {
      // TODO Auto-generated method stub
   }
}