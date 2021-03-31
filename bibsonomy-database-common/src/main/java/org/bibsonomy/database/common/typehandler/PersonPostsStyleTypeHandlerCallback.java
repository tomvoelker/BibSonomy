/**
 * BibSonomy-Database-Common - Helper classes for database interaction
 *
 * Copyright (C) 2006 - 2021 Knowledge & Data Engineering Group,
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

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import org.bibsonomy.model.enums.PersonPostsStyle;

import java.sql.SQLException;

public class PersonPostsStyleTypeHandlerCallback extends AbstractTypeHandlerCallback {

    @Override
    public void setParameter(ParameterSetter parameterSetter, Object o) throws SQLException {
        if (o == null) {
            parameterSetter.setInt(PersonPostsStyle.GOLDSTANDARD.getValue());
        } else {
            final PersonPostsStyle personPostsStyle = (PersonPostsStyle) o;
            parameterSetter.setInt(personPostsStyle.getValue());
        }
    }

    @Override
    public Object valueOf(String s) {
        try {
            return PersonPostsStyle.valueOf(Integer.parseInt(s));
        } catch (NumberFormatException ex) {
            return PersonPostsStyle.GOLDSTANDARD;
        }
    }
}
