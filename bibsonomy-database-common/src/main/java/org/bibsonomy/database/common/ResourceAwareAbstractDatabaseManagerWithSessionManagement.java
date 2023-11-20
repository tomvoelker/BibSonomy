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
package org.bibsonomy.database.common;

import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;

/**
 * this class provides methods for resource database managers
 * @param <R>
 *
 * @author dzo
 */
public abstract class ResourceAwareAbstractDatabaseManagerWithSessionManagement<R extends Resource> extends AbstractDatabaseManagerWithSessionManagement {

	private final Class<R> resourceClass;
	private final boolean useSuperiorResourceClass;

	/**
	 * default constructor
	 * @param resourceClass the resource class
	 */
	public ResourceAwareAbstractDatabaseManagerWithSessionManagement(Class<R> resourceClass) {
		this(resourceClass, false);
	}

	/**
	 * default constructor
	 * @param resourceClass
	 * @param useSuperiorResourceClass
	 */
	public ResourceAwareAbstractDatabaseManagerWithSessionManagement(Class<R> resourceClass, boolean useSuperiorResourceClass) {
		this.resourceClass = resourceClass;
		this.useSuperiorResourceClass = useSuperiorResourceClass;
	}

	/**
	 * @return the resource name for the query
	 */
	protected String getResourceName() {
		if (this.useSuperiorResourceClass) {
			final Class<? extends Resource> superiorResourceClass = ResourceFactory.findSuperiorResourceClass(this.resourceClass);
			return superiorResourceClass.getSimpleName();
		}

		return this.resourceClass.getSimpleName();
	}

	/**
	 * @return the {@link ConstantID} for the resource class
	 */
	protected ConstantID getConstantID() {
		return ConstantID.getContentTypeByClass(this.resourceClass);
	}
}
