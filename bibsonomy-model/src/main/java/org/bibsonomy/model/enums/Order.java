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
 * TODO: rename class to PostOrder
 *
 * Defines some ordering criteria for lists of entities
 * 
 * FIXME: the orderings "FREQUENCY" AND "POPULAR" probably mean the same. Check
 * their usage and if possible, remove one of them.
 * 
 * @author Jens Illig
 */
public enum Order {
	/** for ordering by adding time (desc) */
	ADDED,
	/** for ordering by popularity (desc) */
	POPULAR,
	/**
	 * for ordering by rank (e. g. full text search by score) 
	 */
	RANK,
	/** for ordering by folkrank (desc) */
	FOLKRANK,	
	/** for ordering tags by frequency (desc) */
	FREQUENCY,	
	/**
	 * Some items can be ordered alphabetically ...
	 * (in particular groups)
	 */
	ALPH;

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
			throw new IllegalArgumentException("Requested order not supported. Possible values are 'added', 'popular', 'alph', 'frequency' or 'folkrank'");
		}
	}
}