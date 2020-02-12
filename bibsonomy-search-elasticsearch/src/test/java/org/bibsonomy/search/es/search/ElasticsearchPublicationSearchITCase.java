/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.search.es.search;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.management.AbstractEsIndexTest;
import org.bibsonomy.search.es.search.post.ElasticsearchPublicationSearch;
import org.bibsonomy.search.es.search.post.EsResourceSearch;
import org.junit.Test;

/**
 * tests for the {@link ElasticsearchPublicationSearch}
 *
 * @author jensi
 */
public class ElasticsearchPublicationSearchITCase extends AbstractEsIndexTest {

	private static final EsResourceSearch<BibTex> PUBLICATION_SEARCH = EsSpringContextWrapper.getContext().getBean("elasticsearchPublicationSearch", EsResourceSearch.class);
	
	/**
	 * tests person suggestion
	 */
	@Test
	public void testPersonSuggestion() {
		PersonSuggestionQueryBuilder options = new PersonSuggestionQueryBuilder("Schorsche") {
			@Override
			public List<ResourcePersonRelation> doIt() {
				return null;
			}
		}.withEntityPersons(true).withRelationType(PersonResourceRelationType.values());
		
		final List<ResourcePersonRelation> res = PersonDatabaseManager.getInstance().getPersonSuggestion(options);

		assertThat(res.size(), greaterThan(0));

		final ResourcePersonRelation firstRelation = res.get(0);
		assertThat(firstRelation.getRelationType(), is(PersonResourceRelationType.AUTHOR));
		assertThat(firstRelation.getPersonIndex(), is(0));
		final Person person = firstRelation.getPerson();
		assertThat(person.getPersonId(), is("h.muller"));
		assertThat(person.getMainName(), is(new PersonName("Henner", "Schorsche")));
		assertThat(firstRelation.getPost().getResource().getTitle(), is("Wurst aufs Brot"));
	}

	@Test
	public void testCaseInsensitiveTagFiltering() {
		final String tag = "TEST";
		final ResultList<Post<BibTex>> postsUpperCase = PUBLICATION_SEARCH.getPosts(null, null, null, null, null, null, null, null, null, null, Collections.singletonList(tag), null, null, null, null, SortKey.NONE, 10, 0);

		final ResultList<Post<BibTex>> postsLowerCase = PUBLICATION_SEARCH.getPosts(null, null, null, null, null, null, null, null, null, null, Collections.singletonList(tag.toLowerCase()), null, null, null, null, SortKey.NONE, 10, 0);

		assertThat(postsUpperCase.getTotalCount(), is(postsLowerCase.getTotalCount()));
	}
}
