package org.bibsonomy.database.managers.chain.statistic.goldstandard;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GoldStandardDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.services.searcher.PostSearchQuery;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 * statistics chain element for full text search
 *
 * @author dzo
 * @param <R>
 */
public class GetPostsByFulltextSearchCount<RR extends Resource, R extends Resource & GoldStandard<RR>> extends ChainElement<Statistics, QueryAdapter<PostQuery<R>>> {

	private final GoldStandardDatabaseManager<RR, R, ?> goldStandardDatabaseManager;

	/**
	 * default constructor
	 * @param goldStandardDatabaseManager
	 */
	public GetPostsByFulltextSearchCount(GoldStandardDatabaseManager<RR, R, ?> goldStandardDatabaseManager) {
		this.goldStandardDatabaseManager = goldStandardDatabaseManager;
	}

	@Override
	protected Statistics handle(QueryAdapter<PostQuery<R>> param, DBSession session) {
		final PostSearchQuery<?> searchQuery = new PostSearchQuery<>(param.getQuery());
		return this.goldStandardDatabaseManager.getPostsByFulltextCount(param.getLoggedinUser(), searchQuery);
	}

	@Override
	protected boolean canHandle(QueryAdapter<PostQuery<R>> param) {
		return true;
	}
}
