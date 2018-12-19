package org.bibsonomy.search.es.management.group;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

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

	@Test
	public void testGenerate() {
		final GroupQuery groupQuery = GroupQuery.builder().search("\"Test Group\"").build();
		final List<Group> groups = GROUP_SEARCH.getGroups(new User(), groupQuery);
		assertThat(groups.size(), is(4));
	}

	@Test
	public void testInsertGroup() {
		final Group group = new Group("newGroup");
		final String realName = "New Group";
		group.setRealname(realName);
		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setReason("");
		groupRequest.setUserName("testuser1");
		groupRequest.setSubmissionDate(new Date());
		group.setGroupRequest(groupRequest);

		final GroupQuery groupQuery = GroupQuery.builder().search("\"" + realName + "\"").build();
		final User loggedinUser = new User();
		final List<Group> groupsBeforeInsert = GROUP_SEARCH.getGroups(loggedinUser, groupQuery);
		assertThat(groupsBeforeInsert.size(), is(0));

		GROUP_DATABASE_MANAGER.createPendingGroup(group, this.dbSession);
		GROUP_DATABASE_MANAGER.activateGroup(group.getName(), new User("testuser2"), this.dbSession);

		this.updateIndex();

		final List<Group> afterInsert = GROUP_SEARCH.getGroups(loggedinUser, groupQuery);
		assertThat(afterInsert.size(), is(1));
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
