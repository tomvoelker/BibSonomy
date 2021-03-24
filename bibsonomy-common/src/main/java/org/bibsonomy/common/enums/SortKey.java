/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
package org.bibsonomy.common.enums;

/**
 * Defines sorting criteria (aka sort keys) for displayed lists.
 * 
 * The difference between this enum and the one defined in 
 *   org.bibsonomy.model.enums.Order
 * is that the latter defines sorting criteria which are applied when
 * retrieving items from the database (i.e. arguments to ORDER BY..). This
 * class here defines sorting options which are applied only for the currently 
 * displayed entries, e.g. the first 10 ones.
 * 
 * The sort order (asc / desc) is defined in org.bibsonomy.common.enums.SortOrder 
 * 
 * @author Dominik Benz
 * @see org.bibsonomy.common.enums.SortOrder
 */
public enum SortKey {
	/** no re-sorting, keep order as it comes from DB/search index */
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

	NUMBER,

	NOTE,

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
	 * Retrieve Sort Key by name
	 *
	 * @param name	the requested sort key (e.g. "folkrank")
	 * @return the corresponding SortKey enum, returns RANK if no match found
	 */
	public static SortKey getByName(String name) {
		try {
			return SortKey.valueOf(name.toUpperCase());
		} catch (NullPointerException | IllegalArgumentException e) {
			return SortKey.RANK;
		}
	}
}