/**
 * BibSonomy-Layout - Layout engine for the webapp.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.layout.csl.model;

import java.util.ArrayList;

/**
 * DateParts in CSl are basically Lists of Strings.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 */
public class DateParts extends ArrayList<String> {
	private static final long serialVersionUID = 4578145208925557829L;

	/**
	* Constructor
	* 
	* @param year
	* @param month
	* @param day
	*/
	public DateParts(String year, String month, String day) {
		super();
		this.add(year);
		this.add(month);
		this.add(day);
	}

	/**
	* Constructor 
	* 
	* @param year
	* @param month
	*/
	public DateParts(String year, String month) {
		super();
		this.add(year);
		this.add(month);
	}

	/**
	* Constructor 
	* 
	* @param year
	*/
	public DateParts(String year) {
		super();
		this.add(year);
	}
}
