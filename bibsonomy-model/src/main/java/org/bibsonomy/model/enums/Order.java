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

/**
 * Defines some ordering criteria for lists of entities
 * 
 * FIXME: the orderings "FREQUENCY" AND "POPULAR" probably mean the same. Check
 * their usage and if possible, remove one of them.
 * 
 * @author Jens Illig
 */
public enum Order {

	NONE,

	/** for ordering by adding time */
	DATE,

	/** for ordering by popularity */
	POPULAR,

	/** for ordering by rank (e. g. full text search by score) */
	RANK,

	/** for ordering by folkrank */
	FOLKRANK,

	/** for ordering tags by frequency */
	FREQUENCY,

	/** Some items can be ordered alphabetically ... (in particular groups) */
	ALPH,

	/** for ordering by entrytype */
	ENTRYTYPE,

	/** for ordering by title */
	TITLE,

	/** for ordering by booktitle */
	BOOKTITLE,

	/** for ordering by journal */
	JOURNAL,

	/** for ordering by series */
	SERIES,

	/** for ordering by publisher */
	PUBLISHER,

	/** for ordering by author names */
	AUTHOR,

	/** for ordering by editor names */
	EDITOR,

	/** for ordering by school */
	SCHOOL,

	/** for ordering by institution */
	INSTITUTION,

	/** for ordering by organization */
	ORGANIZATION,

	/** for ordering by publication year */
	YEAR,

	/** for ordering by publication month */
	MONTH,

	/** for ordering by publication day */
	DAY,

	/** for ordering by publication date */
	PUBDATE;

	/**
	 * Retrieve Order by name
	 * 
	 * @param name
	 *            the requested order (e.g. "folkrank")
	 * @return the corresponding Order enum
	 */
	public static Order getOrderByName(String name) {
		try {
			return Order.valueOf(name.toUpperCase());
		} catch (NullPointerException np) {
			throw new IllegalArgumentException("No order specified!");
		} catch (IllegalArgumentException ia) {
			throw new IllegalArgumentException("Requested order not supported.");
		}
	}
}