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
package org.bibsonomy.webapp.command.ajax;

/**
 * Command for sending information about a clicked recommended
 * post to the server.
 * 
 * @author lukas
 */
public class AjaxItemRecommenderFeedbackCommand extends AjaxCommand {

	private String intraHash;
	private String userName;
	
	/**
	 * @return the intrahash
	 */
	public String getIntraHash() {
		return this.intraHash;
	}
	/**
	 * @param intraHash the intrahash
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}
	/**
	 * @return the username
	 */
	public String getUserName() {
		return this.userName;
	}
	/**
	 * @param userName the username
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
