package org.bibsonomy.database.util;

import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TransactionTest {

	private Transaction transaction;

	@Before
	public void setUp() {
		this.transaction = DatabaseUtils.getDatabaseSession();
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
		Assert.assertFalse(this.transaction.isAborted());
		
		this.transaction.beginTransaction();
		Assert.assertFalse(this.transaction.isAborted());
		
		this.transaction.beginTransaction();
		Assert.assertFalse(this.transaction.isAborted());
		this.transaction.commitTransaction();
		Assert.assertFalse(this.transaction.isAborted());
		this.transaction.endTransaction();
		Assert.assertFalse(this.transaction.isAborted());
		
		this.transaction.beginTransaction();
		Assert.assertFalse(this.transaction.isAborted());
		this.transaction.commitTransaction();
		Assert.assertFalse(this.transaction.isAborted());
		this.transaction.endTransaction();
		Assert.assertFalse(this.transaction.isAborted());
		
		this.transaction.beginTransaction();
		Assert.assertFalse(this.transaction.isAborted());
		this.transaction.endTransaction();
		Assert.assertTrue(this.transaction.isAborted());
		this.transaction.endTransaction();
		Assert.assertFalse(this.transaction.isAborted());
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
			Assert.fail("should throw exception");
		} catch (Exception e) {
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
			Assert.fail("should throw exception");
		} catch (Exception e) {
		}
	}
}