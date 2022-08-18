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
 * Enum that contains all possible query/update operations for persons
 * @author mho
 */
public enum PersonOperation {

	/** Updates all attributes of a person */
	UPDATE_ALL,

	/** Updates all details of a person */
	UPDATE_DETAILS,

	/** Update the names of a person */
	UPDATE_NAMES,

	/** Update the additional keys of a person */
	UPDATE_ADDITIONAL_KEYS,

	/** Creates a new person */
	CREATE_PERSON,

	/** Add alternative name of a person */
	ADD_NAME,

	/** Delete alternative name of a person */
	DELETE_NAME,

	/** Select alternative name as main name of a person */
	SELECT_MAIN_NAME,

	/** Add role of person to a resource relation */
	ADD_ROLE,

	/** Delete role of person to a resource relation */
	DELETE_ROLE,

	/** Update role of a person to a resource relation */
	UPDATE_ROLE,

	/** link person to a user */
	LINK_USER,

	/** unlink person to a user */
	UNLINK_USER,

	/** accepts merge */
	MERGE_ACCEPT,
	
	/** denies merge */
	MERGE_DENIED,

	/** merge conflicts */
	MERGE_CONFLICTS,

	/** get merge conflicts */
	MERGE_GET_CONFLICTS,

	/** search for authors */
	SEARCH,

	/** search for publications, that are linked to persons */
	SEARCH_PUB,

	/** search for authors or linked publications */
	SEARCH_PUB_AUTHOR,

}
