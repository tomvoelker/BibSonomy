/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.model.statistics.Statistics;
import org.junit.Test;

/**
 * tests for the {@link PersonResourceRelationDatabaseManager}
 *
 * @author ada
 */
public class PersonResourceRelationDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static final PersonResourceRelationDatabaseManager PERESON_RESOURCE_RELATION_DATABASE_MANAGER = testDatabaseContext.getBean(PersonResourceRelationDatabaseManager.class);

	@Test
	public void testGetStatistics() {
		ResourcePersonRelationQuery query = new ResourcePersonRelationQueryBuilder()
						.byPersonId("w.test.1").build();
		Statistics statistics = PERESON_RESOURCE_RELATION_DATABASE_MANAGER.getStatistics(query, new User(), this.dbSession);

		assertThat(statistics.getCount(), equalTo(3));

		// statistics should ignore limit and offset
		query = new ResourcePersonRelationQueryBuilder()
						.byPersonId("w.test.1")
						.start(1)
						.end(2)
						.build();
		statistics = PERESON_RESOURCE_RELATION_DATABASE_MANAGER.getStatistics(query, new User(), this.dbSession);

		assertThat(statistics.getCount(), equalTo(3));
	}
}