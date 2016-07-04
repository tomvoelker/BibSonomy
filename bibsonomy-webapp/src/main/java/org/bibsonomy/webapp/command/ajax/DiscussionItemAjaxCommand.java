/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package org.bibsonomy.webapp.command.ajax;

import java.util.List;

import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.webapp.command.GroupingCommand;

/**
 * @author dzo
 * @param <D> 
 */
public class DiscussionItemAjaxCommand<D extends DiscussionItem> extends AjaxCommand implements GroupingCommand {
	
	/**
	 * the discussionItem
	 */
	private D discussionItem;
	
	/**
	 * the hash of the resource
	 */
	private String hash;
	
	/**
	 * the user name who owns the original post
	 */
	private String postUserName;
	/**
	 * the user name who owns the original post
	 */
	private String intraHash;
	
	/**
	 * The abstract (or general) group of the post:
	 * public, private, or other 
	 */
	private String abstractGrouping;
	
	/**
	 * the groups
	 */
	private List<String> groups;
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.ajax.GroupingCommand#getAbstractGrouping()
	 */
	@Override
	public String getAbstractGrouping() {
		return this.abstractGrouping;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.ajax.GroupingCommand#setAbstractGrouping(java.lang.String)
	 */
	@Override
	public void setAbstractGrouping(String abstractGrouping) {
		this.abstractGrouping = abstractGrouping;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.ajax.GroupingCommand#getGroups()
	 */
	@Override
	public List<String> getGroups() {
		return this.groups;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.ajax.GroupingCommand#setGroups(java.util.List)
	 */
	@Override
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * @return the comment
	 */
	public D getDiscussionItem() {
		return this.discussionItem;
	}
	
	/**
	 * @param discussionItem the comment to set
	 */
	public void setDiscussionItem(final D discussionItem) {
		this.discussionItem = discussionItem;
	}
	
	/**
	 * @return the hash
	 */
	public String getHash() {
		return this.hash;
	}
	
	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return the name of the post's owner
	 * The post is the one, that the discussing user chose to start the discussion
	 */
	public String getPostUserName() {
		return postUserName;
	}

	/**
	 * @param userName
	 */
	public void setPostUserName(String userName) {
		this.postUserName = userName;
	}	
	/**
	 * @return the intraHash of the post owner
	 * The post is the one, that the discussing user chose to start the discussion
	 */
	public String getIntraHash() {
		return this.intraHash;
	}

	/**
	 * @param intraHash
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}	
}
