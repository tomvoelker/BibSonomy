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
package org.bibsonomy.util;

import org.bibsonomy.common.exceptions.InternServerException;
import static org.bibsonomy.util.ValidationUtils.present;

/**
 * @author Christian Schenk
 */
public class EnumUtils {

	/**
	 * Searches for an enum contained in <code>values</code> where its
	 * lowercase name matches the string <code>name</code>.
	 * 
	 * @param <T>
	 *            an enum
	 * @param values
	 *            the values of an enum
	 * @param name
	 *            the name of an enum
	 * @return an enum contained in values or null
	 */
	public static <T extends Enum<?>> T searchEnumByName(final T[] values, final String name) {
		if (present(name) == false) throw new InternServerException("Parameter name must be set");
		for (final T value : values) {
			if (value.name().equalsIgnoreCase(name.trim())) return value;
		}
		return null;
	}
}