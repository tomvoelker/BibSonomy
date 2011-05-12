package org.bibsonomy.database.common.typehandler;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * An iBATIS type handler callback for {@link Properties} that are mapped to
 * Strings in the database.
 * 
 * @author Robert Jaeschke
 * @author wla
 */
public class PropertiesTypeHandlerCallback extends AbstractTypeHandlerCallback {

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setNull(Types.CHAR);
		} else {
			final Properties properties = (Properties) parameter;
			final StringWriter sw = new StringWriter();
			try {
			    properties.store(sw, null);
			} catch (IOException e) {
			    /* TODO Auto-generated catch block
			    e.printStackTrace();*/
			}
			setter.setString(sw.getBuffer().toString());
		}
	}

	@Override
	public Object valueOf(final String str) {
		StringReader reader = new StringReader(str);
		final Properties properties = new Properties();
		try {
		    properties.load(reader);
		} catch (IOException e) {
		    return new Properties();
		}
		return properties;
	}
}