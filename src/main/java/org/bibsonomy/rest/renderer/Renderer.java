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
public interface Renderer
{
	public final static String FORMAT_XML = "xml";
	public final static String FORMAT_RDF = "rdf";
	public final static String FORMAT_HTML = "html";

	/**
	 * serializes a list of posts
	 * 
	 * @param writer
	 * @param posts
	 * @param viewModel
	 */
	public void serializePosts( PrintWriter writer, Set<Post> posts, ViewModel viewModel ) throws InternServerException;

	/**
	 * serializes a list of users
	 * 
	 * @param writer
	 * @param users
	 * @param viewModel
	 */
	public void serializeUsers( PrintWriter writer, Set<User> users, ViewModel viewModel );

	/**
	 * serializes a list of tags
	 * 
	 * @param writer
	 * @param tags
	 * @param viewModel
	 */
	public void serializeTags( PrintWriter writer, Set<Tag> tags, ViewModel viewModel );
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