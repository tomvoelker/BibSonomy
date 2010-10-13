package org.bibsonomy.lucene.database;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.lucene.database.params.ResourcesParam;
import org.bibsonomy.model.GoldStandardPublication;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneGoldStandardPublicationLogic extends LuceneDBLogic<GoldStandardPublication> {

	@Override
	protected String getResourceName() {
		return GoldStandardPublication.class.getSimpleName();
	}

	@Override
	protected ResourcesParam<GoldStandardPublication> getResourcesParam() {
		return new ResourcesParam<GoldStandardPublication>();
	}
	
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