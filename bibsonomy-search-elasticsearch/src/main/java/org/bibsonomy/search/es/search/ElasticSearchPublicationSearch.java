package org.bibsonomy.search.es.search;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;

/**
 * handles publication relevant search
 * 
 * @author dzo
 * @param <P> 
 */
public class ElasticSearchPublicationSearch<P extends BibTex> extends EsResourceSearch<P> {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.search.EsResourceSearch#buildResourceSpecifiyQuery(org.elasticsearch.index.query.BoolQueryBuilder, java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	protected void buildResourceSpecifiyQuery(BoolQueryBuilder mainQueryBuilder, String userName, String requestedUserName, String requestedGroupName, List<String> requestedRelationNames, Collection<String> allowedGroups, String searchTerms, String titleSearchTerms, String authorSearchTerms, String bibtexKey, String year, String firstYear, String lastYear) {
		super.buildResourceSpecifiyQuery(mainQueryBuilder, userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, bibtexKey, year, firstYear, lastYear);
		
		if (present(authorSearchTerms)) {
			final QueryBuilder authorSearchQuery = QueryBuilders.termQuery(Fields.Publication.AUTHOR, authorSearchTerms);
			mainQueryBuilder.must(authorSearchQuery);
		}
		
		if (present(bibtexKey)) {
			final QueryBuilder bibtexKeyQuery = QueryBuilders.termQuery(Fields.Publication.BIBTEXKEY, bibtexKey);
			mainQueryBuilder.must(bibtexKeyQuery);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.search.EsResourceSearch#buildResourceSpecifiyFilters(org.elasticsearch.index.query.BoolFilterBuilder, java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	protected void buildResourceSpecifiyFilters(BoolFilterBuilder mainFilterBuilder, String userName, String requestedUserName, String requestedGroupName, List<String> requestedRelationNames, Collection<String> allowedGroups, String searchTerms, String titleSearchTerms, String authorSearchTerms, String bibtexKey, String year, String firstYear, String lastYear) {
		super.buildResourceSpecifiyFilters(mainFilterBuilder, userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, bibtexKey, year, firstYear, lastYear);
		
		if (present(year)) {
			final TermFilterBuilder yearQuery = FilterBuilders.termFilter(Fields.Publication.YEAR, year);
			mainFilterBuilder.must(yearQuery);
		}
		
		final boolean presentLastYear = present(lastYear);
		final boolean presentFirstYear = present(firstYear);
		if (presentLastYear || presentFirstYear) {
			final RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter(Fields.Publication.YEAR);
			if (presentFirstYear) {
				rangeFilter.gte(Integer.parseInt(firstYear));
			}
			
			if (presentLastYear) {
				rangeFilter.lte(Integer.parseInt(lastYear));
			}
			mainFilterBuilder.must(rangeFilter);
		}
	}
}
