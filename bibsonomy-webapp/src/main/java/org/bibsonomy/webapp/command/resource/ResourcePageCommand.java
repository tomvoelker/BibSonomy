/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.resource;

import java.util.List;
import java.util.Map;

import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.TagResourceViewCommand;

/**
 * @author dzo
 * @param <R> the resource
 */
public class ResourcePageCommand<R extends Resource> extends TagResourceViewCommand {
	private String requestedHash;
	
	private Map<String, List<String>> copyUsersMap;
	
	private List<DiscussionItem> discussionItems;
	
	private String postOwner;
	
	private String intraHash;
	
	private String resourceClass; 

	/**
	 * @return the intraHash of a post
	 */
	public String getIntraHash() {
		return this.intraHash;
	}

	/**
	 * set the intraHash of a post
	 * @param intraHash
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}

	/**
	 * @return the owner of the post
	 */
	public String getPostOwner() {
		return this.postOwner;
	}

	/**
	 * set the owner of a post
	 * @param postOwner
	 */
	public void setPostOwner(String postOwner) {
		this.postOwner = postOwner;
	}

	/**
	 * @return the requestedHash
	 */
	public String getRequestedHash() {
		return this.requestedHash;
	}

	/**
	 * @param requestedHash the requestedHash to set
	 */
	public void setRequestedHash(final String requestedHash) {
		this.requestedHash = requestedHash;
	}

	/**
	 * @return the discussionItems
	 */
	public List<DiscussionItem> getDiscussionItems() {
		return this.discussionItems;
	}

	/**
	 * @param discussionItems the discussionItems to set
	 */
	public void setDiscussionItems(final List<DiscussionItem> discussionItems) {
		this.discussionItems = discussionItems;
	}

	/**
	 * @return the copyUsersMap
	 */
	public Map<String, List<String>> getCopyUsersMap() {
		return this.copyUsersMap;
	}

	/**
	 * @param copyUsersMap the copyUsersMap to set
	 */
	public void setCopyUsersMap(Map<String, List<String>> copyUsersMap) {
		this.copyUsersMap = copyUsersMap;
	}

	/**
	 * @return the resourceClass
	 */
	public String getResourceClass() {
		return this.resourceClass;
	}

	/**
	 * @param resourceClass the resourceClass to set
	 */
	public void setResourceClass(String resourceClass) {
		this.resourceClass = resourceClass;
	}
}
