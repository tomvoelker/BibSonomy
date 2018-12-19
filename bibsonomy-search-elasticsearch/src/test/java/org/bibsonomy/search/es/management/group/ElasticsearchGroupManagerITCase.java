package org.bibsonomy.search.es.management.group;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.GroupQuery;
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
		final GroupQuery groupQuery = GroupQuery.builder().search("\"Test Group\"").build();
		final List<Group> groups = GROUP_SEARCH.getGroups(new User(), groupQuery);
		assertThat(groups.size(), is(4));
	}
}
