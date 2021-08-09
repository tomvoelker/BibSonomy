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
import org.bibsonomy.search.es.index.converter.person.PersonConverter;
import org.bibsonomy.search.es.index.generator.OneToManyEntityInformationProvider;
import org.bibsonomy.search.es.index.mapping.person.PersonMappingBuilder;
import org.bibsonomy.model.Person;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * implementation of the {@link EntityInformationProvider} interface for {@link Person}s
 *
 * @author dzo
 */
public class PersonEntityInformationProvider extends OneToManyEntityInformationProvider<Person, ResourcePersonRelation> {

	/**
	 * the entity information provider
	 */
	public PersonEntityInformationProvider(final MappingBuilder<XContentBuilder> mappingBuilder, final EntityInformationProvider<ResourcePersonRelation> toManyEntityProvider) {
		super(new PersonConverter(), mappingBuilder, toManyEntityProvider);
	}

	@Override
	public int getContentId(final Person person) {
		return person.getPersonChangeId();
	}

	@Override
	public String getEntityId(final Person entity) {
		return entity.getPersonId();
	}

	@Override
	public String getType() {
		return PersonMappingBuilder.PERSON_DOCUMENT_TYPE;
	}

	@Override
	public String getRouting(final Person entity) {
		return entity.getPersonId();
	}
}
