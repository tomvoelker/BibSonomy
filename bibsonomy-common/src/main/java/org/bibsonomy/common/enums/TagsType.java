/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
 * Enum which summarizes which kinds of tags we have in the system
 * (and which can be queried)
 * 
 * @author Dominik Benz
 */
public enum TagsType {
	/** the standard kind of tag */
	DEFAULT,
	/** related tags, i.e., tags which co-occur with a given tag */
	RELATED,
	/** similar tags, i.e., tags which are semantically similar to a given tag */
	SIMILAR,
	/** prefix tags, i.e., tags which are needed for auto-completion*/
	PREFIX;

	/**
	 * Returns the name for this kind of tags, i.e.:
	 * 
	 * <pre>
	 *  DEFAULT  - default
	 *  REALTED  - related
	 *  SIMILAR  - similar
	 *  PREFIX	 - prefix
	 * </pre>
	 * 
	 * @return an all lowercase string for this kind of tag
	 */
	public String getName() {
		return this.name().toLowerCase();
	}
}