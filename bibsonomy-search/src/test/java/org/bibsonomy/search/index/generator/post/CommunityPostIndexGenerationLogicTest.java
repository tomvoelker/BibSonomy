/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search.index.generator.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.junit.Test;

import java.util.List;

/**
 * abstract test
 *
 * @author dzo
 */
public abstract class CommunityPostIndexGenerationLogicTest<R extends Resource> extends AbstractDatabaseManagerTest {

	/**
	 * finds the first post with the specified interhash in a list of posts
	 * @param entities
	 * @param hash
	 * @param <R>
	 * @return
	 */
	protected static <R extends Resource> Post<R> getFirstPostByInterHash(List<Post<R>> entities, String hash) {
		return entities.stream().filter(post -> post.getResource().getInterHash().equals(hash)).findFirst().get();
	}

	/**
	 * @return the resource logic to test
	 */
	protected abstract CommunityPostIndexGenerationLogic<R> getLogic();

	@Test
	public void testGetNumberOfEntities() {
		final int numberOfEntities = this.getLogic().getNumberOfEntities();
		assertThat(numberOfEntities, is(this.getNumberOfEntites()));
	}

	/**
	 * @return the number of entities in the database
	 */
	protected abstract int getNumberOfEntites();

	@Test
	public void testGetEntities() {
		final List<Post<R>> entities = this.getLogic().getEntities(0, this.getNumberOfEntites());
		assertThat(entities.size(), is(this.getNumberOfEntites()));
		this.testEntities(entities);
	}

	protected abstract void testEntities(List<Post<R>> entities);
}
