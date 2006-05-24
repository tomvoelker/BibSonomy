package org.bibsonomy.rest;

import java.util.Set;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public interface LogicInterface
{

	/**
	 * returns all users bibsonomy has
	 * 
	 * @param authUser currently logged in user's name
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<User> getUsers( String authUser, int start, int end );

	/**
	 * returns every post which has all of the tags attached
	 * 
	 * @param authUser currently logged in user's name
	 * @param tags
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getPostsByTags( String authUser, Set<String> tags, int start, int end );

	/**
	 * returns all posts of a user identified by an userName
	 * 
	 * @param authUser currently logged in user's name
	 * @param userName
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getPostsOfUser( String authUser, String userName, int start, int end );

	/**
	 * returns every post of user u which has all of the tags attached
	 * 
	 * @param authUser currently logged in user's name
	 * @param userName
	 * @param tags
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getPostsOfUserByTags( String authUser, String userName,
			Set<String> tags, int start, int end );

	/**
	 * returns every post of user u which has for every tag at least one of its subtags or the tag 
	 * itself attached
	 * 
	 * @param authUser currently logged in user's name
	 * @param userName
	 * @param tags
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getConceptOfUserByTags( String authUser, String userName,
			Set<String> tags, int start, int end );

	/**
	 * returns all posts of the resource r, if its a bookmark
	 * 
	 * @param authUser
	 * @param bookmarkHash
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getPostsOfBookmark( String authUser, String bookmarkHash, int start,
			int end );

	/**
	 * returns the post of user u of the resource r, if its a bookmark
	 * 
	 * @param authUser
	 * @param bookmarkHash
	 * @param userName
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getPostOfBookmarkByUser( String authUser, String bookmarkHash,
			String userName, int start, int end );

	/**
	 * returns all posts of the resource r, it its a bibtex entry
	 * 
	 * @param authUser
	 * @param bibtexHash
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getPostsOfBibtex( String authUser, String bibtexHash, int start,
			int end );

	/**
	 * returns the post of user u of the resource r, it its a bibtex entry
	 * 
	 * @param authUser
	 * @param bibtexHash
	 * @param userName
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getPostOfBibtexByUser( String authUser, String bibtexHash,
			String userName, int start, int end );

	/**
	 * returns all groups of the system
	 * 
	 * @return
	 */
	public abstract Set<Group> getGroups();

	/**
	 * returns all posts of all users belonging to the group g
	 * 
	 * @param authUser
	 * @param groupName
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getPostsOfGroup( String authUser, String groupName, int start, int end );

	/**
	 * returns every post which has all of the tags attached and where the user belongs to group g
	 * 
	 * @param authUser
	 * @param groupName
	 * @param tags
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getPostsOfGroupByTags( String authUser, String groupName,
			Set<String> tags, int start, int end );

	/**
	 * returns all posts which are set viewable for members of the group g
	 * 
	 * @param authUser
	 * @param groupName
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getViewablePostsForGroup( String authUser, String groupName,
			int start, int end );

	/**
	 * returns all posts which are set viewable for members of the group g and which have all of the 
	 * tags attached
	 * 
	 * @param authUser
	 * @param groupName
	 * @param tags
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Post> getViewablePostsTaggedWith( String authUser, String groupName,
			Set<String> tags, int start, int end );

	/**
	 * returns every post which has all of the tags attached and where the user belongs to group g
	 * 
	 * @param authUser
	 * @param query
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Resource> getResourcesBySearch( String authUser, String query, int start,
			int end );

	/**
	 * returns all tags this user uses
	 * 
	 * @param authUser ?
	 * @param userName
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Set<Tag> getTagsOfUser( String authUser, String userName, int start, int end );
}

/*
 * $Log$
 * Revision 1.1  2006-05-24 20:09:03  jillig
 * renamed DbInterface to RESTs LogicInterface
 *
 * Revision 1.1  2006/05/19 21:01:08  mbork
 * started implementing rest api
 *
 */