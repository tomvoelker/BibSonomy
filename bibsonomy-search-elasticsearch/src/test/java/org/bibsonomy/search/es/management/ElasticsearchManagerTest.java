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
package org.bibsonomy.search.es.management;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.bibsonomy.database.managers.AdminDatabaseManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.search.ElasticsearchPublicationSearch;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * tests for {@link ElasticsearchManager}
 *
 * @author dzo
 */
public class ElasticsearchManagerTest extends AbstractEsIndexTest {
	
	private static final AdminDatabaseManager adminDatabaseManager = AdminDatabaseManager.getInstance();
	private static ElasticsearchManager<BibTex> publicationManager;
	private static ElasticsearchPublicationSearch<BibTex> publicationSearch;
	
	/**
	 * inits the manager
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static final void initManager() {
		publicationManager = EsSpringContextWrapper.getContext().getBean("elasticsearchPublicationManager", ElasticsearchManager.class);
		publicationSearch = EsSpringContextWrapper.getContext().getBean("elasticsearchPublicationSearch", ElasticsearchPublicationSearch.class);
	}
	
	/**
	 * tests {@link ElasticsearchManager#updateIndex()}
	 */
	@Test
	public void testUpdateIndexWithSpammer() {
		final String userToFlag = "testuser3";
		final ResultList<Post<BibTex>> postsBefore = publicationSearch.getPosts(userToFlag, userToFlag, null, null, Collections.<String>emptyList(), null, "test", null, null, null, null, null, null, null, null, Order.ADDED, 10, 0);
		assertEquals(1, postsBefore.size());
		
		final User user = new User(userToFlag);
		user.setSpammer(Boolean.TRUE);
		user.setAlgorithm("unittest");
		adminDatabaseManager.flagSpammer(user, "admin", this.dbSession);
		publicationManager.updateIndex();
		
		final ResultList<Post<BibTex>> posts = publicationSearch.getPosts(userToFlag, userToFlag, null, null, Collections.<String>emptyList(), null, null, null, null, null, null, null, null, null, null, Order.ADDED, 10, 0);
		assertEquals(0, posts.size());
		
		user.setSpammer(Boolean.FALSE);
		user.setAlgorithm("admin");
		user.setPrediction(null); // FIXME: side effects :(
		adminDatabaseManager.flagSpammer(user, "admin", this.dbSession);
		
		publicationManager.updateIndex();
		
		final ResultList<Post<BibTex>> readded = publicationSearch.getPosts(userToFlag, userToFlag, null, null, Collections.<String>emptyList(), null, null, null, null, null, null, null, null, null, null, Order.ADDED, 10, 0);
		assertEquals(postsBefore.size(), readded.size());
	}
}
