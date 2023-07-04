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
package org.bibsonomy.search.es.search.util;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.search.InvalidSearchRequestException;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * util classes for search instances
 *
 * @author dzo
 */
public class ElasticsearchIndexSearchUtils {

	private static final Log LOG = LogFactory.getLog(ElasticsearchIndexSearchUtils.class);
	private static final Pattern LETTER_PATTERN = Pattern.compile("^[a-z].*", Pattern.CASE_INSENSITIVE);
	private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9].*");

	private ElasticsearchIndexSearchUtils() {
		// noop
	}

	/**
	 * builds the prefix filter for the specified prefix
	 * @param prefix the prefix to filter
	 * @param fieldName the name of the field to filter with the prefix
	 * @return
	 */
	public static QueryBuilder buildPrefixFilter(final Prefix prefix, final String fieldName) {
		return QueryBuilders.termQuery(fieldName, prefix);
	}

	/**
	 * @param string
	 * @return the correct prefix for the provided string
	 */
	public static Prefix getPrefixForString(final String string) {
		if (!present(string)) {
			return null;
		}
		if (LETTER_PATTERN.matcher(string).matches()) {
			return Prefix.valueOf(string.substring(0, 1).toUpperCase());
		}

		if (NUMBER_PATTERN.matcher(string).matches()) {
			return Prefix.NUMBER;
		}

		return Prefix.OTHER;
	}

	/**
	 * @param call
	 * @param <T>
	 * @return shortcut for callSearch
	 */
	public static <T> ResultList<T> callSearch(final Supplier<ResultList<T>> call) {
		return callSearch(call, new ResultList<>());
	}

	/**
	 * method to secure call a search instance
	 * @param call
	 * @param defaultValue
	 * @param <T>
	 * @return
	 */
	public static <T> T callSearch(final Supplier<T> call, final T defaultValue) {
		try {
			return call.get();
		} catch (final ElasticsearchStatusException e) {
			if (!RestStatus.NOT_FOUND.equals(e.status())) {
				LOG.error("unknown error while searching", e);
			} else {
				LOG.error("no index found: ", e);
			}
		} catch (final SearchPhaseExecutionException e) {
			LOG.info("parsing query failed.", e);
			throw new InvalidSearchRequestException();
		}

		return defaultValue;
	}

	/**
	 * converts our {@link org.bibsonomy.common.enums.SortOrder} enum to the elasticsearch {@link SortOrder}
	 * @param sortOrder
	 * @return the converted order, default desc
	 */
	public static SortOrder convertSortOrder(final org.bibsonomy.common.enums.SortOrder sortOrder) {
		if (present(sortOrder)) {
			switch (sortOrder) {
				case ASC:
					return SortOrder.ASC;
				case DESC:
					return SortOrder.DESC;
			}
		}

		return SortOrder.DESC;
	}

	/**
	 * builds a bool match prefix query
	 * here we split the string into tokens and build a boolean query
	 * where the first n-1 tokens will generate term queries and the last token a prefix query
	 *
	 * FIXME: replace this with a bool match prefix query in elasticsearch 7.X
	 *
	 * @param search the search terms
	 * @param field the field term
	 * @return the bool query builder
	 */
	public static BoolQueryBuilder buildBoolMatchPrefixQuery(final String search, final String field) {
		final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		final List<String> tokens = Arrays.asList(search.split(" "));

		final int lastTokenIndex = tokens.size() - 1;
		for (final String term : tokens.subList(0, lastTokenIndex)) {
			boolQueryBuilder.should(QueryBuilders.matchQuery(field, term));
		}

		final String prefix = tokens.get(lastTokenIndex).toLowerCase();
		boolQueryBuilder.should(QueryBuilders.prefixQuery(field, prefix));

		boolQueryBuilder.should(QueryBuilders.matchQuery(field, search).boost(0.75f)); // to score docs with more than one match higher
		return boolQueryBuilder;
	}

	/**
	 * FIXME: replace this with a mulit match bool prefix query in elasticsearch 7.X
	 *
	 * @param search the search terms
	 * @param fields the fields to use
	 * @return the bool mutli bool match prefix query
	 */
	public static QueryBuilder buildMultiBoolMatchPrefixQuery(final String search, final Set<String> fields) {
		final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		final List<String> tokens = Arrays.asList(search.split(" "));

		final int lastTokenIndex = tokens.size() - 1;
		for (final String term : tokens.subList(0, lastTokenIndex)) {
			final MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(term);
			fields.forEach(queryBuilder::field);
			boolQueryBuilder.should(queryBuilder);
		}

		final MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(tokens.get(lastTokenIndex));
		fields.forEach(queryBuilder::field);
		queryBuilder.type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX);
		boolQueryBuilder.should(queryBuilder);

		// config the bool should match
		boolQueryBuilder.minimumShouldMatch("75%");
		boolQueryBuilder.should(QueryBuilders.multiMatchQuery(search).boost(0.75f)); // to score documents with more matches higher
		return boolQueryBuilder;
	}
}
