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
 * @author cvo
 */
public enum GroupUpdateOperation {

	/** Update the settings of a group. */
	UPDATE_SETTINGS,

	/** Update the grouprole of a user */
	UPDATE_GROUPROLE,

	/** Adds new user to a group. */
	ADD_MEMBER,

	/** Removes a user from the group. */
	REMOVE_MEMBER,

	/** Update the whole group */
	UPDATE_ALL,

	/** Update the user specific shared documents flag */
	UPDATE_USER_SHARED_DOCUMENTS,

	/** update the publication reporting settings for a group */
	UPDATE_GROUP_REPORTING_SETTINGS,

	/** Activate the pending group */
	ACTIVATE,

	/** Adds a join request */
	ADD_REQUESTED,

	/** Add an invited user */
	ADD_INVITED,

	/** Remove an invited user */
	REMOVE_INVITED,

	// FIXME: no group update operation
	/** Request a new group. */
	REQUEST,

	/** Decline a join request */
	DECLINE_JOIN_REQUEST,

	/** Add a group-level-permission @see org.bibsonomy.GroupLevelPermission **/
	UPDATE_PERMISSIONS,
	
	/** deletes the pending group - used for the request deletion by the user */
	DELETE_GROUP_REQUEST,

	/** Delete a preset tag */
	DELETE_PRESET_TAG,

	/** Add or update a preset tag */
	UPDATE_PRESET_TAG,
	
	/** updates the API key of the group user **/
	REGENERATE_API_KEY;
}
