/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * Depicts which party of a user should be updated when calling 
 * the <code>update(...)</code> method in the LogicInterface.
 * 
 * @author cvo
 */
public enum UserUpdateOperation {

	/**
	 * Update all parts of the entity.
	 */
	UPDATE_ALL,
	
	/**
	 * Update only the password of a user.
	 */
	UPDATE_PASSWORD,
	
	/**
	 * Update only the settings of a user.
	 */
	UPDATE_SETTINGS,
	
	/**
	 * Update only the core settings of a user (personal data, like homepage etc.)
	 */
	UPDATE_CORE,
	
	/**
	 * flag / unflag a user as a spammer
	 */
	UPDATE_SPAMMER_STATUS,
	
	/**
	 * Update only the API key of a user
	 */
	UPDATE_API,
	
	/**
	 * Activates the user
	 */
	ACTIVATE,
	
	/**
	 * used for upgrading a limited user account
	 */
	UPDATE_LIMITED_USER,
	
	/**
	 * used for deleting openID access of an user
	 */
	DELETE_OPENID;
}
