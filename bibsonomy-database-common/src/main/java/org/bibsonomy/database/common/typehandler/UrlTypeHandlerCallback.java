package org.bibsonomy.database.common.typehandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class UrlTypeHandlerCallback extends AbstractTypeHandlerCallback {
	private static final Log log = LogFactory.getLog(UrlTypeHandlerCallback.class);

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setNull(Types.VARCHAR);
			setter.setString("");
		} else {
			final URL url = (URL) parameter;
			setter.setString(url.toExternalForm());
		}
	}

	@Override
	public Object valueOf(final String str) {
		try {
			return new URL(str);
		} catch (final MalformedURLException ex) {
			log.warn("'" + str + "' is not a valid URL");
			return null;
		}
	}
}