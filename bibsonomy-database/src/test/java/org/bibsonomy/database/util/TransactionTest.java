package org.bibsonomy.database.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TransactionTest {

	private Transaction transaction;

	@Before
	public void setUp() {
		this.transaction = new Transaction();
	}

	@After
	public void tearDown() {
		this.transaction = null;
	}

	@Test
	public void getSqlMap() {
		assertNotNull(this.transaction.getSqlMap());
	}

	@Test
	public void normalCycleWithEndTransaction() throws SQLException {
		assertNotNull(this.transaction.getSqlMap());
		this.transaction.startTransaction();
		this.transaction.endTransaction();
	}

	@Test
	public void normalCycleWithCommitTransaction() throws SQLException {
		assertNotNull(this.transaction.getSqlMap());
		this.transaction.startTransaction();
		this.transaction.commitTransaction();
	}

	@Test
	public void multipleTimesStartTransaction() throws SQLException {
		this.transaction.startTransaction();
		this.transaction.startTransaction();
		this.transaction.startTransaction();
	}

	@Test
	public void multipleTimesEndTransaction() throws SQLException {
		this.transaction.endTransaction();
		this.transaction.endTransaction();
		this.transaction.endTransaction();
	}

	@Test
	public void commitTransactionWithoutStartTransaction() throws SQLException {
		try {
			this.transaction.commitTransaction();
			fail("Exception should be thrown");
		} catch (final RuntimeException ex) {
		}
	}
}