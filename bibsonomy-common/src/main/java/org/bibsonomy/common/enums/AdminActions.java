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

import org.bibsonomy.util.EnumUtils;

/**
 * Defines different possibilities of classifiers of a user
 * 
 * @author Beate Krause
 */
public enum AdminActions {
	/** An automatic classifier algorithm */
	FLAG_SPAMMER("flag_spammer"),

	UNFLAG_SPAMMER("unflag_spammer"),

	MARK_UNCERTAINUSER("mark_uncertainuser"),

	UPDATE_SETTINGS("update_settings"),

	LATEST_POSTS("latest_posts"),

	PREDICTION_HISTORY("prediction_history"),

	FETCH_GROUP_WITH_PERMISSIONS("fetch_group_with_permissions");

	AdminActions(final String action) {
	}

	/**
	 * @param setting
	 *        name of the setting enum to retrieve
	 * @return the corresponding enum object
	 */
	public static AdminActions getAdminAction(final String action) {
		final AdminActions adminAction = EnumUtils.searchEnumByName(AdminActions.values(), action);
		if (adminAction == null) {
			throw new UnsupportedOperationException();
		}
		return adminAction;
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

}