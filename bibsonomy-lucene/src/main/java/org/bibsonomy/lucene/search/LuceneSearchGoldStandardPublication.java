package org.bibsonomy.lucene.search;

import static org.bibsonomy.lucene.util.LuceneBase.FLD_AUTHOR;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
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
	protected String getResourceName() {
		return GoldStandardPublication.class.getSimpleName();
	}
}
