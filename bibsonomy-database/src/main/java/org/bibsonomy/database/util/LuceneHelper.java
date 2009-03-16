package org.bibsonomy.database.util;

import java.util.ArrayList;

import org.bibsonomy.database.AbstractDatabaseManager;

/**
 * LuceneHelper manages temporary database tables needed for sql-queries 
 * 
 * @author SvenStefani
 * @version $Id$
 */
public class LuceneHelper extends AbstractDatabaseManager  {
	
	/** create temporary memory table
	 *  
	 * @param session
	 */
	public void createTTable(final DBSession session) { 
//		Integer ErrCodeTempC = dbconn.rawUpdate("CREATE TEMPORARY TABLE IF NOT EXISTS tempcids (cid bigint, PRIMARY KEY (cid)) ENGINE=MEMORY;", connection);
		Object param = new Object();
		this.update("createLuceneTT", param ,session);
	}

	/** Create temporary memory table. 
	 * 
	 * @param param
	 * @param session
	 */

	/** truncate temporary memory table
	 * 
	 * @param session
	 */
	public void truncateTTable(final DBSession session) { 
//		Integer ErrCodeTempD = dbconn.rawUpdate("TRUNCATE TABLE tempcids; ", connection);
		Object param = new Object();
		this.update("truncateLuceneTT", param ,session);
	}	
		
	/** store data given in ArrayList into database
	 * @param contentIds 
	 * 
	 * @param session
	 */
	public void fillTTable(final ArrayList<Integer> contentIds, final DBSession session)
	{
		for  (final int contentId: contentIds) {
//			Integer ErrCodeTempS = dbconn.rawUpdate("INSERT INTO tempcids (cid) VALUE ("+e+");", connection);
			this.insert("insertLuceneTT", contentId, session);
		}			
	}	

	

}