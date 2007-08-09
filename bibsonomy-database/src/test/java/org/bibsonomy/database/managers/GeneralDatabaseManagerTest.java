package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.junit.Test;

/**
 * General tests.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class GeneralDatabaseManagerTest extends AbstractDatabaseManagerTest {

	@Test
	public void isFriendOf() {
		this.generalParam.setUserName("stumme");
		this.generalParam.setRequestedUserName("grahl");
		assertTrue(this.generalDb.isFriendOf(this.generalParam, this.dbSession));

		// with no users set no exception should be thrown and the result should
		// just be "false"
		this.generalParam.setUserName(null);
		this.generalParam.setRequestedUserName(null);
		assertFalse(this.generalDb.isFriendOf(this.generalParam, this.dbSession));
	}

	@Test
	public void getGroupIdsForUser() {
		assertEquals(4, this.generalDb.getGroupIdsForUser(this.generalParam.getUserName(), this.dbSession).size());
		this.generalParam.setUserName(null);
		assertTrue(this.generalDb.getGroupIdsForUser(this.generalParam.getUserName(), this.dbSession).size() == 0);
	}

	/*
	 * Hint: we have to call the more generic method before the specific one,
	 * i.e. getGroupIdByGroupNameAndUserName before getGroupIdByGroupName,
	 * because the latter will have side effects.
	 */
	@Test
	public void getGroupIdByGroupNameAndUserName() {
		// group exists
		this.generalParam.setRequestedGroupName("kde");
		assertEquals(GroupID.KDE.getId(), this.generalDb.getGroupIdByGroupNameAndUserName(this.generalParam, this.dbSession));
		assertEquals(GroupID.KDE.getId(), this.generalDb.getGroupIdByGroupName(this.generalParam, this.dbSession));

		// group doesn't exist
		this.resetParameters();
		this.generalParam.setRequestedGroupName("this-group-doesnt-exists");
		assertEquals(GroupID.INVALID.getId(), this.generalDb.getGroupIdByGroupNameAndUserName(this.generalParam, this.dbSession));
		assertEquals(GroupID.INVALID.getId(), this.generalDb.getGroupIdByGroupName(this.generalParam, this.dbSession));

		// groupname is null
		this.resetParameters();
		this.generalParam.setRequestedGroupName(null);
		try {
			this.generalDb.getGroupIdByGroupNameAndUserName(this.generalParam, this.dbSession);
			fail("Exception should be thrown");
		} catch (final Exception ex) {
		}
		try {
			this.generalDb.getGroupIdByGroupName(this.generalParam, this.dbSession);
			fail("Exception should be thrown");
		} catch (final Exception ex) {
		}
	}

	@Test
	public void isSpammer() {
		// User "stumme" isn't a spammer
		assertEquals(false, this.generalDb.isSpammer(this.generalParam, this.dbSession));
		// This user is a spammer
		this.generalParam.setRequestedUserName("alexsandra");
		assertEquals(true, this.generalDb.isSpammer(this.generalParam, this.dbSession));
		// Default behaviour
		for (final String requestedUserName : new String[] { "", " ", null }) {
			this.generalParam.setRequestedUserName(requestedUserName);
			assertEquals(false, this.generalDb.isSpammer(this.generalParam, this.dbSession));
		}
	}

	@Test
	public void getNewContentId() {
		assertNull(this.generalDb.getNewContentId(ConstantID.IDS_UNDEFINED_CONTENT_ID, this.dbSession));
		final int id = this.generalDb.getNewContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		assertTrue(2649144 < id);
		assertTrue(id < this.generalDb.getNewContentId(ConstantID.IDS_CONTENT_ID, this.dbSession));
		try {
			this.generalDb.getNewContentId(null, this.dbSession);
			fail("Exception should be thrown");
		} catch (final Exception ex) {
		}
	}

	@Test
	public void updateIds() {
		Integer curId = null;
		Integer newId = null;
		curId = this.generalDb.getNewContentId(ConstantID.IDS_TAS_ID, this.dbSession);
		this.generalDb.updateIds(ConstantID.IDS_TAS_ID, this.dbSession);
		newId = this.generalDb.getNewContentId(ConstantID.IDS_TAS_ID, this.dbSession);
		assertTrue(newId == curId + 2);
	}
}