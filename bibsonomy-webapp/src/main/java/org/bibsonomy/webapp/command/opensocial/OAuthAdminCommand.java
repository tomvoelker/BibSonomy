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
package org.bibsonomy.webapp.command.opensocial;

import java.util.List;

import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;


/**
 * @author fei
 */
public class OAuthAdminCommand extends OAuthCommand {
	public enum AdminAction { List, Register, Remove };
	
	private String adminAction;
	
	private List<OAuthConsumerInfo> consumers;
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	
	public void setAdminAction(String authorizeAction) {
		this.adminAction = authorizeAction;
	}

	public String getAdminAction() {
		return adminAction;
	}
	
	/**
	 * tmp getter until spring's enum binding works again
	 * @return
	 */
	public AdminAction getAdminAction_() {
		return this.adminAction == null ? null : AdminAction.valueOf(this.adminAction);
	}

	public void setConsumers(List<OAuthConsumerInfo> consumers) {
		this.consumers = consumers;
	}

	public List<OAuthConsumerInfo> getConsumers() {
		return consumers;
	}

}
