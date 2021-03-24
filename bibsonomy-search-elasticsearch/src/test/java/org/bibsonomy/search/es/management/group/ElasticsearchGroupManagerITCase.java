package org.bibsonomy.search.es.management.group;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupRequest;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.GroupQuery;
import org.bibsonomy.search.es.search.group.AbstractGroupSearchTest;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.util.BasicUtils;
import org.junit.Test;

/**
 * tests the group manager
 *
 * @author dzo
 */
public class ElasticsearchGroupManagerITCase extends AbstractGroupSearchTest {

	private static final GroupDatabaseManager GROUP_DATABASE_MANAGER = testDatabaseContext.getBean(GroupDatabaseManager.class);
	private static final String REAL_NAME = "New Group ";

	@Test
	public void testGenerate() {
		final GroupQuery groupQuery = GroupQuery.builder().search("\"Test Group\"").build();
		final List<Group> groups = GROUP_SEARCH.getGroups(new User(), groupQuery);
		assertThat(groups.size(), is(4));

		final List<Group> childgroup = GROUP_SEARCH.getGroups(new User(), GroupQuery.builder().search("childgroup").build());
		assertThat(childgroup.size(), is(3));
	}

	@Test
	public void testInsertGroup() {
		final GroupQuery groupQuery = GroupQuery.builder().search("\"" + REAL_NAME + 0 + "\"").build();
		final User loggedinUser = new User();
		final List<Group> groupsBeforeInsert = GROUP_SEARCH.getGroups(loggedinUser, groupQuery);
		assertThat(groupsBeforeInsert.size(), is(0));

		createGroup(0);

		this.updateIndex();

		final List<Group> afterInsert = GROUP_SEARCH.getGroups(loggedinUser, groupQuery);
		assertThat(afterInsert.size(), is(1));
	}

	private void createGroup(final int i) {
		final Group group = new Group("newGroup" + i);
		group.setAllowJoin(false);
		group.setInternalId("Internal");
		group.setOrganization(true);

		group.setRealname(REAL_NAME + i);
		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setReason("");
		groupRequest.setUserName("testuser1");
		groupRequest.setSubmissionDate(new Date());
		group.setGroupRequest(groupRequest);
		GROUP_DATABASE_MANAGER.createPendingGroup(group, this.dbSession);
		GROUP_DATABASE_MANAGER.activateGroup(group.getName(), new User("testuser2"), this.dbSession);
	}

	@Test
	public void testMassInsertGroup() {
		final IntStream intStream = IntStream.rangeClosed(1, 1000);
		intStream.forEach(this::createGroup);

		this.updateIndex();

		IntStream.rangeClosed(1, 1000).forEach(i -> {
			final GroupQuery groupQuery = GroupQuery.builder().search("\"" + REAL_NAME + i + "\"").build();
			final User loggedinUser = new User();
			final List<Group> groupsBeforeInsert = GROUP_SEARCH.getGroups(loggedinUser, groupQuery);
			assertThat(groupsBeforeInsert.size(), is(1));
		});
	}

	@Test
	public void testIndexInfo() {
		final List<SearchIndexInfo> indexInformation = GROUP_SEARCH_MANAGER.getIndexInformations();
		assertThat(indexInformation.size(), is(2));

		final SearchIndexInfo searchIndexInfo = indexInformation.get(0);
		assertThat(searchIndexInfo.getSyncState().getMappingVersion(), is(BasicUtils.VERSION));
	}

	private void updateIndex() {
		// update both indices
		GROUP_SEARCH_MANAGER.updateIndex();
		GROUP_SEARCH_MANAGER.updateIndex();

		// wait some time for the index to update
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
