package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;
import java.sql.Types;
import java.util.TimeZone;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * Converts Java TimeZone objects to some suitable SQL datatype.
 *
 * FIXME: which SQL datatype should we use?
 *
 * 
 * FIXME: it would be nice to store a rather short string in the database (e.g.,
 * GMT+08:00) - how to normalize a timezone given by the user that has "Europe/Berlin"
 * or "PST" as ID?
 * 
 * See also: http://stackoverflow.com/questions/240510/convert-a-string-to-gregoriancalendar
 *  
 *  
 * 
 * @author rja
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