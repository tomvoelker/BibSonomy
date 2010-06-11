package org.bibsonomy.lucene.search;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.List;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.bibsonomy.lucene.param.QuerySortContainer;
import org.bibsonomy.model.GoldStandardPublication;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneSearchGoldStandardPublication extends LuceneResourceSearch<GoldStandardPublication> {
	
	private static final LuceneSearchGoldStandardPublication INSTANCE = new LuceneSearchGoldStandardPublication();

	/**
	 * @return the @{link:LuceneSearchGoldStandardPublication} instance
	 */
	public static LuceneSearchGoldStandardPublication getInstance() {
		return INSTANCE;
	}
	
	private LuceneSearchGoldStandardPublication() {
		this.reloadIndex(0);
	}

	@Override
	protected BooleanQuery buildSearchQuery(String userName, String searchTerms, String titleSearchTerms, String authorSearchTerms, Collection<String> tagIndex) {
		final BooleanQuery searchQuery = super.buildSearchQuery(userName, searchTerms, titleSearchTerms, authorSearchTerms, tagIndex);
		
		// search author
		if( present(authorSearchTerms) ) {
			final Query authorQuery = this.parseSearchQuery(FLD_AUTHOR, authorSearchTerms);
			searchQuery.add(authorQuery, Occur.MUST);
		}
		
		return searchQuery;
	}
	
	@Override
	protected QuerySortContainer buildAuthorQuery(String group, String searchTerms, String requestedUserName, String requestedGroupName, List<String> groupMembers, String year, String firstYear, String lastYear, List<String> tagList) {
		// TODO: remove old code
		return null;
	}

	@Override
	protected String getResourceName() {
		return GoldStandardPublication.class.getSimpleName();
	}
}
