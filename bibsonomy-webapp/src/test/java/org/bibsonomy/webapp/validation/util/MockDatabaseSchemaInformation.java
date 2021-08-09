/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.validation.util;

import org.bibsonomy.services.database.DatabaseSchemaInformation;

/**
 * a mock {@link DatabaseSchemaInformation} implementation
 * returns 4 * property.length()
 * 
 * @author dzo
 */
public class MockDatabaseSchemaInformation implements DatabaseSchemaInformation {

	@Override
	public int getMaxColumnLengthForProperty(Class<?> resourceClass, String property) {
		if (property == null) {
			return -1;
		}
		return property.length() * 4;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.database.DatabaseSchemaInformation#callTypeHandler(java.lang.Class, java.lang.String, java.lang.Object, java.lang.Class)
	 */
	@Override
	public <T> T callTypeHandler(Class<?> resourceClass, String property, Object type, Class<T> resultType) {
		return null;
	}

}
