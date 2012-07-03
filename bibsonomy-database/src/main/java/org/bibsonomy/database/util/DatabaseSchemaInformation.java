package org.bibsonomy.database.util;


import org.bibsonomy.database.common.util.AbstractDatabaseSchemaInformation;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * @author dzo
 * @version $Id$
 */
public class DatabaseSchemaInformation extends AbstractDatabaseSchemaInformation {
	
	private static final String PUBLICATION_COMMON_ID = "BibTexCommon.bibtex_common";
	private static final String USER_COMMON_ID = "UserCommon.user";
	
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
	 * the name of the discussion database table
	 */
	public static final String DISCUSSION_TABLE = "discussion";
	
	private SqlMapClient client;
	
	/**
	 * inits max fields and so on
	 */
	public void init() {
		final SqlMapSession sqlMap = this.client.openSession();
		/*
		 * we provide the database table name to make the getMaxFieldLengths call faster!
		 */
		try {
			this.insertMaxFieldLengths(PUBLICATION_COMMON_ID, PUBLICATION_TABLE, sqlMap);
			this.insertMaxFieldLengths(USER_COMMON_ID, USER_TABLE, sqlMap);
		} finally {
			sqlMap.close();
		}
	}
	
	/**
	 * @param client the client to set
	 */
	public void setClient(final SqlMapClient client) {
		this.client = client;
	}
}
