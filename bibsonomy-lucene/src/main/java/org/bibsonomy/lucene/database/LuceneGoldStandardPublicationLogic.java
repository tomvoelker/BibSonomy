package org.bibsonomy.lucene.database;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.params.Pair;
import org.bibsonomy.lucene.database.params.ResourcesParam;
import org.bibsonomy.lucene.param.LucenePost;
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
	public Date getLatestDate() {
		final DBSession session = this.openSession();
		try {
			return this.queryForObject("getLatestDate" + this.getResourceName(), Date.class, session);
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<LucenePost<GoldStandardPublication>> getNewPosts(Date from, Date now) {
		final DBSession session = this.openSession();
		
		final Pair<Date, Date> date = new Pair<Date, Date>(from, now);
		
		try {
			return this.queryForList("getNewPosts" + this.getResourceName(), date, session);
		} finally {
			session.close();
		}
	}
}
