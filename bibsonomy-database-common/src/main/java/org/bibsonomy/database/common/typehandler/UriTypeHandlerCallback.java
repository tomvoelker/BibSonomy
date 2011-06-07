package org.bibsonomy.database.common.typehandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * An iBATIS type handler callback for {@link URI}s that are mapped to Strings
 * in the database. If a URI cannot be constructed based on the String, then the
 * URI will be set to <code>null</code>.<br/>
 * 
 * Almost copied from <a
 * href="http://opensource.atlassian.com/confluence/oss/display/IBATIS/Type+Handler+Callbacks">Atlassian -
 * Type Handler Callbacks</a>
 * 
 * @author Robert Jäschke
 * @version $Id$
 */
public class UriTypeHandlerCallback extends AbstractTypeHandlerCallback {
	private static final Log log = LogFactory.getLog(UriTypeHandlerCallback.class);

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setNull(Types.VARCHAR);
			setter.setString("");
		} else {
			final URI uri = (URI) parameter;
			setter.setString(uri.toString());
		}
	}

	@Override
	public Object valueOf(final String str) {
		try {
			return new URI(str);
		} catch (final URISyntaxException ex) {
			log.warn("'" + str + "' is not a valid URI");
			return null;
		}
	}
}