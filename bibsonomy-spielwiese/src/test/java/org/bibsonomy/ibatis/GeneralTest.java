package org.bibsonomy.ibatis;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Test;

/**
 * General tests.
 * 
 * @author mgr
 * @author Christian Schenk
 */
public class GeneralTest extends AbstractSqlMapTest {

	@Test
	public void isFriendOf() throws SQLException {
		this.bookmarkParam.setUserName("stumme");
		this.bookmarkParam.setFriendUserName("grahl");
		assertTrue((Boolean)this.sqlMap.queryForObject("isFriendOf", this.bookmarkParam));
	}
}