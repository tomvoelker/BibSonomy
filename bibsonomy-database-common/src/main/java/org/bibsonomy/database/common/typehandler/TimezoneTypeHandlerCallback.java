/**
 * BibSonomy-Database-Common - Helper classes for database interaction
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 * @author rja
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