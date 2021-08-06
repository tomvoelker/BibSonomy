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
package org.bibsonomy.common.information.utils;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.stream.Collectors;

import org.bibsonomy.common.information.JobInformation;

/**
 * util methods for jobInformation
 * @author dzo
 */
public final class JobInformationUtils {

	/**
	 * @param list
	 * @param jobInformationClass
	 * @return <code>true</code> iff at least one jobinformation is of the specified class
	 */
	public static boolean containsInformationType(List<JobInformation> list, Class<? extends JobInformation> jobInformationClass) {
		if (!present(list)) {
			return false;
		}
		return list.stream().anyMatch(jobInformationClass::isInstance);
	}

	/**
	 * @param list
	 * @param jobInformationClass
	 * @param <T>
	 * @return filters the list of information and returns only list items that are instances of the provided class
	 */
	public static <T extends JobInformation> List<T> extractInformationOfType(List<JobInformation> list, Class<? extends JobInformation> jobInformationClass) {
		return (List<T>) list.stream().filter(jobInformationClass::isInstance).collect(Collectors.toList());
	}
}
