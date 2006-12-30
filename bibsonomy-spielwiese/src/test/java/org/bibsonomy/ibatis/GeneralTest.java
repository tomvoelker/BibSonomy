package org.bibsonomy.ibatis;

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
		this.bookmarkParam.setFriendUserName("grahl");
		assertTrue(this.db.getGeneral().isFriendOf(this.bookmarkParam));
	}
}