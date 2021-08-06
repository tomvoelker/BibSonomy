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
package org.bibsonomy.search.index.update.person;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

/**
 * tests for the {@link PersonResourceRelationUpdateLogic}
 *
 * @author dzo
 */
public class PersonResourceRelationUpdateLogicTest extends AbstractDatabaseManagerTest {

	private static final PersonResourceRelationUpdateLogic UPDATE_LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean("personResourceRelationDBLogic", PersonResourceRelationUpdateLogic.class);

	private static final PersonDatabaseManager PERSON_DATABASE_MANAGER = testDatabaseContext.getBean(PersonDatabaseManager.class);

	@Test
	public void testGetDeletedEntitities() throws InterruptedException {
		final Date lastLogDate = new Date();
		Thread.sleep(1000);

		PERSON_DATABASE_MANAGER.removeResourceRelation("w.test.2", "eb0000af0a0c00b0b0ac0e0a0a00d0c0", 0, PersonResourceRelationType.AUTHOR, new User("testuser1"), this.dbSession);

		final List<ResourcePersonRelation> deletedEntities = UPDATE_LOGIC.getDeletedEntities(lastLogDate);
		assertThat(deletedEntities.size(), is(1));
	}
}