/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import java.util.List;
import java.util.Map;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.BasicQuery;
import org.bibsonomy.model.logic.query.util.BasicQueryUtils;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.util.Converter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

/**
 * abstract class for elasticsearch search implementations
 *
 * @author dzo
 */
public abstract class AbstractElasticsearchSearch<T, Q extends BasicQuery, S extends SearchIndexState, O> {

	protected final ElasticsearchManager<T, S> manager;
	private final Converter<T, Map<String, Object>, O> converter;

	/**
	 * default constructor
	 * @param manager the manager to use for searching
	 * @param converter the converter to use for converting es hits to our model
	 */
	public AbstractElasticsearchSearch(final ElasticsearchManager<T, S> manager, final Converter<T, Map<String, Object>, O> converter) {
		this.manager = manager;
		this.converter = converter;
	}

	protected List<T> searchEntities(final User loggedinUser, final Q query) {
		return ElasticsearchIndexSearchUtils.callSearch(() -> {
			final ResultList<T> results = new ResultList<>();
			final QueryBuilder queryBuilder = this.buildQuery(loggedinUser, query);
			if (queryBuilder == null) {
				return results;
			}
			
			final List<Pair<String, SortOrder>> sortCriteria = this.getSortCriteria(query);

			/*
			 * there is a limit in the es search how many entries we can skip (max result window)
			 * here we check the limit set for the index
			 * we do the following:
			 * 1. we set this information e.g. for the view
			 * 2. if the start already exceeds the limit we return an empty result list
			 * 3. if the end only exceeds the limit we set it to the max result window
			 */
			final Settings indexSettings = this.manager.getIndexSettings();
			final Integer maxResultWindow = indexSettings.getAsInt("index.max_result_window", 10000);
			results.setPaginationLimit(maxResultWindow);

			if (query.getStart() > maxResultWindow) {
				return results;
			}

			final int offset = BasicQueryUtils.calcOffset(query);
			final int limit = BasicQueryUtils.calcLimit(query, maxResultWindow);
			final SearchHits hits = this.manager.search(queryBuilder, sortCriteria, offset, limit, null, null);

			if (hits == null) {
				return results;
			}

			results.setTotalCount((int) hits.getTotalHits());

			for (final SearchHit hit : hits) {
				final T result = this.converter.convert(hit.getSourceAsMap(), this.getConversionOptions(loggedinUser));
				results.add(result);
			}

			return results;
		});
	}

	protected Statistics statisticsForSearch(final User loggedinUser, final Q query) {
		final Statistics statistics = new Statistics();
		final QueryBuilder queryBuilder = this.buildQuery(loggedinUser, query);
		if (!present(queryBuilder)) {
			return statistics;
		}

		return ElasticsearchIndexSearchUtils.callSearch(() -> {
			final long documentCount = this.manager.getDocumentCount(queryBuilder);
			statistics.setCount((int) documentCount);
			return statistics;
		}, statistics);
	}

	protected List<Pair<String, SortOrder>> getSortCriteria(final Q query) {
		return null;
	}

	protected O getConversionOptions(final User loggedinUser) {
		return null;
	}

	/**
	 * builds the query
	 * @param loggedinUser
	 * @param query
	 * @return
	 */
	protected final QueryBuilder buildQuery(final User loggedinUser, final Q query) {
		final BoolQueryBuilder mainQuery = this.buildMainQuery(loggedinUser, query);
		final BoolQueryBuilder filterQuery = this.buildFilterQuery(loggedinUser, query);

		/*
		 * XXX: e.g. when a group/organization has no persons and we want to filter for projects of the connected persons
		 * we must have a way to indicate there is no match; so the filter query returns null
		 */
		if (!present(filterQuery)) {
			return null;
		}

		// now some general search queries
		final String search = query.getSearch();
		if (present(search)) {
			final QueryBuilder searchQueryBuilder = this.buildSearchQueryBuilder(query);
			mainQuery.must(searchQueryBuilder);
		}

		return mainQuery.filter(filterQuery);
	}

	private QueryBuilder buildSearchQueryBuilder(final Q query) {
		final String search = query.getSearch();
		final boolean phraseMatch = query.isPhraseMatch();
		final boolean prefixMatch = query.isUsePrefixMatch();

		/*
		 * the search terms must match in the order entered and the last is only a prefix match
		 */
		if (phraseMatch) {
			final MultiMatchQueryBuilder searchQueryBuilder = QueryBuilders.multiMatchQuery(search);
			this.manager.getPublicFields().forEach(searchQueryBuilder::field);
			searchQueryBuilder.minimumShouldMatch("75%");
			if (!prefixMatch) {
				return searchQueryBuilder;
			}
			// prefix config
			searchQueryBuilder
					.type(MatchQuery.Type.PHRASE_PREFIX);

			return searchQueryBuilder;
		}

		return ElasticsearchIndexSearchUtils.buildMultiBoolMatchPrefixQuery(search, this.manager.getPublicFields());
	}

	protected BoolQueryBuilder buildMainQuery(final User loggedinUser, final Q query) {
		return QueryBuilders.boolQuery();
	}

	protected BoolQueryBuilder buildFilterQuery(final User loggedinUser, final Q query) {
		return QueryBuilders.boolQuery();
	}
}
