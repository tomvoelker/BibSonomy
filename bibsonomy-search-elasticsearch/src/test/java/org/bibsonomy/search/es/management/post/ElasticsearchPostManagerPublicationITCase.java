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
package org.bibsonomy.search.es.management.post;

import static org.bibsonomy.search.es.management.post.ElasticsearchCommunityPostPublicationManagerITCase.buildQuery;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.database.managers.AdminDatabaseManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.management.AbstractElasticsearchPostIndexTest;
import org.bibsonomy.search.es.search.post.ElasticsearchPublicationSearch;
import org.bibsonomy.services.searcher.query.PostSearchQuery;
import org.junit.Test;

/**
 * tests for {@link ElasticsearchPostManager}
 *
 * @author dzo
 */
public class ElasticsearchPostManagerPublicationITCase extends AbstractElasticsearchPostIndexTest {
	
	private static final AdminDatabaseManager adminDatabaseManager = testDatabaseContext.getBean(AdminDatabaseManager.class);
	private static ElasticsearchPostManager<BibTex> publicationManager = EsSpringContextWrapper.getContext().getBean("elasticsearchPublicationManager", ElasticsearchPostManager.class);
	private static ElasticsearchPublicationSearch<BibTex> publicationSearch = EsSpringContextWrapper.getContext().getBean("elasticsearchPublicationSearch", ElasticsearchPublicationSearch.class);

	/**
	 * tests {@link ElasticsearchPostManager#updateIndex()}
	 */
	@Test
	public void testUpdateIndexWithSpammer() {
		final String userToFlag = "testuser3";
		final ResultList<Post<BibTex>> postsBefore = publicationSearch.getPosts(userToFlag, userToFlag, null, null, Collections.<String>emptyList(), null, "test", null, null, null, null, null, null, null, null, SortKey.DATE, 10, 0, null);
		assertEquals(1, postsBefore.size());

		final User user = new User(userToFlag);
		final PostSearchQuery<?> query = buildQuery("test");
		query.setGrouping(GroupingEntity.USER);
		query.setGroupingName(userToFlag);
		final ResultList<Post<BibTex>> postsBefore = publicationSearch.getPosts(user, query);
		assertEquals(1, postsBefore.size());

		user.setSpammer(Boolean.TRUE);
		user.setAlgorithm("unittest");
		adminDatabaseManager.flagSpammer(user, "admin", this.dbSession);
		publicationManager.updateIndex();
		
		final ResultList<Post<BibTex>> posts = publicationSearch.getPosts(user, query);
		assertEquals(0, posts.size());
		
		user.setSpammer(Boolean.FALSE);
		user.setAlgorithm("admin");
		user.setPrediction(null); // FIXME: side effects :(
		adminDatabaseManager.flagSpammer(user, "admin", this.dbSession);
		
		publicationManager.updateIndex();
		
		final ResultList<Post<BibTex>> readded = publicationSearch.getPosts(user, query);
		assertEquals(postsBefore.size(), readded.size());
	}

	@Override
	protected ElasticsearchPostManager getManager() {
		return publicationManager;
	}
}
