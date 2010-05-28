package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;

import org.bibsonomy.common.enums.ProfilePrivlevel;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * @author dzo
 * @version $Id$
 */
public class ProfilePrivlevelTypeHandlerCallback extends AbstractTypeHandlerCallback {
	private static final ProfilePrivlevel DEFAULT_PROFILE_PRIVLEVEL = ProfilePrivlevel.PRIVATE;

	@Override
	public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(DEFAULT_PROFILE_PRIVLEVEL.getProfilePrivlevel());
		} else {
			final ProfilePrivlevel profilePrivlevel = (ProfilePrivlevel) parameter;
			setter.setInt(profilePrivlevel.getProfilePrivlevel());
		}
	}

	@Override
	public Object valueOf(final String str) {
		try {
			return ProfilePrivlevel.getProfilePrivlevel(Integer.parseInt(str));
		} catch (NumberFormatException ex) {
			return DEFAULT_PROFILE_PRIVLEVEL;
		}
	}

}
