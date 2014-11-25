/**
 * BibSonomy-OpenSocial - Implementation of the Opensocial specification and OAuth Security Handling
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.opensocial.oauth.database.typehandler;

import java.sql.SQLException;

import org.apache.shindig.social.opensocial.oauth.OAuthEntry.Type;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class TokenTypeHandler implements TypeHandlerCallback{

	public Object getResult(ResultGetter arg) throws SQLException {
		return valueOf(arg.getString());
	}

	public void setParameter(ParameterSetter param, Object value) throws SQLException {
		Type tokenType = (Type) value;
		if( value!=null ) {
			param.setInt(tokenType.ordinal());
		}
	}

	public Object valueOf(String arg) {
		if ("0".equals(arg)) {
			return Type.REQUEST;
		} else if ("1".equals(arg)) {
			return Type.ACCESS;
		} else if ("2".equals(arg)) {
			return Type.DISABLED;
		} else {
			throw new RuntimeException("Given token type ('"+arg+"') not supported.");
		}
	}

}
