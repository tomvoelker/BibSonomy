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
import java.util.Set;

import org.bibsonomy.common.enums.AdminActions;
import org.bibsonomy.common.enums.GroupLevelPermission;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * Command for ajax requests from admin page
 * 
 * @author Stefan Stützer
 */
public class AdminAjaxCommand extends AjaxCommand<AdminActions> {
	
	/** list of bookmarks of an user */
	private List<Post<Bookmark>> bookmarks;
	
	/** prediction history of a user  */
	private List<User> predictionHistory;
	
	/** user for which we want to add a group or mark as spammer */
	private String userName; 
	
	/** key for updating classifier settings */
	private String key;
	
	/** value for updating classifier settings */
	private String value;
	
	/** show spam posts; enabled by default*/
	private String showSpamPosts = "true";
	
	/** total number of bookmarks*/
	private int bookmarkCount;
	
	/** total number of bibtex*/
	private int bibtexCount;
	
	/** evaluator name */
	private String evaluator;

	private String groupname;
	
	private Set<GroupLevelPermission> groupLevelPermissions;
	
	
	/**
	 * @return the bookmarks
	 */
	public List<Post<Bookmark>> getBookmarks() {
		return this.bookmarks;
	}

	/**
	 * @param bookmarks the bookmarks to set
	 */
	public void setBookmarks(final List<Post<Bookmark>> bookmarks) {
		this.bookmarks = bookmarks;
	}

	/**
	 * @return the predictionHistory
	 */
	public List<User> getPredictionHistory() {
		return this.predictionHistory;
	}

	/**
	 * @param predictionHistory the predictionHistory to set
	 */
	public void setPredictionHistory(final List<User> predictionHistory) {
		this.predictionHistory = predictionHistory;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * @return the showSpamPosts
	 */
	public String getShowSpamPosts() {
		return this.showSpamPosts;
	}

	/**
	 * @param showSpamPosts the showSpamPosts to set
	 */
	public void setShowSpamPosts(final String showSpamPosts) {
		this.showSpamPosts = showSpamPosts;
	}

	/**
	 * @return the bookmarkCount
	 */
	public int getBookmarkCount() {
		return this.bookmarkCount;
	}

	/**
	 * @param bookmarkCount the bookmarkCount to set
	 */
	public void setBookmarkCount(final int bookmarkCount) {
		this.bookmarkCount = bookmarkCount;
	}

	/**
	 * @return the bibtexCount
	 */
	public int getBibtexCount() {
		return this.bibtexCount;
	}

	/**
	 * @param bibtexCount the bibtexCount to set
	 */
	public void setBibtexCount(final int bibtexCount) {
		this.bibtexCount = bibtexCount;
	}

	/**
	 * @return the evaluator
	 */
	public String getEvaluator() {
		return this.evaluator;
	}

	/**
	 * @param evaluator the evaluator to set
	 */
	public void setEvaluator(final String evaluator) {
		this.evaluator = evaluator;
	}

	public String getGroupname() {
		return this.groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public Set<GroupLevelPermission> getGroupLevelPermissions() {
		return this.groupLevelPermissions;
	}

	public void setGroupLevelPermissions(Set<GroupLevelPermission> groupLevelPermissions) {
		this.groupLevelPermissions = groupLevelPermissions;
	}
}