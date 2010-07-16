package org.bibsonomy.lucene.search;

import static org.bibsonomy.lucene.util.LuceneBase.FLD_AUTHOR;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.bibsonomy.model.BibTex;

/**
 * class for bibtex search
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneSearchBibTex extends LuceneResourceSearch<BibTex> {
	private final static LuceneSearchBibTex singleton = new LuceneSearchBibTex();
	
	/**
	 * @return LuceneSearchBookmarks
	 */
	public static LuceneSearchBibTex getInstance() {
		return singleton;
	}
	
	/**
	 * constructor
	 */
	private LuceneSearchBibTex() {
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
		return BibTex.class.getSimpleName();
	}
}