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
package org.bibsonomy.search.es.search.person;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.junit.Test;

import java.util.List;

/**
 * tests for {@link ElasticsearchPersonSearch}
 *
 * @author dzo
 */
public class ElasticsearchPersonSearchITCase extends AbstractPersonSearchTest {

	private static final ElasticsearchPersonSearch PERSON_SEARCH = EsSpringContextWrapper.getContext().getBean(ElasticsearchPersonSearch.class);

	private static final String PERSON_ID = "h.muller";

	/**
	 * tests {@link ElasticsearchPersonSearch#getPersons(PersonQuery)}
	 */
	@Test
	public void testGetPersonSuggestions() {
		assertPersonSuggestion("Schorsche");
		assertPersonSuggestion("schorsche");
		assertPersonSuggestion("schor");
	}

	/**
	 * test the index to also return persons with queries containing publication titles
	 */
	@Test
	public void testGetPersonSuggestionWithPublicationTitle() {
		assertPersonSuggestion("Schorsche Wurst");
	}

	private void assertPersonSuggestion(final String query) {
		final List<Person> personSuggestions = PERSON_SEARCH.getPersons(new PersonQuery(query));
		assertThat(personSuggestions.size(), is(1));

		final Person person = personSuggestions.get(0);
		assertThat(person.getPersonId(), is(PERSON_ID));

		// check for the resource relations
		final List<ResourcePersonRelation> resourceRelations = person.getResourceRelations();
		assertThat(resourceRelations.size(), is(1));
		assertThat(resourceRelations.get(0).getPost().getResource().getTitle(), is("Wurst aufs Brot"));
	}
}