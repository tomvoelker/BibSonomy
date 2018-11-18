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
package org.bibsonomy.search.es.search.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Set;

import org.apache.lucene.search.join.ScoreMode;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.services.searcher.query.PostSearchQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.Operator;
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
public class ElasticsearchPublicationSearch<P extends BibTex> extends ElasticsearchPostSearch<P> {

	@Override
	protected void buildResourceSpecificQuery(BoolQueryBuilder mainQueryBuilder, String loggedinUser, PostSearchQuery<?> postQuery) {
		super.buildResourceSpecificQuery(mainQueryBuilder, loggedinUser, postQuery);

		final String authorSearchTerms = postQuery.getAuthorSearchTerms();
		if (present(authorSearchTerms)) {
			final QueryBuilder authorSearchQuery = buildPersonNameQuery(authorSearchTerms, false);
			final NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(Fields.Publication.AUTHORS, authorSearchQuery, ScoreMode.Total);
			mainQueryBuilder.must(nestedQuery);
		}

		final String bibtexKey = postQuery.getBibtexKey();
		if (present(bibtexKey)) {
			final QueryBuilder bibtexKeyQuery = QueryBuilders.termQuery(Fields.Publication.BIBTEXKEY, bibtexKey);
			mainQueryBuilder.must(bibtexKeyQuery);
		}

		/*
		 * find publications that are not assigned to a person but match one of the person names
		 */
		final List<PersonName> personNames = postQuery.getPersonNames();
		final boolean onlyIncludeAuthorsWithoutPersonId = postQuery.isOnlyIncludeAuthorsWithoutPersonId();
		if (present(personNames)) {
			final BoolQueryBuilder personNameQuery = QueryBuilders.boolQuery();

			for (final PersonName personName : personNames) {
				final QueryBuilder personNameSearchQuery = buildPersonNameQuery(personName.toString(), onlyIncludeAuthorsWithoutPersonId);
				personNameQuery.should(personNameSearchQuery);
			}

			final NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(Fields.Publication.AUTHORS, personNameQuery, ScoreMode.Total);
			mainQueryBuilder.must(nestedQuery);
		}
	}

	private static QueryBuilder buildPersonNameQuery(String authorName, boolean onlyIncludeAuthorsWithoutPersonId) {
		final MatchQueryBuilder matchQuery = QueryBuilders.matchQuery(Fields.Publication.AUTHORS + "." + Fields.Publication.PERSON_NAME, authorName).operator(Operator.AND);

		if (!onlyIncludeAuthorsWithoutPersonId) {
			return matchQuery;
		}

		final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder.must(matchQuery);
		boolQueryBuilder.mustNot(QueryBuilders.existsQuery(Fields.Publication.AUTHORS + "." + Fields.Publication.PERSON_ID));

		return boolQueryBuilder;
	}


	@Override
	protected void buildResourceSpecifiyFilters(BoolQueryBuilder mainFilterBuilder, String loggedinUser, Set<String> allowedGroups, Set<String> usersThatShareDocs, PostSearchQuery<?> postQuery) {
		super.buildResourceSpecifiyFilters(mainFilterBuilder, loggedinUser, allowedGroups, usersThatShareDocs, postQuery);

		final String year = postQuery.getYear();
		final String lastYear = postQuery.getLastYear();
		final String firstYear = postQuery.getFirstYear();

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

		/*
		 * entry type filter
		 */
		final String entryType = postQuery.getEntryType();
		if (present(entryType)) {
			final MatchQueryBuilder entryTypeMatch = QueryBuilders.matchQuery(Fields.Publication.ENTRY_TYPE, entryType);
			mainFilterBuilder.must(entryTypeMatch);
		}

		final Set<Filter> filters = postQuery.getFilters();
		if (present(filters)) {
			/*
			 * only return documents where users attached documents
			 * but only show posts of users that share documents with the loggedin user
			 */
			if (filters.contains(FilterEntity.JUST_PDF)) {
				final ExistsQueryBuilder docFieldExists = QueryBuilders.existsQuery(Fields.Publication.DOCUMENTS);
				final BoolQueryBuilder docFilter = QueryBuilders.boolQuery();
				docFilter.must(docFieldExists);
				usersThatShareDocs.stream().map(user -> QueryBuilders.matchQuery(Fields.USER_NAME, user)).forEach(docFilter::must);
				mainFilterBuilder.must(docFilter);
			}
		}
	}
}
