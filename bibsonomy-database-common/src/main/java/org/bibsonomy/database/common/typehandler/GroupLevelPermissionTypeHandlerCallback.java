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

import org.bibsonomy.common.enums.GroupLevelPermission;
import org.bibsonomy.common.enums.Role;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * An iBATIS type handler callback for {@link Role}es that are mapped to
 * Strings in the database. If a Role cannot be constructed based on the String,
 * then the Role will be set to <code>DEFAULT</code>.<br/>
 * 
 * Almost copied from <a
 * href="http://opensource.atlassian.com/confluence/oss/display/IBATIS/Type+Handler+Callbacks">Atlassian -
 * Type Handler Callbacks</a>
 * 
 * @author Ken Weiner
 * @author Christian Schenk
 * @author Robert Jaeschke
 */
public class GroupLevelPermissionTypeHandlerCallback extends AbstractTypeHandlerCallback {

    @Override
    public Object valueOf(final String str) {
	    return GroupLevelPermission.getGroupLevelPermission(str);
    }

	/* (non-Javadoc)
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#setParameter(com.ibatis.sqlmap.client.extensions.ParameterSetter, java.lang.Object)
	 */
	@Override
	public void setParameter(ParameterSetter arg0, Object arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}