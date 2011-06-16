package org.bibsonomy.database.util;


import org.bibsonomy.database.common.util.AbstractDatabaseSchemaInformation;

import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * @author dzo
 * @version $Id$
 */
public class DatabaseSchemaInformation extends AbstractDatabaseSchemaInformation {
	
	private static final String PUBLICATION_COMMON_ID = "BibTexCommon.bibtex_common";
	private static final String USER_COMMON_ID = "UserCommon.user";
	private static final String COMMENT_COMMON_ID = "CommentCommon.comment";
	private static final String REVIEW_COMMON_ID = "ReviewCommon.review";
	
	/**
	 * the name of the publication database table
	 */
	public static final String PUBLICATION_TABLE = "bibtex";
	
	/**
	 * the name of the bookmark database table
	 */
	public static final String BOOKMARK_TABLE = "bookmark";
	
	/**
	 * the name of the tas database table
	 */
	public static final String TAG_TABLE = "tas";
	
	/**
	 * the name of the grouptas database table
	 */
	public static final String GROUP_TAG_TABLE = "grouptas";
	
	private static final String USER_TABLE = "user";
	
	/**
	 * the name of the discussion groups database table
	 */
	public static final String DISCUSSION_GROUP_TABLE = "discussion_groups";
	private static final String DISCUSSION_TABLE = "discussion";
	
	
	private static final DatabaseSchemaInformation INSTANCE = new DatabaseSchemaInformation();

	/**
	 * @return the @{link:DatabaseSchemaInformationImpl} instance
	 */
	public static DatabaseSchemaInformation getInstance() {
		return INSTANCE;
	}
	
	
	private DatabaseSchemaInformation() {
		final SqlMapSession sqlMap = IbatisDBSessionFactory.getSqlMapClient().openSession();
		/*
		 * we provide the database table name to make the getMaxFieldLengths call faster!
		 */
		try {
			this.insertMaxFieldLengths(PUBLICATION_COMMON_ID, PUBLICATION_TABLE, sqlMap);
			this.insertMaxFieldLengths(USER_COMMON_ID, USER_TABLE, sqlMap);
			this.insertMaxFieldLengths(COMMENT_COMMON_ID, DISCUSSION_TABLE, sqlMap);
			this.insertMaxFieldLengths(REVIEW_COMMON_ID, DISCUSSION_TABLE, sqlMap);
		} finally {
			sqlMap.close();
		}
	}
}
