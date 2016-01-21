/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.testutil;

import javax.sql.DataSource;

import org.bibsonomy.database.AbstractDatabaseTest;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.common.impl.AbstractDBSessionFactory;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.junit.Ignore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * @author dzo
 */
@Ignore
public class TestDatabaseManager extends AbstractDatabaseManager {
	private static SqlMapClient SQL_MAP = null;
	
	static {
		final SqlMapClientFactoryBean factoryBean = new SqlMapClientFactoryBean();
		factoryBean.setConfigLocation(new ClassPathResource("TestSqlMapConfig.xml"));
		factoryBean.setDataSource(AbstractDatabaseTest.testDatabaseContext.getBean(DataSource.class));
		try {
			factoryBean.afterPropertiesSet();
		} catch (final Exception ex) {
			throw new RuntimeException(ex);
		}
		
		SQL_MAP = factoryBean.getObject();
	}
	
	private static final TestSessionFactory TESTSESSION_FACTORY = new TestSessionFactory();
	
	private static final class TestSessionFactory extends AbstractDBSessionFactory {

		@Override
		protected SqlMapSession getSqlMap() {
			return SQL_MAP.openSession();
		}
		
	}
	
	private DBSession createDBSession() {
		return TESTSESSION_FACTORY.getDatabaseSession();
	}
	
	// TODO: move to AbstractDBManager?
	private int checkResult(final Integer value) {
		return value == null ? 0 : value;
	}
	
	private double checkResult(final Double value) {
		return value == null ? 0 : value;
	}
	
	/** 
	 * @param param
	 * @return count requested contentID from BibTeX
	 */
	public int countRequestedContentIdFromBibTex(final BibTexParam param) {
		final DBSession session = this.createDBSession();
		try {
			return this.queryForObject("countRequestedContentIdFromBibTex", param, Integer.class, session);
		} finally {
			session.close();
		}
	}
	
	/** 
	 * @param idsType
	 * @return current contentID
	 */
	public int getCurrentContentId(final ConstantID idsType) {
		final DBSession session = this.createDBSession();
		try {
			return this.queryForObject("getCurrentContentId", idsType.getId(), Integer.class, session);
		} finally {
			session.close();
		}
	}
	
	/**
	 * @param param
	 * @return count logged tasIDs
	 */
	public int countLoggedTasIds(final TagParam param) {
		final DBSession session = this.createDBSession();
		try {
			return this.queryForObject("countLoggedTasIds", param, Integer.class, session);
		} finally {
			session.close();
		}
	}
	
	/**
	 * @param param
	 * @return count new contentID from BibTeX
	 */
	public int countNewContentIdFromBibTex(final BibTexParam param) {
		final DBSession session = this.createDBSession();
		try {
			return this.queryForObject("countNewContentIdFromBibTex", param, Integer.class, session);
		} finally {
			session.close();
		}
	}

	/**
	 * @param param
	 * @return count new contentID from Bookmark
	 */
	public int countNewContentIdFromBookmark(final BookmarkParam param) {
		final DBSession session = this.createDBSession();
		try {
			return this.queryForObject("countNewContentIdFromBookmark", param, Integer.class, session);
		} finally {
			session.close();
		}
	}

	/**
	 * @param param
	 * @return count requested contentID from Bookmark
	 */
	public int countRequestedContentIdFromBookmark(final BookmarkParam param) {
		final DBSession session = this.createDBSession();
		try {
			return this.queryForObject("countRequestedContentIdFromBookmark", param, Integer.class, session);
		} finally {
			session.close();
		}
	}

	/**
	 * @param param
	 * @return count tasIDs
	 */
	public int countTasIds(final TagParam param) {
		final DBSession session = this.createDBSession();
		try {			
			return this.queryForObject("countTasIds", param, Integer.class, session);
		} finally {
			session.close();
		}
	}

	/**
	 * @param param
	 * @return count tag relation
	 */
	public int countTagRelation(final TagRelationParam param) {
		final DBSession session = this.createDBSession();
		try {
			return this.queryForObject("countTagRelation", param, Integer.class, session);
		} finally {
			session.close();
		}
	}

	/**
	 * @param param
	 * @return count group
	 */
	public int countGroup(final GroupParam param) {
		final DBSession session = this.createDBSession();
		try {
			return this.queryForObject("countGroup", param, Integer.class, session);
		} finally {
			session.close();
		}
	}
	
	/**
	 * @return number of all review log entries
	 */
	public int countReviewLogs() {
		final DBSession session = this.createDBSession();
		try {
			final Integer result = this.queryForObject("countReviewLogs", Integer.class, session);
			return this.checkResult(result);
		} finally {
			session.close();
		}
	}
	
	/**
	 * @param interHash
	 * @return the average ratings (of reviews)
	 */
	public double getReviewRatingsArithmeticMean(final String interHash) {
		final DBSession session = this.createDBSession();
		try {
			final Double result = this.queryForObject("getReviewRatingsArithmeticMean", interHash, Double.class, session);
			return this.checkResult(result);
		} finally {
			session.close();
		}
	}
	
	/**
	 * @param interHash
	 * @return the number of reviews for the interHash
	 */
	public int getReviewCount(final String interHash) {
		final DBSession session = this.createDBSession();
		try {
			final Integer result = this.queryForObject("getReviewCount", interHash, Integer.class, session);
			return this.checkResult(result);			
		} finally {
			session.close();
		}
	}
	
	/**
	 * 
	 * @param receiverName optional
	 * @return count of logged messages for receiver, or global count if receiverName is null  
	 */
	public int getLogInboxCount(final String receiverName) {
		final DBSession session = this.createDBSession();
		try {			
			return this.queryForObject("inboxLogCount", receiverName, Integer.class, session);
		} finally {
			session.close();
		}
	}
}
