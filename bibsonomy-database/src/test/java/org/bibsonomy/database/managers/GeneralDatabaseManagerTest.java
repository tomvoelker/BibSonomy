package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.common.enums.ConstantID;
import org.junit.Test;

/**
 * General tests.
 * 
 * @author Miranda Grahl
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
	public void getGroupsForUser() {
		assertEquals(4, this.generalDb.getGroupsForUser(this.generalParam, this.dbSession).size());
		this.generalParam.setUserName(null);
		assertTrue(this.generalDb.getGroupsForUser(this.generalParam, this.dbSession).size() == 0);
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
		assertEquals(ConstantID.GROUP_KDE.getId(), this.generalDb.getGroupIdByGroupNameAndUserName(this.generalParam, this.dbSession));
		assertEquals(ConstantID.GROUP_KDE.getId(), this.generalDb.getGroupIdByGroupName(this.generalParam, this.dbSession));

		// group doesn't exist
		this.resetParameters();
		this.generalParam.setRequestedGroupName("this-group-doesnt-exists");
		assertEquals(ConstantID.GROUP_INVALID.getId(), this.generalDb.getGroupIdByGroupNameAndUserName(this.generalParam, this.dbSession));
		assertEquals(ConstantID.GROUP_INVALID.getId(), this.generalDb.getGroupIdByGroupName(this.generalParam, this.dbSession));

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
		this.generalParam.setRequestedUserName("");
		assertEquals(null, this.generalDb.isSpammer(this.generalParam, this.dbSession)); // FIXME shouldn't be null
		this.generalParam.setRequestedUserName(null);
		assertEquals(false, this.generalDb.isSpammer(this.generalParam, this.dbSession));
	}

	@Test
	public void getNewContentId() {
		this.generalParam.setIdsType(ConstantID.IDS_UNDEFINED_CONTENT_ID);
		assertEquals(null, this.generalDb.getNewContentId(this.generalParam, this.dbSession));
		this.generalParam.setIdsType(ConstantID.IDS_CONTENT_ID);
		assertEquals(2649144, this.generalDb.getNewContentId(this.generalParam, this.dbSession));

		this.generalParam.setIdsType(null);
		try {
			this.generalDb.getNewContentId(this.generalParam, this.dbSession);
			fail("Exception should be thrown");
		} catch (final Exception ex) {
		}
	}

	@Test
	public void updateIds() {
		Integer curId = 0;
		Integer newId = 0;
		curId = this.generalDb.getNewContentId(this.generalParam, this.dbSession);
		this.generalDb.updateIds(this.generalParam, this.dbSession);
		newId = this.generalDb.getNewContentId(this.generalParam, this.dbSession);
		assertTrue(newId == curId + 1);
	}
}