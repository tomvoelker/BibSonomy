/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;

/**
 * For every content type there should exist a separate class which implements
 * this interface. It supplies basic CRUD: create, read, update and delete.
 * @param <T> extends Resource
 * @param <P> extends GenericParam
 * 
 * @author Christian Schenk
 */
public interface CrudableContent<T extends Resource, P extends GenericParam> {
	/**
	 * Read
	 * 
	 * @param param
	 * @param session
	 * @return list of posts
	 */
	public List<Post<T>> getPosts(P param, DBSession session);

	/**
	 * Read
	 * 
	 * @param loginUserName
	 * @param resourceHash
	 * @param userName
	 * @param visibleGroupIDs
	 * @param session
	 * 
	 * @throws ResourceMovedException - when no resource 
	 * with that hash exists for that user, but once a resource 
	 * with that hash existed that has been moved. The new hash 
	 * is returned inside the exception. 
	 * @throws ObjectNotFoundException
	 * 
	 * @return list of posts
	 */
	public Post<T> getPostDetails(String loginUserName, String resourceHash, String userName, List<Integer> visibleGroupIDs, DBSession session) throws ResourceMovedException, ObjectNotFoundException;

	/**
	 * Delete
	 * 
	 * @param userName 
	 * @param resourceHash 
	 * @param session 
	 * 
	 * @return true, if entry existed and was deleted
	 */
	public boolean deletePost(String userName, String resourceHash, DBSession session);

	/**
	 * create
	 * 
	 * @param post
	 * @param session
	 * @return true if entry was created
	 */
	public boolean createPost(Post<T> post, DBSession session);

	/**
	 * update
	 * 
	 * @param post
	 * @param oldHash
	 * @param operation
	 * @param session
	 * @param loginUser
	 * @return <code>true</code> iff update was successful
	 */
	public boolean updatePost(Post<T> post, String oldHash, PostUpdateOperation operation, DBSession session, User loginUser);
}