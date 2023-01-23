/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
package org.bibsonomy.search.es.index.generator;

import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * provides entity information
 * @param <E>
 */
public abstract class EntityInformationProvider<E> {

	private final Converter<E, Map<String, Object>, ?> converter;

	private final MappingBuilder<XContentBuilder> mappingBuilder;

	/**
	 * the entity information provider
	 * @param converter
	 * @param mappingBuilder
	 */
	protected EntityInformationProvider(Converter<E, Map<String, Object>, ?> converter, MappingBuilder<XContentBuilder> mappingBuilder) {
		this.converter = converter;
		this.mappingBuilder = mappingBuilder;
	}

	public abstract int getContentId(E e);

	public abstract String getEntityId(E entity);

	public abstract String getType();

	/**
	 * @return the converter
	 */
	public Converter<E, Map<String, Object>, ?> getConverter() {
		return this.converter;
	}

	public MappingBuilder<XContentBuilder> getMappingBuilder() {
		return this.mappingBuilder;
	}

	/**
	 * returns all public fields of this type
	 */
	public Set<String> getPublicFields() {
		return Collections.emptySet();
	}

	/**
	 * @return a set of all private fields that should only be queryied by the owner of the entity
	 */
	public Set<String> getPrivateFields() {
		return Collections.emptySet();
	}

	/**
	 * the routing information for this entity
	 * @param entity
	 * @return
	 */
	public String getRouting(E entity) {
		return null;
	}
}
