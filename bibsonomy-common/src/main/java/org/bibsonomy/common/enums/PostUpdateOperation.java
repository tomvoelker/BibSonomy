/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.common.enums;

/**
 * Depicts which party of a post should be updated when calling 
 * the <code>update(...)</code> method in the LogicInterface.
 * 
 * @author rja
 */
public enum PostUpdateOperation {
	/**
	 * Update all parts of the entity.
	 */
	UPDATE_ALL,
	
	/**
	 * Update only the tags of the post.
	 */
	UPDATE_TAGS,
	
	/**
	 * Update only the documents attached to the post.
	 */
	UPDATE_DOCUMENTS,
	
	/**
	 * Add a url to the post.
	 */
	UPDATE_URLS_ADD,
	
	/**
	 * Delete a url of the post.
	 */
	UPDATE_URLS_DELETE,
	
	/**
	 * Update only the repositories attached to the post
	 * (PUMA specific)
	 */
	UPDATE_REPOSITORY,
	

	/**
	 * Update privacy settings of a post. (Public, Private, Other)
	 */
	UPDATE_VIEWABLE,
	
	/**
	 * Normalizes a bibtex*/
	UPDATE_NORMALIZE;
	
	
}
