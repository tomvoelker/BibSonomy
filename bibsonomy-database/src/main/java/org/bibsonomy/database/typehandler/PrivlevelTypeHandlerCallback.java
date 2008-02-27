package org.bibsonomy.database.typehandler;

import java.sql.SQLException;

import org.bibsonomy.common.enums.Privlevel;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * An iBATIS type handler callback for {@link Privlevel}es that are mapped to Strings
 * in the database. If the {@link Privlevel} cannot be constructed based on the String, then the
 * Privlevel will be set to <code>MEMBERS</code>.<br/>
 * 
 * Almost copied from <a
 * href="http://opensource.atlassian.com/confluence/oss/display/IBATIS/Type+Handler+Callbacks">Atlassian -
 * Type Handler Callbacks</a>
 * 
 * @author Ken Weiner
 * @author Christian Schenk
 * @author rja
 * @version $Id$
 */
public class PrivlevelTypeHandlerCallback implements TypeHandlerCallback {
	
	public Object getResult(final ResultGetter getter) throws SQLException {
		final String value = getter.getString();
		if (getter.wasNull()) {
			return null;
		}
		return this.valueOf(value);
	}

	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(Privlevel.MEMBERS.getPrivlevel());
		} else {
			System.out.println("############################# parameter = " + parameter);
			final Privlevel privlevel = (Privlevel) parameter;
			setter.setInt(privlevel.getPrivlevel());
		}
	}

	public Object valueOf(final String str) {
		try {
			return Privlevel.getPrivlevel(Integer.parseInt(str));
		} catch (NumberFormatException ex) {
			return Privlevel.MEMBERS;
		}
	}
}