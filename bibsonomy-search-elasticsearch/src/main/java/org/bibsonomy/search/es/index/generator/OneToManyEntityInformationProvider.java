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

import java.util.Map;

/**
 * @author dzo
 * @param <T>
 * @param <M>
 */
public abstract class OneToManyEntityInformationProvider<T, M> extends EntityInformationProvider<T> {

	private EntityInformationProvider<M> toManyEntityInformationProvider;

	/**
	 * the entity information provider
	 *
	 * @param converter
	 * @param mappingBuilder
	 */
	protected OneToManyEntityInformationProvider(final Converter<T, Map<String, Object>, ?> converter, MappingBuilder<XContentBuilder> mappingBuilder, final EntityInformationProvider<M> toManyEntityInformationProvider) {
		super(converter, mappingBuilder);

		this.toManyEntityInformationProvider = toManyEntityInformationProvider;
	}

	/**
	 * returns the entity provider of the tomany entity
	 * @return
	 */
	public EntityInformationProvider<M> getToManyEntityInformationProvider() {
		return this.toManyEntityInformationProvider;
	}

}
