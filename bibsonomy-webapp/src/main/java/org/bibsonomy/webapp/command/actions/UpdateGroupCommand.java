/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import java.io.Serializable;
import java.net.URL;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.model.Group;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * Command for the /updateGroup page
 * @author niebler
 */
public class UpdateGroupCommand extends BaseCommand implements Serializable {
	
	// TODO: Find out why on earth spring needs this and remove it again!
	private String requestedGroup;
	
	private Group group;
	private String groupname;
	private GroupUpdateOperation operation;
	private int privlevel;
	private int sharedDocuments;
	private String username;
	
	private String realname;
	private URL homepage;
	private String description;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupName) {
		this.groupname = groupName;
	}

	public GroupUpdateOperation getOperation() {
		return operation;
	}

	public void setOperation(GroupUpdateOperation operation) {
		this.operation = operation;
	}

	public int getPrivlevel() {
		return privlevel;
	}

	public void setPrivlevel(int privlevel) {
		this.privlevel = privlevel;
	}

	public int getSharedDocuments() {
		return sharedDocuments;
	}

	public void setSharedDocuments(int sharedDocuments) {
		this.sharedDocuments = sharedDocuments;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRequestedGroup() {
		return requestedGroup;
	}

	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public URL getHomepage() {
		return homepage;
	}

	public void setHomepage(URL homepage) {
		this.homepage = homepage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
