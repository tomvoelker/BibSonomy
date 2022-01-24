/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.webapp.command.GroupingCommand;

/**
 * @author dzo
 * @param <D> 
 */
@Getter
@Setter
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
	 * the name of the post's owner
	 * The post is the one, that the discussing user chose to start the discussion
	 */
	private String postUserName;
	/**
	 * the intraHash of the post owner
	 * The post is the one, that the discussing user chose to start the discussion
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

}
