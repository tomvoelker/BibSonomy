/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.enums;

public enum SortDirection {

	ASC,
	DESC;

	/**
	 * Retrieve SortDirection by name
	 *
	 * @param name "asc" or "desc"
	 *
	 * @return the corresponding SortDirection enum
	 */
	public static SortDirection getByName(String name) {
		try {
			return SortDirection.valueOf(name.toUpperCase());
		} catch (NullPointerException np) {
			throw new IllegalArgumentException("No sort direction specified!");
		} catch (IllegalArgumentException ia) {
			throw new IllegalArgumentException("Requested sort direction not supported. Possible values are 'asc' or 'desc'.");
		}
	}
}
