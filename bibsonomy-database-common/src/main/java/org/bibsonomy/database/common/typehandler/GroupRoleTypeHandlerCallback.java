/**
 * BibSonomy-Database-Common - Helper classes for database interaction
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
package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;

import org.bibsonomy.common.enums.GroupRole;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * An iBATIS type handler callback for {@link GroupRole}es that are mapped to
 * Strings in the database. If a GroupRole cannot be constructed based on the
 * String, then the GroupRole will be set to <code>USER</code>.<br/>
 * 
 * Almost copied from <a href=
 * "http://opensource.atlassian.com/confluence/oss/display/IBATIS/Type+Handler+Callbacks"
 * >Atlassian - Type Handler Callbacks</a>
 * 
 * @author Clemens Baier
 */
public class GroupRoleTypeHandlerCallback extends AbstractTypeHandlerCallback {
	@Override
	public void setParameter(final ParameterSetter setter,
			final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(GroupRole.USER.getRole());
		} else {
			final GroupRole role = (GroupRole) parameter;
			setter.setInt(role.getRole());
		}
	}

	@Override
	public Object valueOf(final String str) {
		try {
			return GroupRole.getGroupRole(str);
		} catch (NumberFormatException ex) {
			return GroupRole.USER;
		}
	}

}
