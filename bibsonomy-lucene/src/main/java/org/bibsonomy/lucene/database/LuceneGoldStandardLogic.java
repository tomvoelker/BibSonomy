package org.bibsonomy.lucene.database;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.GoldStandardPublication;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneGoldStandardLogic extends LuceneDBLogic<GoldStandardPublication> {
	
	@Override
	public Integer getLastTasId() {
    	final DBSession session = this.openSession();
    	try {
    	    return this.queryForObject("getLastContentId", Integer.class, session);
    	} finally {
    	    session.close();
    	}
	}
}