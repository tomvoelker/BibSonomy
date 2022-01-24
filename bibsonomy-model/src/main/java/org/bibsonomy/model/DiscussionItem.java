/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author dzo
 * @author rja
 *
 */
@Getter
@Setter
public class DiscussionItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -640702214244246648L;

	/**
	 * the internal id; only use in database module!
	 * We need this here if we want to use groupBy in iBatis
	 */
	private Integer id;
	
	/**
	 * the hash representing this item
	 */
	private String hash;
	
	/**
	 * the groups of this item
	 */
	private Set<Group> groups;
	
	/**
	 * the user who posted this item
	 */
	private User user;

	/**
	 * the creation date
	 */
	private Date date;
	
	/**
	 * the <em>last</em> change date
	 */
	private Date changeDate;
	
	/**
	 * the comments of this item
	 */
	private List<DiscussionItem> subDiscussionItems;

	/**
	 * the hash of the parent discussion item
	 */
	private String parentHash;

	/**
	 * the user can decide if his username is published with the review
	 */
	private boolean anonymous;

	/**
	 * resource type the comment belongs to
	 */
	private Class<? extends Resource> resourceType;

	/**
	 * @return the comments
	 */
	public List<DiscussionItem> getSubDiscussionItems() {
		return this.subDiscussionItems;
	}

	/**
	 * @param discussionItem the discussion item to add
	 */
	public void addToDiscussionItems(final DiscussionItem discussionItem) {
		if (this.subDiscussionItems == null) {
			this.subDiscussionItems = new LinkedList<DiscussionItem>();
		}
		
		if (!this.subDiscussionItems.contains(discussionItem)) {
			this.subDiscussionItems.add(discussionItem);
		}
	}

	/**
	 * @return the groups
	 */
	public Set<Group> getGroups() {
		if (this.groups == null) {
			this.groups = new HashSet<Group>();
		}
		return this.groups;
	}

}
