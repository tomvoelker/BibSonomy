/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.params;

import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;

/**
 * used to store {@link SamlRemoteUserId}s which do not themselves have a backlink to the user object
 * 
 * @author jensi
 */
public class SamlUserParam {
	
	protected SamlRemoteUserId samlRemoteUserId;
	protected User user;

	/**
	 * default constructor
	 */
	public SamlUserParam() {
	}

	/**
	 * handy constructor
	 * @param user
	 * @param samlRemoteUserId
	 */
	public SamlUserParam(User user, SamlRemoteUserId samlRemoteUserId) {
		this.user = user;
		this.samlRemoteUserId = samlRemoteUserId;
	}

	public SamlRemoteUserId getSamlRemoteUserId() {
		return this.samlRemoteUserId;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param samlRemoteUserId the remoteId to set
	 */
	public void setSamlRemoteUserId(final SamlRemoteUserId samlRemoteUserId) {
		this.samlRemoteUserId = samlRemoteUserId;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
