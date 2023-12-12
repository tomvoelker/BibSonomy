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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.metadata.PostMetaData;

/**
 * A post connects a given resource with a user and a certain date.
 * 
 * @param <R>
 *            resource type
 */
@Getter
@Setter
public class Post<R extends Resource> implements Serializable, Linkable {

	/**
	 * For persistency (Serializable)
	 */
	private static final long serialVersionUID = -4890029197498534435L;

	/**
	 * This is the {@link Resource} that this post is encapsulating.
	 */
	private R resource;
	
	/**
	 * for shared resource posts this contains the url of the post
	 */
	private String systemUrl;

	/**
	 * We need this here if we want to use groupBy in iBatis
	 * TODO: document me
	 * TODO: Is this field really part of the model?
	 */
	private Integer contentId;
	
	private List<ResourcePersonRelation> resourcePersonRelations;

	/**
	 * This post belongs to this {@link User}.
	 */
	private User user;

	/** contains a list of users that have this post in their collection (based on the interhash of the resource) */
	private List<User> users = new LinkedList<>();

	/**
	 * This post belongs to these {@link Group}s.
	 */
	private Set<Group> groups;

	/**
	 * This post is tagged with these {@link Tag}s.
	 */
	private Set<Tag> tags;
	
	/**
	 * This post is tagged with these {@link SystemTag}s
	 * they are hidden but can be called when needed
	 */
	private Set<Tag> hiddenSystemTags;
	/**
	 * This post is tagged with these {@link Tag}s
	 * they are not hidden
	 */
	private Set<Tag> visibleTags;

	/**
	 * This is the {@link Date} when this post was lastly modified.
	 */
	private Date changeDate;
	
	/**
	 * This is the {@link Date} when this post was created.
	 */
	private Date date;
	
	/**
	 * This is a text describing the post. <br/>
	 * 
	 * The description should be part of the post because it's a description
	 * individually made by one user for his post - another user may describe
	 * the post with another text.
	 */
	private String description;
	
	/**
	 * a ranking (used to sort a list of posts)
	 */
	private double ranking = 0.0;
	
	/**
	 * identifier if this post is picked or not
	 */
	private boolean picked = false;
	
	/**
	 * identifier if post is in the inbox
	 * use only to create the inbox page of a user
	 */
	private boolean isInboxPost = false;
	
	/**
	 * List of the collected metadata
	 */
	private List<PostMetaData> metaData;

	/**
	 * List of repositories where this post has been send to (PUMA specific)
	 */
	private List<Repository> repositories;

	/**
	 * This is the user who owns the post which should be copied.
	 * TODO: use User as type
	 */
	private String copyFrom;
	
	private boolean approved = false;
	
	
	/**
	 * default constructor
	 */
	public Post() {
		// noop
	}
	
	/**
	 * copies the post without the resource
	 * 
	 * @param post the post to copy
	 * @param withoutResource XXX: unused for distinguish between 
	 */
	public Post(final Post<?> post, final boolean withoutResource) {
		if (!withoutResource) {
			throw new IllegalArgumentException();
		}
		
		this.systemUrl = post.getSystemUrl();
		this.contentId = post.getContentId();
		
		this.resourcePersonRelations = post.getResourcePersonRelations();
		
		this.user = post.getUser();
		this.groups = post.getGroups();
		
		this.tags = post.getTags();
		this.hiddenSystemTags = post.getHiddenSystemTags();
		this.visibleTags = post.getVisibleTags();
		
		
		this.isInboxPost = post.isInboxPost();
		this.picked = post.isPicked();
		
		this.date = post.getDate();
		this.changeDate = post.getChangeDate();
		
		this.description = post.getDescription();
		
		this.ranking = post.getRanking();
		
		this.metaData = post.getMetaData();
		this.repositories = post.getRepositories();
		this.copyFrom = post.getCopyFrom();
		this.approved = post.isApproved();
	}

	/**
	 * @return groups
	 */
	public Set<Group> getGroups() {
		if (this.groups == null) {
			this.groups = new HashSet<>();
		}
		return this.groups;
	}


	/**
	 * @return tags
	 */
	public Set<Tag> getTags() {
		if (this.tags == null) {
			/*
			 * a linked hash set gives predictable iteration order
			 * (insertion order)
			 */
			this.tags = new LinkedHashSet<>();
		}
		return this.tags;
	}

	/**
	 * Convenience method to add a tag.
	 * 
	 * @param tagName
	 */
	public void addTag(final String tagName) {
		// call getTags to initialize this.tags
		this.getTags();
		// add the tag
		this.tags.add(new Tag(tagName));
	}

	/**
	 * Convenience method to add a group.
	 * 
	 * @param groupName
	 */
	public void addGroup(final String groupName) {
		// call getGroups to initialize this.groups
		this.getGroups();
		// add the group
		this.groups.add(new Group(groupName));
	}

	@Override
	public String toString() {
		return "\n" + (this.user == null ? "" : this.user.getName()) + "\n\ttagged\n\t\t" + this.resource + "\n\twith\n" + this.tags;
	}

	/**
	 * Add a SystemTag (Tag) to the HiddenSystemTag list
	 * 
	 * @param tag
	 */
	public void addHiddenSystemTag(final Tag tag) {
		if (!present(this.hiddenSystemTags)) {
			this.hiddenSystemTags = new HashSet<>();
		}
		this.hiddenSystemTags.add(tag);
	}

	/**
	 * @param tag
	 */
	public void addVisibleTag(final Tag tag) {
		if (!present(this.visibleTags)) {
			this.visibleTags = new HashSet<>();
		}
		this.visibleTags.add(tag);
	}

	@Override
	public String getLinkableId() {
		return this.resource.getInterHash();
	}

	@Override
	public Integer getId() {
		return this.contentId;
	}
}