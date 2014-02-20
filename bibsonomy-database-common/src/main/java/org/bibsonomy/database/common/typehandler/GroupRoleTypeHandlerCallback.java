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
