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
package org.bibsonomy.search.es.index.generator.group;

import java.util.Map;

import org.bibsonomy.model.Group;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.index.mapping.group.GroupMappingBuilder;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * entity information provider
 *
 * @author dzo
 */
public class GroupEntityInformationProvider extends EntityInformationProvider<Group> {

	/**
	 * the entity information provider
	 *
	 * @param converter
	 * @param mappingBuilder
	 */
	protected GroupEntityInformationProvider(Converter<Group, Map<String, Object>, ?> converter, MappingBuilder<XContentBuilder> mappingBuilder) {
		super(converter, mappingBuilder);
	}

	@Override
	public int getContentId(Group group) {
		return group.getGroupId();
	}

	@Override
	public String getEntityId(Group entity) {
		return entity.getName();
	}

	@Override
	public String getType() {
		return GroupMappingBuilder.GROUP_DOCUMENT_TYPE;
	}
}
