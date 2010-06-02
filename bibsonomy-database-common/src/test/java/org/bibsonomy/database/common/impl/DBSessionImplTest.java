package org.bibsonomy.database.common.impl;
//package org.bibsonomy.database.util;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
//import org.bibsonomy.testutil.JNDITestDatabaseBinder;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// * TODO: adapt
// * @author Christian Schenk
// * @version $Id$
// */
//public class DBSessionImplTest {
//
//	private DBSessionImpl session;
//
//	/**
//	 * setUp
//	 */
//	@Before
//	public void setUp() {
//		JNDITestDatabaseBinder.bind();
//		this.session = (DBSessionImpl) DatabaseUtils.getDBSessionFactory().getDatabaseSession();
//	}
//
//	/**
//	 * tearDown
//	 */
//	@After
//	public void tearDown() {
//		this.session.close();
//		JNDITestDatabaseBinder.unbind();
//	}
//
//	/**
//	 * tests getSqlMapExecutor
//	 */
//	@Test
//	public void getSqlMapExecutor() {
//		assertNotNull(this.session.getSqlMapExecutor());
//	}
//
//	/**
//	 * tests normalCycleWithEndTransaction
//	 */
//	@Test
//	public void normalCycleWithEndTransaction() {
//		this.session.beginTransaction();
//		this.session.endTransaction();
//	}
//
//	/**
//	 * tests normalCycleWithCommitTransaction
//	 */
//	@Test
//	public void normalCycleWithCommitTransaction() {
//		this.session.beginTransaction();
//		this.session.commitTransaction();
//		this.session.endTransaction();
//	}
//
//	/**
//	 * tests nestedCycleWithAbort
//	 */
//	@Test
//	public void nestedCycleWithAbort() {
//		assertFalse(this.session.isAborted());
//
//		this.session.beginTransaction();
//		assertFalse(this.session.isAborted());
//
//		this.session.beginTransaction();
//		assertFalse(this.session.isAborted());
//		this.session.commitTransaction();
//		assertFalse(this.session.isAborted());
//		this.session.endTransaction();
//		assertFalse(this.session.isAborted());
//
//		this.session.beginTransaction();
//		assertFalse(this.session.isAborted());
//		this.session.commitTransaction();
//		assertFalse(this.session.isAborted());
//		this.session.endTransaction();
//		assertFalse(this.session.isAborted());
//
//		this.session.beginTransaction();
//		assertFalse(this.session.isAborted());
//		this.session.endTransaction();
//		assertTrue(this.session.isAborted());
//		this.session.endTransaction();
//		assertFalse(this.session.isAborted());
//	}
//
//	/**
//	 * tests multipleTimesBeginTransaction
//	 */
//	@Test
//	public void multipleTimesBeginTransaction() {
//		this.session.beginTransaction();
//		this.session.beginTransaction();
//		this.session.beginTransaction();
//	}
//
//	/**
//	 * tests multipleTimesEndTransaction
//	 */
//	@Test
//	public void multipleTimesEndTransaction() {
//		this.session.beginTransaction();
//		this.session.beginTransaction();
//		this.session.endTransaction();
//		this.session.endTransaction();
//		try {
//			this.session.endTransaction();
//			fail("should throw exception");
//		} catch (final Exception ex) {
//		}
//	}
//
//	/**
//	 * tests multipleTimesCommitTransaction
//	 */
//	@Test
//	public void multipleTimesCommitTransaction() {
//		this.session.beginTransaction();
//		this.session.beginTransaction();
//		this.session.commitTransaction();
//		this.session.commitTransaction();
//		try {
//			this.session.commitTransaction();
//			fail("should throw exception");
//		} catch (final Exception ex) {
//		}
//	}
//}