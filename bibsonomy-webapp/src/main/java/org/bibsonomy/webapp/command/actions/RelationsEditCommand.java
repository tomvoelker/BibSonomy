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

/**
 * @author philipp
 */
public class RelationsEditCommand {
	
	private String upper = "";
	
	private String lower = "";
	
	/**
	 * which action is requested
	 * 0 = add relation
	 * 1 = del relation
	 */
	private int forcedAction;

	/**
	 * @param upper the upper to set
	 */
	public void setUpper(String upper) {
		this.upper = upper;
	}

	/**
	 * @return the upper
	 */
	public String getUpper() {
		return upper;
	}

	/**
	 * @param lower the lower to set
	 */
	public void setLower(String lower) {
		this.lower = lower;
	}

	/**
	 * @return the lower
	 */
	public String getLower() {
		return lower;
	}

	/**
	 * @param forcedAction the forcedAction to set
	 */
	public void setForcedAction(int forcedAction) {
		this.forcedAction = forcedAction;
	}

	/**
	 * @return the forcedAction
	 */
	public int getForcedAction() {
		return forcedAction;
	}

}
