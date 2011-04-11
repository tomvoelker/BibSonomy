package org.bibsonomy.testutil;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.impl.AbstractDBSessionFactory;
import org.bibsonomy.database.common.util.IbatisUtils;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * @author dzo
 * @version $Id$
 */
public class TestDatabaseManager extends AbstractDatabaseManager {
	private static final SqlMapClient SQL_MAP = IbatisUtils.loadSqlMap("TestSqlMapConfig.xml");
	
	private static final TestSessionFactory TESTSESSION_FACTORY = new TestSessionFactory();
	
	private static final class TestSessionFactory extends AbstractDBSessionFactory {

		@Override
		protected SqlMapSession getSqlMap() {
			return SQL_MAP.openSession();
		}
		
	}
	
	public DBSession createDBSession() {
		return TESTSESSION_FACTORY.getDatabaseSession();
	}
	
	/** 
	 * @param param
	 * @return count requested contentID from BibTeX
	 */
	public Integer countRequestedContentIdFromBibTex(final BibTexParam param) {
		return this.queryForObject("countRequestedContentIdFromBibTex", param, Integer.class, this.createDBSession());
	}
	
	/** 
	 * @param idsType
	 * @return current contentID
	 */
	public Integer getCurrentContentId(final ConstantID idsType) {
		return this.queryForObject("getCurrentContentId", idsType.getId(), Integer.class, this.createDBSession());
	}
	
	/**
	 * @param param
	 * @return count logged tasIDs
	 */
	public Integer countLoggedTasIds(final TagParam param) {
		return this.queryForObject("countLoggedTasIds", param, Integer.class, this.createDBSession());
	}
	
	/**
	 * @param param
	 * @return count new contentID from BibTeX
	 */
	public Integer countNewContentIdFromBibTex(final BibTexParam param) {
		return this.queryForObject("countNewContentIdFromBibTex", param, Integer.class, this.createDBSession());
	}

	/**
	 * @param param
	 * @return count new contentID from Bookmark
	 */
	public Integer countNewContentIdFromBookmark(final BookmarkParam param) {
		return this.queryForObject("countNewContentIdFromBookmark", param, Integer.class, this.createDBSession());
	}

	/**
	 * @param param
	 * @return count requested contentID from Bookmark
	 */
	public Integer countRequestedContentIdFromBookmark(final BookmarkParam param) {
		return this.queryForObject("countRequestedContentIdFromBookmark", param, Integer.class, this.createDBSession());
	}

	/**
	 * @param param
	 * @return count tasIDs
	 */
	public Integer countTasIds(final TagParam param) {
		return this.queryForObject("countTasIds", param, Integer.class, this.createDBSession());
	}

	/**
	 * @param param
	 * @return count tag relation
	 */
	public Integer countTagRelation(final TagRelationParam param) {
		return this.queryForObject("countTagRelation", param, Integer.class, this.createDBSession());
	}

	/**
	 * @param param
	 * @return count group
	 */
	public Integer countGroup(final GroupParam param) {
		return this.queryForObject("countGroup", param, Integer.class, this.createDBSession());
	}
	
	/**
	 * @return number of all review log entries
	 */
	public int countReviewLogs() {
		return this.queryForObject("countReviewLogs", Integer.class, this.createDBSession());
	}
	
	/**
	 * @param interHash
	 * @return the average ratings (of reviews)
	 */
	public double getReviewRatingsAverage(final String interHash) {
		final Double result = this.queryForObject("getReviewRatingsAverage", interHash, Double.class, this.createDBSession());
		return result == null ? 0 : result;
	}
	
	/**
	 * @param interHash
	 * @return the number of reviews for the interHash
	 */
	public int getReviewCount(final String interHash) {
		final Integer result = this.queryForObject("getReviewCount", interHash, Integer.class, this.createDBSession());
		return result == null ? 0 : result;
	}
}
