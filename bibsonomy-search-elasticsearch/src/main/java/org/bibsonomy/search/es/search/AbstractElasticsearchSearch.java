package org.bibsonomy.search.es.search;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Map;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.BasicQuery;
import org.bibsonomy.model.logic.query.util.BasicQueryUtils;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Converter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

/**
 * abstract class for elasticsearch search implementations
 *
 * @author dzo
 */
public abstract class AbstractElasticsearchSearch<T, Q extends BasicQuery, S extends SearchIndexSyncState, O> {

	private final ElasticsearchManager<T, S> manager;
	private final Converter<T, Map<String, Object>, O> converter;

	/**
	 * default constructor
	 * @param manager
	 * @param converter
	 */
	public AbstractElasticsearchSearch(ElasticsearchManager<T, S> manager, Converter<T, Map<String, Object>, O> converter) {
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
			
			final Pair<String, SortOrder> sortOrder = this.getSortOrder(query);

			final int offset = BasicQueryUtils.calcOffset(query);
			final int limit = BasicQueryUtils.calcLimit(query);
			final SearchHits hits = this.manager.search(queryBuilder, sortOrder, offset, limit, null, null);

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

	protected Pair<String, SortOrder> getSortOrder(Q query) {
		return null;
	}

	protected O getConversionOptions(User loggedinUser) {
		return null;
	}

	/**
	 * builds the query
	 * @param loggedinUser
	 * @param query
	 * @return
	 */
	protected final QueryBuilder buildQuery(User loggedinUser, Q query) {
		final BoolQueryBuilder mainQuery = this.buildMainQuery(loggedinUser, query);
		final BoolQueryBuilder filterQuery = this.buildFilterQuery(loggedinUser, query);

		// now some general search queries
		final String search = query.getSearch();
		if (present(search)) {
			final QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(search);
			this.manager.getPublicFields().stream().forEach(queryStringQueryBuilder::field);
			queryStringQueryBuilder.type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX);
			mainQuery.must(queryStringQueryBuilder);
		}

		return mainQuery.filter(filterQuery);
	}

	protected BoolQueryBuilder buildMainQuery(User loggedinUser, Q query) {
		return QueryBuilders.boolQuery();
	}

	protected BoolQueryBuilder buildFilterQuery(User loggedinUser, Q query) {
		return QueryBuilders.boolQuery();
	}
}
