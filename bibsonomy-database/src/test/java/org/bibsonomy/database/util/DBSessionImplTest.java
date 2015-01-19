/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.database.AbstractDatabaseTest;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.common.impl.DBSessionImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jens Illig
 * @author Christian Schenk
 * 
 */
public class DBSessionImplTest extends AbstractDatabaseTest {

	private DBSessionImpl session;

	/**
	 * setUp
	 */
	@Before
	public void setUp() {
		this.session = (DBSessionImpl) testDatabaseContext.getBean(DBSessionFactory.class).getDatabaseSession();
	}

	/**
	 * tearDown
	 */
	@After
	public void tearDown() {
		this.session.close();
	}

	/**
	 * tests getSqlMapExecutor
	 */
	@Test
	public void getSqlMapExecutor() {
		assertNotNull(this.session.getSqlMapExecutor());
	}

	/**
	 * tests normalCycleWithEndTransaction
	 */
	@Test
	public void normalCycleWithEndTransaction() {
		this.session.beginTransaction();
		this.session.endTransaction();
	}

	/**
	 * tests normalCycleWithCommitTransaction
	 */
	@Test
	public void normalCycleWithCommitTransaction() {
		this.session.beginTransaction();
		this.session.commitTransaction();
		this.session.endTransaction();
	}

	/**
	 * tests nestedCycleWithAbort
	 */
	@Test
	public void nestedCycleWithAbort() {
		assertFalse(this.session.isAborted());

		this.session.beginTransaction();
		assertFalse(this.session.isAborted());

		this.session.beginTransaction();
		assertFalse(this.session.isAborted());
		this.session.commitTransaction();
		assertFalse(this.session.isAborted());
		this.session.endTransaction();
		assertFalse(this.session.isAborted());

		this.session.beginTransaction();
		assertFalse(this.session.isAborted());
		this.session.commitTransaction();
		assertFalse(this.session.isAborted());
		this.session.endTransaction();
		assertFalse(this.session.isAborted());

		this.session.beginTransaction();
		assertFalse(this.session.isAborted());
		this.session.endTransaction();
		assertTrue(this.session.isAborted());
		this.session.endTransaction();
		assertFalse(this.session.isAborted());
	}

	/**
	 * tests multipleTimesBeginTransaction
	 */
	@Test
	public void multipleTimesBeginTransaction() {
		this.session.beginTransaction();
		this.session.beginTransaction();
		this.session.beginTransaction();
	}

	/**
	 * tests multipleTimesEndTransaction
	 */
	@Test
	public void multipleTimesEndTransaction() {
		this.session.beginTransaction();
		this.session.beginTransaction();
		this.session.endTransaction();
		this.session.endTransaction();
		try {
			this.session.endTransaction();
			fail("should throw exception");
		} catch (final Exception ex) {
		}
	}

	/**
	 * tests multipleTimesCommitTransaction
	 */
	@Test
	public void multipleTimesCommitTransaction() {
		this.session.beginTransaction();
		this.session.beginTransaction();
		this.session.commitTransaction();
		this.session.commitTransaction();
		try {
			this.session.commitTransaction();
			fail("should throw exception");
		} catch (final Exception ex) {
		}
	}
}