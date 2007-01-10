package org.bibsonomy.ibatis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.junit.Test;

/**
 * General tests.
 * 
 * @author mgr
 * @author Christian Schenk
 */
public class GeneralTest extends AbstractSqlMapTest {

	@Test
	public void isFriendOf() {
		this.bookmarkParam.setUserName("stumme");
		this.bookmarkParam.setRequestedUserName("grahl");
		assertTrue(this.db.getGeneral().isFriendOf(this.bookmarkParam));

		// with no users set no exception should be thrown and the result should
		// just be "false"
		this.bookmarkParam.setUserName(null);
		this.bookmarkParam.setRequestedUserName(null);
		assertFalse(this.db.getGeneral().isFriendOf(this.bookmarkParam));
	}

	@Test
	public void getGroupsForUser() {
		this.db.getGeneral().getGroupsForUser(this.bookmarkParam);
		this.bookmarkParam.setUserName(null);
		assertTrue(this.db.getGeneral().getGroupsForUser(this.bookmarkParam).size() == 0);
	}

	@Test
	public void getGroupIdByGroupName() {
		// group exists
		this.bookmarkParam.setRequestedGroupName("kde");
		assertEquals(ConstantID.GROUP_KDE.getId(), this.db.getGeneral().getGroupIdByGroupName(this.bookmarkParam));
		// group doesn't exist
		this.bookmarkParam.setRequestedGroupName("this-group-doesnt-exists");
		assertEquals(ConstantID.GROUP_INVALID.getId(), this.db.getGeneral().getGroupIdByGroupName(this.bookmarkParam));
		// groupname is null
		this.bookmarkParam.setRequestedGroupName(null);
		try {
			this.db.getGeneral().getGroupIdByGroupName(this.bookmarkParam);
			fail("Exception should be thrown");
		} catch (final Exception ex) {
		}
	}

	@Test
	public void getGroupIdByGroupNameAndUserName() {
		// group exists
		this.bookmarkParam.setRequestedGroupName("kde");
		assertEquals(ConstantID.GROUP_KDE.getId(), this.db.getGeneral().getGroupIdByGroupNameAndUserName(this.bookmarkParam));
		// group doesn't exist
		this.bookmarkParam.setRequestedGroupName("this-group-doesnt-exists");
		assertEquals(ConstantID.GROUP_INVALID.getId(), this.db.getGeneral().getGroupIdByGroupNameAndUserName(this.bookmarkParam));
		// groupname is null
		this.bookmarkParam.setRequestedGroupName(null);
		try {
			this.db.getGeneral().getGroupIdByGroupNameAndUserName(this.bookmarkParam);
			fail("Exception should be thrown");
		} catch (final Exception ex) {
		}
	}
}