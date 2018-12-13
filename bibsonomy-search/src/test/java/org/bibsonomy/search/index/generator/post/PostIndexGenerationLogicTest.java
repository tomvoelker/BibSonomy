/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search.index.generator.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

import java.util.List;

/**
 * tests for {@link PostIndexGenerationLogic}
 *
 * @author dzo
 */
public class PostIndexGenerationLogicTest extends AbstractDatabaseManagerTest {

	private static final PostIndexGenerationLogic<BibTex> INDEX_GENERATION_LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean("publicationGenerationDBLogic", PostIndexGenerationLogic.class);

	/**
	 * tests {@link PostIndexGenerationLogic#getEntities(int, int)}
	 * @throws Exception
	 */
	@Test
	public void testGetPostEntries() {
		final List<Post<BibTex>> posts = INDEX_GENERATION_LOGIC.getEntities(0, 100);
		assertThat(posts.size(), is(22));
		// check for documents
		for (final Post<BibTex> searchPost : posts) {
			final BibTex publication = searchPost.getResource();
			if ("b77ddd8087ad8856d77c740c8dc2864a".equals(publication.getIntraHash()) && "testuser1".equals(searchPost.getUser().getName())) {
				assertEquals(2, publication.getDocuments().size());
			}
		}
	}
}