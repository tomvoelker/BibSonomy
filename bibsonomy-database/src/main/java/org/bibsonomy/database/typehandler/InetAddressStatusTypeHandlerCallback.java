package org.bibsonomy.database.typehandler;

import java.sql.SQLException;

import org.bibsonomy.common.enums.InetAddressStatus;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * An iBATIS type handler callback for {@link InetAddressStatus}es that are
 * mapped to Strings in the database. If an InetAddressStatus cannot be
 * constructed based on the String, then the InetAddressStatus will be set to
 * <code>UNKNOWN</code>.<br/>
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
public class InetAddressStatusTypeHandlerCallback implements TypeHandlerCallback {

	public Object getResult(final ResultGetter getter) throws SQLException {
		final String value = getter.getString();
		if (getter.wasNull()) {
			return null;
		}
		return this.valueOf(value);
	}

	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(InetAddressStatus.UNKNOWN.getInetAddressStatus());
		} else {
			final InetAddressStatus inetAddressStatus = (InetAddressStatus) parameter;
			setter.setInt(inetAddressStatus.getInetAddressStatus());
		}
	}

	public Object valueOf(final String str) {
		try {
			return InetAddressStatus.getInetAddressStatus(str);
		} catch (NumberFormatException ex) {
			return InetAddressStatus.UNKNOWN;
		}
	}
}