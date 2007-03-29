package org.bibsonomy.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.common.enums.ConstantID;
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
		assertTrue(this.generalDb.isFriendOf(this.bookmarkParam));

		// with no users set no exception should be thrown and the result should
		// just be "false"
		this.bookmarkParam.setUserName(null);
		this.bookmarkParam.setRequestedUserName(null);
		assertFalse(this.generalDb.isFriendOf(this.bookmarkParam));
	}

	@Test
	public void getGroupsForUser() {
		assertEquals(4, this.generalDb.getGroupsForUser(this.bookmarkParam).size());
		this.bookmarkParam.setUserName(null);
		assertTrue(this.generalDb.getGroupsForUser(this.bookmarkParam).size() == 0);
	}

	/*
	 * Hint: we have to call the more generic method before the specific one,
	 * i.e. getGroupIdByGroupNameAndUserName before getGroupIdByGroupName,
	 * because the latter will have side effects.
	 */
	@Test
	public void getGroupIdByGroupNameAndUserName() {
		// group exists
		this.bookmarkParam.setRequestedGroupName("kde");
		assertEquals(ConstantID.GROUP_KDE.getId(), this.generalDb.getGroupIdByGroupNameAndUserName(this.bookmarkParam));
		assertEquals(ConstantID.GROUP_KDE.getId(), this.generalDb.getGroupIdByGroupName(this.bookmarkParam));

		// group doesn't exist
		this.resetParameters();
		this.bookmarkParam.setRequestedGroupName("this-group-doesnt-exists");
		assertEquals(ConstantID.GROUP_INVALID.getId(), this.generalDb.getGroupIdByGroupNameAndUserName(this.bookmarkParam));
		assertEquals(ConstantID.GROUP_INVALID.getId(), this.generalDb.getGroupIdByGroupName(this.bookmarkParam));

		// groupname is null
		this.resetParameters();
		this.bookmarkParam.setRequestedGroupName(null);
		try {
			this.generalDb.getGroupIdByGroupNameAndUserName(this.bookmarkParam);
			fail("Exception should be thrown");
		} catch (final Exception ex) {
		}
		try {
			this.generalDb.getGroupIdByGroupName(this.bookmarkParam);
			fail("Exception should be thrown");
		} catch (final Exception ex) {
		}
	}

	public void isSpammer() {
		this.generalDb.isSpammer(this.bookmarkParam);
	}
}