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
package org.bibsonomy.search.index.update.group;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.search.index.update.GeneralIndexUpdateLogic;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

/**
 * tests for group index update logic test
 *
 * @author dzo
 */
public class GroupIndexUpdateLogicTest extends AbstractDatabaseManagerTest {

	private static final GeneralIndexUpdateLogic<Group> INDEX_UPDATE_LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean("groupSearchUpdateLogic", GeneralIndexUpdateLogic.class);

	private static final GroupDatabaseManager GROUP_DATABASE_MANAGER = testDatabaseContext.getBean(GroupDatabaseManager.class);

	@Test
	public void testGetNewerEntities() {
		final List<Group> newerEntities = INDEX_UPDATE_LOGIC.getNewerEntities(12, new Date(), 10, 0);
		assertThat(newerEntities.size(), is(1));

		final List<Group> newGroups = INDEX_UPDATE_LOGIC.getNewerEntities(3, new Date(), 10, 0);
		assertThat(newGroups.size(), is(8));
	}

	@Test
	public void testGetDeletedEntities() {
		final Date lastLogDate = new Date();

		GROUP_DATABASE_MANAGER.deleteGroup("testgroup1", true, new User("testuser1"), this.dbSession);

		final List<Group> deletedEntities = INDEX_UPDATE_LOGIC.getDeletedEntities(lastLogDate);
		assertThat(deletedEntities.size(), is(1));
	}
}
