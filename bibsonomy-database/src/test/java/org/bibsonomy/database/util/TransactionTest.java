package org.bibsonomy.database.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class TransactionTest {

	private DBSessionImpl transaction;

	@Before
	public void setUp() {
		this.transaction = (DBSessionImpl) DatabaseUtils.getDBSessionFactory().getDatabaseSession();
	}

	@After
	public void tearDown() {
		this.transaction.close();
	}

	@Test
	public void getSqlMapExecutor() {
		assertNotNull(this.transaction.getSqlMapExecutor());
	}

	@Test
	public void normalCycleWithEndTransaction() throws SQLException {
		this.transaction.beginTransaction();
		this.transaction.endTransaction();
	}

	@Test
	public void normalCycleWithCommitTransaction() throws SQLException {
		this.transaction.beginTransaction();
		this.transaction.commitTransaction();
		this.transaction.endTransaction();
	}

	@Test
	public void nestedCycleWithAbort() throws SQLException {
		assertFalse(this.transaction.isAborted());

		this.transaction.beginTransaction();
		assertFalse(this.transaction.isAborted());

		this.transaction.beginTransaction();
		assertFalse(this.transaction.isAborted());
		this.transaction.commitTransaction();
		assertFalse(this.transaction.isAborted());
		this.transaction.endTransaction();
		assertFalse(this.transaction.isAborted());

		this.transaction.beginTransaction();
		assertFalse(this.transaction.isAborted());
		this.transaction.commitTransaction();
		assertFalse(this.transaction.isAborted());
		this.transaction.endTransaction();
		assertFalse(this.transaction.isAborted());

		this.transaction.beginTransaction();
		assertFalse(this.transaction.isAborted());
		this.transaction.endTransaction();
		assertTrue(this.transaction.isAborted());
		this.transaction.endTransaction();
		assertFalse(this.transaction.isAborted());
	}

	@Test
	public void multipleTimesBeginTransaction() throws SQLException {
		this.transaction.beginTransaction();
		this.transaction.beginTransaction();
		this.transaction.beginTransaction();
	}

	@Test
	public void multipleTimesEndTransaction() throws SQLException {
		this.transaction.beginTransaction();
		this.transaction.beginTransaction();
		this.transaction.endTransaction();
		this.transaction.endTransaction();
		try {
			this.transaction.endTransaction();
			fail("should throw exception");
		} catch (final Exception ex) {
		}
	}

	@Test
	public void multipleTimesCommitTransaction() throws SQLException {
		this.transaction.beginTransaction();
		this.transaction.beginTransaction();
		this.transaction.commitTransaction();
		this.transaction.commitTransaction();
		try {
			this.transaction.commitTransaction();
			fail("should throw exception");
		} catch (final Exception ex) {
		}
	}
}