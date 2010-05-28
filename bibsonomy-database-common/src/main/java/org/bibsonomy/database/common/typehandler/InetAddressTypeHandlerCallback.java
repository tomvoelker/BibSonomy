package org.bibsonomy.database.common.typehandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * An iBATIS type handler callback for {@link InetAddress}es that are mapped to
 * Strings in the database. If an InetAddress cannot be constructed based on the
 * String, then the InetAddress will be set to <code>null</code>.<br/>
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
public class InetAddressTypeHandlerCallback extends AbstractTypeHandlerCallback {
	private static final Log log = LogFactory.getLog(InetAddressTypeHandlerCallback.class);

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setNull(Types.CHAR);
		} else {
			final InetAddress inetAddress = (InetAddress) parameter;
			setter.setString(inetAddress.getHostAddress());
		}
	}

	@Override
	public Object valueOf(final String str) {
		try {
			return InetAddress.getByName(str);
		} catch (UnknownHostException ex) {
			log.warn("'" + str + "' is not a valid InetAddress.");
			return null;
		}
	}
}