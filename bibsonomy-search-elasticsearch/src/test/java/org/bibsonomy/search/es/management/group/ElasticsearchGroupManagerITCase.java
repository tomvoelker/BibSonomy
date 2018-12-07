package org.bibsonomy.search.es.management.group;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.querybuilder.GroupQueryBuilder;
import org.bibsonomy.search.es.search.group.AbstractGroupSearchTest;
import org.junit.Test;

/**
 * tests the group manager
 *
 * @author dzo
 */
public class ElasticsearchGroupManagerITCase extends AbstractGroupSearchTest {

	@Test
	public void testGenerate() {
		final GroupQueryBuilder groupQueryBuilder = new GroupQueryBuilder();
		groupQueryBuilder.setSearch("\"Test Group\"");
		final List<Group> groups = GROUP_SEARCH.getGroups(new User(), groupQueryBuilder.createGroupQuery());
		assertThat(groups.size(), is(4));
	}
}
