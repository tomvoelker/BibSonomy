package org.bibsonomy.rest.renderer;

import java.io.PrintWriter;
import java.util.Set;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.InternServerException;

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
	 * TODO encapsulate start, end and next into a special context type?
	 * 
	 * @param writer
	 * @param posts
	 * @param start
	 * @param end
	 * @param next
	 */
	public void serializePosts( PrintWriter writer, Set<Post> posts, int start, int end, String next ) throws InternServerException;

	/**
	 * serializes a list of users
	 * 
	 * TODO encapsulate start, end and next into a special context type?
	 * 
	 * @param writer
	 * @param users
	 * @param start
	 * @param end
	 * @param next
	 */
	public void serializeUsers( PrintWriter writer, Set<User> users, int start, int end, String next );

	/**
	 * serializes a list of tags
	 * 
	 * TODO encapsulate start, end and next into a special context type?
	 * 
	 * @param writer
	 * @param tags
	 * @param start
	 * @param end
	 * @param next
	 */
	public void serializeTags( PrintWriter writer, Set<Tag> tags, int start, int end, String next );
}

/*
 * $Log$
 * Revision 1.1  2006-05-19 21:01:08  mbork
 * started implementing rest api
 *
 */