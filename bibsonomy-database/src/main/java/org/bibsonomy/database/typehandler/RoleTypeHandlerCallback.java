package org.bibsonomy.database.typehandler;

import java.sql.SQLException;

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
 * @version $Id$
 */
public class RoleTypeHandlerCallback extends AbstractTypeHandlerCallback {

	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(Role.DEFAULT.getRole());
		} else {
			final Role role = (Role) parameter;
			setter.setInt(role.getRole());
		}
	}

	public Object valueOf(final String str) {
		try {
			return Role.getRole(str);
		} catch (NumberFormatException ex) {
			return Role.DEFAULT;
		}
	}
}