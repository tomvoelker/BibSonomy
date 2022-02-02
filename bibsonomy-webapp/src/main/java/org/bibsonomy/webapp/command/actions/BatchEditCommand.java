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
package org.bibsonomy.webapp.command.actions;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.webapp.command.GroupingCommand;
import org.bibsonomy.webapp.command.TagResourceViewCommand;
import org.bibsonomy.webapp.util.PostChangeInfo;


/**
 * @author pbu
 * @author dzo
 */
@Getter
@Setter
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
