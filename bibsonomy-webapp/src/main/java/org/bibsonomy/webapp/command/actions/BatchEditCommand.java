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
package org.bibsonomy.webapp.command.actions;

import java.util.List;
import java.util.Map;

import org.bibsonomy.webapp.command.GroupingCommand;
import org.bibsonomy.webapp.command.TagResourceViewCommand;
import org.bibsonomy.webapp.util.PostChangeInfo;


/**
 * @author pbu
 * @author dzo
 */
public class BatchEditCommand extends TagResourceViewCommand implements GroupingCommand {

	/**
	 * should publications be edited before they're stored?
	 */
	private boolean editBeforeImport = false;

	/**
	 * this flag determines, whether an existing post is being edited or a new post
	 * should be added and edited**/
	private boolean updateExistingPost;
	/**
	 * when batchedit is used after importing posts, this flag
	 * stores if the user wants to overwrite existing posts
	 */
	private boolean overwrite;
	/**
	 * these tags will be added to all resources
	 */
	private String tags;
	/**
	 * hashes of the resources which posts were selected (hash as key and "on" as value (checkbox))
	 */
	private Map<String, PostChangeInfo> posts;
	/**
	 * actions to apply to post
	 */
	private List<Integer> action;

	private String abstractGrouping;

	private List<String> groups;

	/*
	 * true means: we are in the batch edit page
	 * false means: we are in the snippet posting workflow
	 */
	private boolean directEdit;


	/**
	 * @return the groups
	 */
	@Override
	public List<String> getGroups() {
		return this.groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	@Override
	public void setGroups(final List<String> groups) {
		this.groups = groups;
	}

	/**
	 * @return the tags
	 */
	public String getTags() {
		return this.tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(final String tags) {
		this.tags = tags;
	}

	/**
	 * @return the posts
	 */
	public Map<String, PostChangeInfo> getPosts() {
		return this.posts;
	}

	/**
	 * @param posts the posts to set
	 */
	public void setPosts(final Map<String, PostChangeInfo> posts) {
		this.posts = posts;
	}

	/**
	 * @return the action
	 */
	public List<Integer> getAction() {
		return this.action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(final List<Integer> action) {
		this.action = action;
	}

	/**
	 * @return the editBeforeImport
	 */
	public boolean isEditBeforeImport() {
		return this.editBeforeImport;
	}

	/**
	 * @param editBeforeImport the editBeforeImport to set
	 */
	public void setEditBeforeImport(final boolean editBeforeImport) {
		this.editBeforeImport = editBeforeImport;
	}

	/**
	 * @return the overwrite
	 */
	public boolean isOverwrite() {
		return this.overwrite;
	}

	/**
	 * @param overwrite the overwrite to set
	 */
	public void setOverwrite(final boolean overwrite) {
		this.overwrite = overwrite;
	}

	/**
	 * @return the directEdit
	 */
	public boolean isDirectEdit() {
		return this.directEdit;
	}

	/**
	 * @param directEdit the directEdit to set
	 */
	public void setDirectEdit(final boolean directEdit) {
		this.directEdit = directEdit;
	}

	/**
	 * @return the updateExistingPost
	 */
	public boolean isUpdateExistingPost() {
		return this.updateExistingPost;
	}

	/**
	 * @param updateExistingPost the updateExistingPost to set
	 */
	public void setUpdateExistingPost(final boolean updateExistingPost) {
		this.updateExistingPost = updateExistingPost;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.GroupingCommand#getAbstractGrouping()
	 */
	@Override
	public String getAbstractGrouping() {
		return this.abstractGrouping;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.GroupingCommand#setAbstractGrouping(java.lang.String)
	 */
	@Override
	public void setAbstractGrouping(final String abstractGrouping) {
		this.abstractGrouping = abstractGrouping;

	}

}
