/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import org.bibsonomy.model.user.remote.SamlRemoteUserId;

/**
 * @author jensi
 */
public class SamlUserIDRegistrationCommand extends UserIDRegistrationCommand {
	private static final long serialVersionUID = -2989822342885077454L;

	private SamlRemoteUserId samlId = new SamlRemoteUserId();

	/**
	 * @return the samlId
	 */
	public SamlRemoteUserId getSamlId() {
		return this.samlId;
	}

	/**
	 * @param samlId the samlId to set
	 */
	public void setSamlId(SamlRemoteUserId samlId) {
		this.samlId = samlId;
	}
}
