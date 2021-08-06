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
package org.bibsonomy.search.index.update.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * abstract test class for {@link CommunityPostIndexUpdateLogic}
 * @param <R>
 */
public abstract class CommunityPostIndexUpdateLogicTest<R extends Resource> extends AbstractDatabaseManagerTest {

	protected abstract CommunityPostIndexUpdateLogic<R> getUpdateLogic();

	/**
	 * tests {@link CommunityPostIndexCommunityUpdateLogic#getNewerEntities(long, Date, int, int)}
	 */
	@Test
	public void testGetNewEntities() {
		final List<Post<R>> newCommunityPosts = this.getUpdateLogic().getNewerEntities(this.getLastEntityIdForNewEntities(), new Date(), 1, 0);
		assertThat(newCommunityPosts.size(), is(1));
		this.testNewEntities(newCommunityPosts);
	}

	protected abstract void testNewEntities(List<Post<R>> newCommunityPosts);

	protected abstract long getLastEntityIdForNewEntities();

	/**
	 * tests {@link CommunityPostIndexCommunityUpdateLogic#getDeletedEntities(Date)}
	 */
	@Test
	public void testGetDeletedEntities() {
		final Date lastLogDate = new Date();
		final List<Post<R>> deletedEntities = this.getUpdateLogic().getDeletedEntities(lastLogDate);
		assertThat(deletedEntities.size(), is(0));
	}

}
