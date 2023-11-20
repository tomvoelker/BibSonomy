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
package org.bibsonomy.webapp.command;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.model.Tag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Bean for Tag Sites
 * 
 * @author Michael Wagner
 */
@Getter
@Setter
public class TagResourceViewCommand extends SimpleResourceViewCommand {
	
	/** the selected search type such as 'group', 'search', 'sharedResourceSearch' */
	private QueryScope scope = QueryScope.LOCAL;
	
	/** tags to search for */
	private String requestedTags = "";
	
	/** tags to search for, as list */
	private List<String> requestedTagsList = null;
	
	/**
	 * the number of normal tags (no system tags)
	 * TODO: remove as soon as we can check for system tags in the view
	 */
	private int numberOfNormalTags;
	
	/** the specified sorting key */
	private SortKey sortKey;

	/** the specified list of sort criteria */
	private List<SortCriteria> sortCriteria = new LinkedList<>();

	/** bean for related tags */
	private RelatedTagCommand relatedTagCommand = new RelatedTagCommand();
	
	/** re-using relatedTagCommand to store similar tags */
	private RelatedTagCommand similarTags = new RelatedTagCommand();
	
	/** related users - needed for FolkRank */
	private RelatedUserCommand relatedUserCommand = new RelatedUserCommand();

	/** number of loginUser's posts for the requestedTags */
	private int postCountForTagsForLoginUser = 0;

	/** number of requestedUser's posts for the requestedTags */
	private int postCountForTagsForRequestedUser = 0;

	/** number of requestedGroup's posts for the requestedTags */
	private int postCountForTagsForGroup = 0;

	/**  number of all posts for the requestedTags */
	private int postCountForTagsForAll = 0;

	/** conceptsOfLoginUser (a list of tags) */
	private List<Tag> conceptsOfLoginUser = new ArrayList<Tag>();

	/** conceptsOfRequestedUser (a list of tags) */
	private List<Tag> conceptsOfRequestedUser = new ArrayList<Tag>();

	/** conceptsOfGroup (a list of tags) */
	private List<Tag> conceptsOfGroup = new ArrayList<Tag>();

	/** conceptsOfAll (a list of tags) */
	private List<Tag> conceptsOfAll = new ArrayList<Tag>();
	
	/**
	 * @return the requested tagstring as a list
	 */
	public List<String> getRequestedTagsList() {
		// tagstring has not yet been tokenized 
		if (this.requestedTagsList == null) {
			this.requestedTagsList = new ArrayList<String>();
			final StringTokenizer st = new StringTokenizer(requestedTags);
			while (st.hasMoreTokens()) {
				final String tagname = st.nextToken();
				this.requestedTagsList.add(tagname);
			}
		}
		return this.requestedTagsList;
	}

	/**
	 * sets the requested tags
	 * @param requestedTags 
	 */
	public void setRequestedTags(final String requestedTags) {
		relatedTagCommand.setRequestedTags(requestedTags);
		this.requestedTags = requestedTags;
	}

}
