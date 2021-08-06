/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.logic.query.statistics.meta;

import java.util.Set;

import org.bibsonomy.util.object.FieldDescriptor;

/**
 * query to get the distinct values indicated from a specific field
 *
 * @author dzo
 * @param <T>
 * @param <E>
 */
public class DistinctFieldValuesQuery<T, E> implements MetaDataQuery<Set<E>> {

	private final FieldDescriptor<T, E> fieldDescriptor;
	private final Class<T> clazz;

	/**
	 * default constructor
	 * @param clazz
	 * @param fieldGetter
	 */
	public DistinctFieldValuesQuery(Class<T> clazz, FieldDescriptor<T, E> fieldGetter) {
		this.clazz = clazz;
		this.fieldDescriptor = fieldGetter;
	}

	/**
	 * @return the fieldDescriptor
	 */
	public FieldDescriptor<T, E> getFieldDescriptor() {
		return fieldDescriptor;
	}

	/**
	 * @return the clazz
	 */
	public Class<T> getClazz() {
		return clazz;
	}
}
