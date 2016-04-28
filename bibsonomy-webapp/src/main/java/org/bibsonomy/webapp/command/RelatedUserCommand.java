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
package org.bibsonomy.webapp.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.User;

/**
 * @author daill
 */
public class RelatedUserCommand extends BaseCommand {
	
	/**
	 *  set of user to show
	 */
	private Set<User> relatedUsers = new HashSet<User>();

	/**
	 * @return list of user
	 */
	public Set<User> getRelatedUsers() {
		return this.relatedUsers;
	}

	/**
	 * @param relatedUsers the relatedUsers to set
	 */
	public void setRelatedUsers(final Set<User> relatedUsers) {
		this.relatedUsers = relatedUsers;
	}
	
	/**
	 * @param relatedUsers the relatedUsers to set
	 */
	public void setRelatedUsers(final List<User> relatedUsers) {
		this.relatedUsers = new HashSet<User>(relatedUsers);
	}

}
