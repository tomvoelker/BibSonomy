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
package org.bibsonomy.webapp.command;

import org.bibsonomy.common.SortCriterium;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.model.Tag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;

/**
 * Bean for Tag Sites
 * 
 * @author Michael Wagner
 */
public class TagResourceViewCommand extends SimpleResourceViewCommand {
	
	/**
	 * the selected search type such as 'group', 'search', 'sharedResourceSearch'  
	 */
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

	/** the specified list of sort criteriums */
	private List<SortCriterium> sortCriteriums = new LinkedList<>();

	/**
	 * Use the elasticsearch index for retrieving and sorting listed posts?
	 */
	private boolean esIndex = true;

	/** bean for related tags */
	private RelatedTagCommand relatedTagCommand = new RelatedTagCommand();
	
	/** re-using relatedTagCommand to store similar tags */
	private RelatedTagCommand similarTags = new RelatedTagCommand();
	
	/** related users - needed for FolkRank */
	private RelatedUserCommand relatedUserCommand = new RelatedUserCommand();

	private int postCountForTagsForLoginUser = 0;
	private int postCountForTagsForRequestedUser = 0;
	private int postCountForTagsForGroup = 0;
	private int postCountForTagsForAll = 0;
	private List<Tag> conceptsOfLoginUser = new ArrayList<Tag>();
	private List<Tag> conceptsOfRequestedUser = new ArrayList<Tag>();
	private List<Tag> conceptsOfGroup = new ArrayList<Tag>();
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
	 * @return requested tags as string
	 */
	public String getRequestedTags() {
		return this.requestedTags;
	}
	
	/**
	 * sets the requested tags
	 * @param requestedTags 
	 */
	public void setRequestedTags(final String requestedTags) {
		relatedTagCommand.setRequestedTags(requestedTags);
		this.requestedTags = requestedTags;
	}

	/**
	 * @return command with related tags
	 */
	public RelatedTagCommand getRelatedTagCommand() {
		return this.relatedTagCommand;
	}
	
	/**
	 * @return the relatedUserCommand
	 */
	public RelatedUserCommand getRelatedUserCommand() {
		return this.relatedUserCommand;
	}
	
	/**
	 * @param relatedUserCommand
	 */
	public void setRelatedUserCommand(final RelatedUserCommand relatedUserCommand) {
		this.relatedUserCommand = relatedUserCommand;
	}

	/**
	 * @param relatedTagCommand command with related tags
	 */
	public void setRelatedTagCommand(final RelatedTagCommand relatedTagCommand) {
		this.relatedTagCommand = relatedTagCommand;
	}

	/**
	 * @return sortKey
	 */
	public SortKey getSortKey() {
		return sortKey;
	}

	/**
	 * @param sortKey	the sort key to set
	 */
	public void setSortKey(SortKey sortKey) {
		this.sortKey = sortKey;
	}

	/**
	 * List of sorting criteriums
	 * @return sortCriteriums
	 */
	public List<SortCriterium> getSortCriteriums() {
		return sortCriteriums;
	}

	/**
	 * @param sortCriteriums	set the list of sort criteriums
	 */
	public void setSortCriteriums(List<SortCriterium> sortCriteriums) {
		this.sortCriteriums = sortCriteriums;
	}

	/**
	 * @return esIndex
	 */
	public boolean isEsIndex() {
		return this.esIndex;
	}

	/**
	 * Set wether to use elasticsearch index for retrieving and sorting posts.
	 * @param 	esIndex		use elasticsearch index?
	 */
	public void setEsIndex(boolean esIndex) {
		this.esIndex = esIndex;
	}

	/**
	 * @return the similarTags
	 */
	public RelatedTagCommand getSimilarTags() {
		return this.similarTags;
	}

	/**
	 * @param similarTags the similarTags to set
	 */
	public void setSimilarTags(final RelatedTagCommand similarTags) {
		this.similarTags = similarTags;
	}
	
	
	/**
	 * @param postCount
	 */
	public void setPostCountForTagsForLoginUser(final int postCount) {
		this.postCountForTagsForLoginUser = postCount;
	}

	/**
	 * @return  number of loginUser's posts for the requestedTags
	 */
	public int getPostCountForTagsForLoginUser() {
		return postCountForTagsForLoginUser;
	}
	
	/**
	 * @param postCount
	 */
	public void setPostCountForTagsForRequestedUser(final int postCount) {
		this.postCountForTagsForRequestedUser = postCount;
	}

	/**
	 * @return number of requestedUser's posts for the requestedTags
	 */
	public int getPostCountForTagsForRequestedUser() {
		return postCountForTagsForRequestedUser;
	}
	
	/**
	 * @param postCount
	 */
	public void setPostCountForTagsForGroup(final int postCount) {
		this.postCountForTagsForGroup = postCount;
	}

	/**
	 * @return number of requestedGroup's posts for the requestedTags
	 */
	public int getPostCountForTagsForGroup() {
		return postCountForTagsForGroup;
	}
	
	/**
	 * @param postCount
	 */
	public void setPostCountForTagsForAll(final int postCount) {
		this.postCountForTagsForAll = postCount;
	}

	/**
	 * @return number of all posts for the requestedTags
	 */
	public int getPostCountForTagsForAll() {
		return postCountForTagsForAll;
	}
	
	/**
	 * @param conceptsOfLoginUser
	 */
	public void setConceptsOfLoginUser(final List<Tag> conceptsOfLoginUser) {
		this.conceptsOfLoginUser = conceptsOfLoginUser;
	}

	/**
	 * @return conceptsOfLoginUser (a list of tags)
	 */
	public List<Tag> getConceptsOfLoginUser() {
		return conceptsOfLoginUser;
	}
	
	/**
	 * @param conceptsOfRequestedUser
	 */
	public void setConceptsOfRequestedUser(final List<Tag> conceptsOfRequestedUser) {
		this.conceptsOfRequestedUser = conceptsOfRequestedUser;
	}

	/**
	 * @return conceptsOfRequestedUser (a list of tags)
	 */
	public List<Tag> getConceptsOfRequestedUser() {
		return conceptsOfRequestedUser;
	}
	
	/**
	 * @param conceptsOfGroup
	 */
	public void setConceptsOfGroup(final List<Tag> conceptsOfGroup) {
		this.conceptsOfGroup = conceptsOfGroup;
	}

	/**
	 * @return conceptsOfGroup (a list of tags)
	 */
	public List<Tag> getConceptsOfGroup() {
		return conceptsOfGroup;
	}
	
	/**
	 * @param conceptsOfAll
	 */
	public void setConceptsOfAll(final List<Tag> conceptsOfAll) {
		this.conceptsOfAll = conceptsOfAll;
	}

	/**
	 * @return conceptsOfAll (a list of tags)
	 */
	public List<Tag> getConceptsOfAll() {
		return conceptsOfAll;
	}

	/**
	 * @return the numberOfNormalTags
	 */
	public int getNumberOfNormalTags() {
		return this.numberOfNormalTags;
	}

	/**
	 * @param numberOfNormalTags the numberOfNormalTags to set
	 */
	public void setNumberOfNormalTags(int numberOfNormalTags) {
		this.numberOfNormalTags = numberOfNormalTags;
	}

	/**
	 * @return the selected search type such as 'group', 'search', 'sharedResourceSearch'
	 */
	public QueryScope getScope() {
		return this.scope;
	}

	/**
	 * @param scope the selected search type such as 'group', 'search', 'sharedResourceSearch'
	 */
	public void setScope(QueryScope scope) {
		this.scope = scope;
	}
}
