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
package org.bibsonomy.webapp.command;

/**
 * bean for a page in a multipaged listview context
 * 
 * @author Jens Illig
 */
public class PageCommand {
	private Integer number;
	private int start;
	
	/**
	 * default bean constructor
	 */
	public PageCommand() {
	}
	
	/**
	 * @param number index of this page (normally displayed in the view and
	 *        therefore starting with 1) 
	 * @param start index of the first entity in the sublist on this page
	 *        (starting with 0)
	 */
	public PageCommand(final Integer number, final int start) {
		this.number = number;
		this.start = start;
	}
	
	/**
	 * @return index of this page (normally displayed in the view and
	 *         therefore starting with 1). May be null if unknown
	 */
	public Integer getNumber() {
		return this.number;
	}
	
	/**
	 * @param number index of this page (normally displayed in the view and
	 *        therefore starting with 1). May be null if unknown
	 */
	public void setNumber(final Integer number) {
		this.number = number;
	}
	
	/**
	 * @return index of the first entity in the sublist on this page
	 *         (starting with 0)
	 */
	public int getStart() {
		return this.start;
	}
	
	/**
	 * @param start index of the first entity in the sublist on this page
	 *              (starting with 0)
	 */
	public void setStart(final int start) {
		this.start = start;
	}
}
