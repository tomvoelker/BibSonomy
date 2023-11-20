/**
 * BibSonomy-Database-Common - Helper classes for database interaction
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import static org.bibsonomy.util.ValidationUtils.present;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import org.bibsonomy.model.cris.GroupPersonLinkType;
import org.bibsonomy.model.cris.ProjectPersonLinkType;
import org.bibsonomy.util.collection.BiHashMap;
import org.bibsonomy.util.collection.BiMap;

import java.sql.SQLException;

/**
 * stores the class in the database for the {@link CRISLinkTypeTypeHandlerCallback}
 *
 * @author dzo
 */
public class CRISLinkTypeClassTypeHandlerCallback extends AbstractTypeHandlerCallback {

	protected static final BiMap<Class<?>, Integer> LINK_TYPE_CLASS_ID_MAP = new BiHashMap<>();

	static {
		LINK_TYPE_CLASS_ID_MAP.put(ProjectPersonLinkType.class, Integer.valueOf(1));
		LINK_TYPE_CLASS_ID_MAP.put(GroupPersonLinkType.class, Integer.valueOf(2));
	}

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (present(parameter)) {
			final Class<?> parameterClass = parameter.getClass();
			setter.setInt(LINK_TYPE_CLASS_ID_MAP.get(parameterClass).intValue());
		}
	}

	@Override
	public Object valueOf(String s) {
		throw new UnsupportedOperationException();
	}
}
