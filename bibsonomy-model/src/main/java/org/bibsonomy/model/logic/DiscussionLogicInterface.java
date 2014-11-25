/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.logic;

import java.util.List;

import org.bibsonomy.model.DiscussionItem;

/**
 * @author dzo
 */
public interface DiscussionLogicInterface {
	
	/**
	 * creates a discussion item for the specified resource (interHash) and user
	 * 
	 * @param interHash
	 * @param username
	 * @param discussionItem
	 */
	public void createDiscussionItem(String interHash, String username, DiscussionItem discussionItem);
	
	/**
	 * updates a discussion item for the specified resource (interHash) and user
	 * the item is identified by the hash (please don't recalculate the hash;
	 * done by the logic)
	 * 
	 * @param username
	 * @param interHash
	 * @param discussionItem
	 */
	public void updateDiscussionItem(String username, String interHash, DiscussionItem discussionItem);
	
	/**
	 * deletes the specified discussion item (hash) for the specified user and
	 * resource (interHash)
	 * 
	 * @param username
	 * @param interHash
	 * @param discussionItemHash
	 */
	public void deleteDiscussionItem(String username, String interHash, String discussionItemHash);
	
	/**
	 * get all 
	 * 
	 * @param interHash
	 * @return a list of discussion items (comment, reviews, …)
	 */
	public List<DiscussionItem> getDiscussionSpace(String interHash);	
}
