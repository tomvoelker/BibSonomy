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

import java.net.URL;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 *
 * @author niebler
 */
@Setter
@Getter
public class GroupSettingsPageCommand extends TabsCommand<Object> {

	// tabs
	public final static int GROUP_SETTINGS = 0;
	public final static int MEMBER_LIST_IDX = 1;
	public final static int DELETE_GROUP = 3;
	public final static int CV_IDX = 2;

	// general attributes
	private Group group;
	private User user;
	private User loggedinUser;
	private String requestedGroup;
	private boolean userSharedDocuments;

	private GroupRole groupRole;
	private GroupMembership groupMembership;

	private String username;
	// TODO: WHat's this for?
	private String groupname;

	// group specific settings. maybe move them to another page?
	private String realname;
	private URL homepage;
	private String description;
	
	// TODO should be Privlevel type
	private int privlevel;
	// TODO: boolean type
	private int sharedDocuments;
	private boolean allowJoin;
	
	// specific settings for the group user
	private CommonsMultipartFile file;

	// cv settings
	private String wikiText;
	private String renderedWikiText;

	// bla
	private GroupUpdateOperation operation;
	
	// tmp error message from URL
	private String errorMessage;

	/**
	 * delete the group yes or no
	 */
	private String delete = null;
	
	/**
	 * new profile picture file to upload
	 */
	private MultipartFile pictureFile;

	/**
	 * flag to indicate whether a prior uploaded picture file shall be deleted
	 */
	private boolean deletePicture;
	
	/**
	 * TODO: remove after setting the default value of selTab to null
	 */
	public GroupSettingsPageCommand() {
		this.setSelTab(null);
	}

}
