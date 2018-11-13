package org.bibsonomy.search.es.search.util;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.search.InvalidSearchRequestException;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.sort.SortOrder;

import java.util.function.Supplier;

/**
 * util classes for search instances
 *
 * @author dzo
 */
public class ElasticsearchIndexSearchUtils {

	private static final Log LOG = LogFactory.getLog(ElasticsearchIndexSearchUtils.class);

	private ElasticsearchIndexSearchUtils() {
		// noop
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
	public static SortOrder convertSortOrder(org.bibsonomy.common.enums.SortOrder sortOrder) {
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
