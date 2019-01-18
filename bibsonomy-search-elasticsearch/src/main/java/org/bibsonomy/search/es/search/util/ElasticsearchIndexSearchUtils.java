package org.bibsonomy.search.es.search.util;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.search.InvalidSearchRequestException;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.sort.SortOrder;

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
	private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]].*");

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
	 * @return short cut for callSearch
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
}
