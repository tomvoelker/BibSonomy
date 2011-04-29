package org.bibsonomy.database.common.typehandler;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Types;
import java.util.TimeZone;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * An iBATIS type handler callback for {@link URL}s that are mapped to Strings
 * in the database. If a URL cannot be constructed based on the String, then the
 * URL will be set to <code>null</code>.<br/>
 * 
 * Almost copied from <a
 * href="http://opensource.atlassian.com/confluence/oss/display/IBATIS/Type+Handler+Callbacks">Atlassian -
 * Type Handler Callbacks</a>
 * 
 * @author Ken Weiner
 * @author Christian Schenk
 * @version $Id$
 */
public class TimezoneTypeHandlerCallback extends AbstractTypeHandlerCallback {

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setNull(Types.VARCHAR);
			setter.setString("");
		} else {
			final TimeZone timezone = (TimeZone) parameter;
			setter.setString(timezone.getID());
		}
	}

	@Override
	public Object valueOf(final String str) {
	    return TimeZone.getTimeZone(str);
	}
}