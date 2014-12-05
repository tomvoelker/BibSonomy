/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
 * Presentation modes for tag clouds
 * 
 * @author Dominik Benz
 */
public enum TagCloudStyle {
	/** cloud representation */
	CLOUD(0),
	/** list representation */
	LIST(1);

	private final int id;

	private TagCloudStyle(final int id) {
		this.id = id;
	}

	/**
	 * @return ID of this tag cloud sort mode
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id
	 * @return a TagCloudStyle object for the corresponding type
	 */
	public static TagCloudStyle getStyle(final int id) {
		switch (id) {
		case 0:
			return CLOUD;
		case 1:
			return LIST;
		default:
			throw new RuntimeException("Style " + id + " doesn't exist.");
		}
	}
}