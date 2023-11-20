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
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
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
	
}