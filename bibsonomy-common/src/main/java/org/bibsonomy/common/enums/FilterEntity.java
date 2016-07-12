/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * Defines possible filter entities
 * 
 * @author Stefan Stützer
 */
public enum FilterEntity implements Filter {

	/**
	 * Use this when you ONLY want to retrieve resources with a PDF
	 * file attached
	 */
	JUST_PDF,
	
	/**
	 * Only retrieve resources which are stored at least two times
	 */
	DUPLICATES,
	
	/**
	 * only unique items
	 */
	UNIQUE,
	
	/**
	 * Filter to retrieve posts of spammers
	 * This can only be used by admins
	 */
	ADMIN_SPAM_POSTS,
	
	/**
	 * Use this when you ONLY want to retrieve resources which are 
	 * viewable for your groups
	 */
	MY_GROUP_POSTS,
	
	/**
	 * TODO: remove or update docu, backlisted tags are not used anymore
	 * 
	 * Some pages apply filtering, e.g., the homepage does not show posts
	 * which contain a blacklisted tag. Setting the filter to this entity
	 * should turn off such filtering. 
	 */
	UNFILTERED,
	
	/**
	 * Return only posts which have been send to a repository (PUMA specific)
	 */
	POSTS_WITH_REPOSITORY,
	
	/**
	 * Return only posts which are discussed by users - only classified non spammer users are shown
	 */
	POSTS_WITH_DISCUSSIONS,

	/**
	 * Return only posts which are discussed by users - all non spammer users are shown
	 */
	POSTS_WITH_DISCUSSIONS_UNCLASSIFIED_USER,
	
	/** the user only wants his layout files 
	  * TODO: maybe this filter entity enum is the wrong location for this filter
	  */
	LAYOUT_DOCUMENTS,
	
	/**
	 * Returns all versions of something
	 */
	HISTORY;
}