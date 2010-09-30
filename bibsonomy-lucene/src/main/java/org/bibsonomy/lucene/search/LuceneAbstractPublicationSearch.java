package org.bibsonomy.lucene.search;

import static org.bibsonomy.lucene.util.LuceneBase.FLD_AUTHOR;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.bibsonomy.model.BibTex;

/**
 * @author dzo
 * @version 
 *
 * @param <P>
 */
public abstract class LuceneAbstractPublicationSearch<P extends BibTex> extends LuceneResourceSearch<P> {

    @Override
    protected BooleanQuery buildSearchQuery(String userName, String searchTerms, String titleSearchTerms, String authorSearchTerms, Collection<String> tagIndex) {
	final BooleanQuery searchQuery = super.buildSearchQuery(userName, searchTerms, titleSearchTerms, authorSearchTerms, tagIndex);
		
	// search author
	if (present(authorSearchTerms)) {
		final Query authorQuery = this.parseSearchQuery(FLD_AUTHOR, authorSearchTerms);
		searchQuery.add(authorQuery, Occur.MUST);
	}
	
	return searchQuery;
    }
}
