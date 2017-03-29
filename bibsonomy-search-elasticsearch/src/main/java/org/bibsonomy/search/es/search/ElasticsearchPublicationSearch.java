/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.search.es.search;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

/**
 * handles publication relevant search
 * 
 * @author dzo
 * @param <P> 
 */
public class ElasticsearchPublicationSearch<P extends BibTex> extends EsResourceSearch<P> {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.search.EsResourceSearch#buildResourceSpecifiyQuery(org.elasticsearch.index.query.BoolQueryBuilder, java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	protected void buildResourceSpecifiyQuery(BoolQueryBuilder mainQueryBuilder, String userName, String requestedUserName, String requestedGroupName, List<String> requestedRelationNames, Collection<String> allowedGroups, String searchTerms, String titleSearchTerms, String authorSearchTerms, String bibtexKey, String year, String firstYear, String lastYear) {
		super.buildResourceSpecifiyQuery(mainQueryBuilder, userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, bibtexKey, year, firstYear, lastYear);
		
		if (present(authorSearchTerms)) {
			final QueryBuilder authorSearchQuery = QueryBuilders.matchQuery(Fields.Publication.AUTHORS + "." + Fields.Publication.PERSON_NAME, authorSearchTerms).operator(Operator.AND);
			final NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(Fields.Publication.AUTHORS, authorSearchQuery);
			mainQueryBuilder.must(nestedQuery);
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
	protected void buildResourceSpecifiyFilters(BoolQueryBuilder mainFilterBuilder, String userName, String requestedUserName, String requestedGroupName, List<String> requestedRelationNames, Collection<String> allowedGroups, String searchTerms, String titleSearchTerms, String authorSearchTerms, String bibtexKey, String year, String firstYear, String lastYear) {
		super.buildResourceSpecifiyFilters(mainFilterBuilder, userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, bibtexKey, year, firstYear, lastYear);
		
		if (present(year)) {
			final TermQueryBuilder yearQuery = QueryBuilders.termQuery(Fields.Publication.YEAR, year);
			mainFilterBuilder.must(yearQuery);
		}
		
		final boolean presentLastYear = present(lastYear);
		final boolean presentFirstYear = present(firstYear);
		if (presentLastYear || presentFirstYear) {
			final RangeQueryBuilder rangeFilter = QueryBuilders.rangeQuery(Fields.Publication.YEAR);
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
