/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import java.util.List;

import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.search.es.management.AbstractEsIndexTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * tests for the {@link ElasticsearchPublicationSearch}
 *
 * @author jensi
 */
public class ElasticsearchPublicationSearchTest extends AbstractEsIndexTest {
	
	/**
	 * TODO: rename method
	 */
	@Test
	public void testSomething() {
		List<ResourcePersonRelation> res;
		PersonSuggestionQueryBuilder options = new PersonSuggestionQueryBuilder("Schorsche") {
			@Override
			public List<ResourcePersonRelation> doIt() {
				return null;
			}
		}.withEntityPersons(true).withRelationType(PersonResourceRelationType.values());
		res = PersonDatabaseManager.getInstance().getPersonSuggestion(options);
		Assert.assertTrue(res.size() > 0);
		Assert.assertEquals(res.get(0).getRelationType(), PersonResourceRelationType.AUTHOR);
		Assert.assertEquals(res.get(0).getPersonIndex(), 0);
		Assert.assertEquals(res.get(0).getPerson().getPersonId(), "h.muller");
		Assert.assertEquals(res.get(0).getPerson().getMainName(), new PersonName("Henner", "Schorsche"));
		Assert.assertEquals(res.get(0).getPost().getResource().getTitle(), "Wurst aufs Brot");
	}
}
