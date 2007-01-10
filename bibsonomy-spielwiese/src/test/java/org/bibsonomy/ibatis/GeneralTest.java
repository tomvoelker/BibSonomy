package org.bibsonomy.ibatis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
}