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