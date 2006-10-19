package org.bibsonomy.ibatis.typehandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Types;

import org.bibsonomy.DefaultValues;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * An iBATIS type handler callback for java.net.URLs that are mapped to Strings in the database. If
 * a URL cannot be constructed based on the String, then the URL will be set to <code>null</code>.<br/>
 * <br/>
 * Almost copied from http://opensource.atlassian.com/confluence/oss/display/IBATIS/Type+Handler+Callbacks
 * 
 * @author Ken Weiner
 */
public class UrlTypeHandlerCallback implements TypeHandlerCallback {

	public Object getResult(final ResultGetter getter) throws SQLException {
		final String value = getter.getString();
		if (getter.wasNull()) {
			return null;
		}
		return this.valueOf(value);
	}

	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setNull(Types.VARCHAR);
		} else {
			final URL url = (URL) parameter;
			setter.setString(url.toExternalForm());
		}
	}

	public Object valueOf(final String str) {
		URL url;
		try {
			url = new URL(str);
		} catch (final MalformedURLException ex) {
			url = DefaultValues.getInstance().getBibsonomyURL();
		}
		return url;
	}
}