package org.bibsonomy.database.typehandler;

import java.sql.SQLException;

import org.bibsonomy.common.enums.Privlevel;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * An iBATIS type handler callback for {@link Privlevel}es that are mapped to
 * Strings in the database. If the {@link Privlevel} cannot be constructed based
 * on the String, then the Privlevel will be set to <code>MEMBERS</code>.<br/>
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
public class PrivlevelTypeHandlerCallback extends AbstractTypeHandlerCallback {

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(Privlevel.MEMBERS.getPrivlevel());
		} else {
			final Privlevel privlevel = (Privlevel) parameter;
			setter.setInt(privlevel.getPrivlevel());
		}
	}

	@Override
	public Object valueOf(final String str) {
		try {
			return Privlevel.getPrivlevel(Integer.parseInt(str));
		} catch (NumberFormatException ex) {
			return Privlevel.MEMBERS;
		}
	}
}