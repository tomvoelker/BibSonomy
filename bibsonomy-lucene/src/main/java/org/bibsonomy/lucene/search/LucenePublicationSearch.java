package org.bibsonomy.lucene.search;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.model.BibTex;

/**
 * @author dzo
 * @version 
 *
 * @param <P>
 */
public class LucenePublicationSearch<P extends BibTex> extends LuceneResourceSearch<P> {

    @Override
    protected BooleanQuery buildSearchQuery(final String userName, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex) {
		final BooleanQuery searchQuery = super.buildSearchQuery(userName, searchTerms, titleSearchTerms, authorSearchTerms, tagIndex);
			
		// search author
		if (present(authorSearchTerms)) {
			final Query authorQuery = this.parseSearchQuery(LuceneFieldNames.AUTHOR, authorSearchTerms);
			searchQuery.add(authorQuery, Occur.MUST);
		}
		
		return searchQuery;
    }
}
