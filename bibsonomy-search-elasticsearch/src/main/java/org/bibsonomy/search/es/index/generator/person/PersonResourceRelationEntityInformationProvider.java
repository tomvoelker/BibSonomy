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
package org.bibsonomy.search.es.index.generator.person;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.index.mapping.person.PersonMappingBuilder;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Map;

/**
 * person resource relation
 *
 * @author dzo
 */
public class PersonResourceRelationEntityInformationProvider extends EntityInformationProvider<ResourcePersonRelation> {

	/**
	 * default constructor
	 *
	 * @param converter the converter to use
	 */
	public PersonResourceRelationEntityInformationProvider(final Converter<ResourcePersonRelation, Map<String, Object>, ?> converter) {
		// the mapping is already created by the parent entity relation
		super(converter, null);
	}

	@Override
	public MappingBuilder<XContentBuilder> getMappingBuilder() {
		throw new UnsupportedOperationException("no mapping for this to-many entity");
	}

	@Override
	public int getContentId(ResourcePersonRelation resourcePersonRelation) {
		return resourcePersonRelation.getPersonRelChangeId();
	}

	@Override
	public String getEntityId(final ResourcePersonRelation entity) {
		return String.valueOf(entity.getPersonRelChangeId());
	}

	/**
	 * since elasticsearch 6.4 there is only one type supported but still supports one to many joins
	 * @return
	 */
	@Override
	public String getType() {
		return PersonMappingBuilder.PERSON_DOCUMENT_TYPE;
	}

	/**
	 * sets the routing to the id of the person
	 * @param entity
	 * @return
	 */
	@Override
	public String getRouting(final ResourcePersonRelation entity) {
		return entity.getPerson().getPersonId();
	}
}
