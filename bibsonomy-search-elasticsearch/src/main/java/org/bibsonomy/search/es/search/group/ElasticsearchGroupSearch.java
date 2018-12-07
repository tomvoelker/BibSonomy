package org.bibsonomy.search.es.search.group;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.GroupQuery;
import org.bibsonomy.search.es.index.converter.group.GroupFields;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.search.AbstractElasticsearchSearch;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.services.searcher.GroupSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * group search implementation for elasticsearch
 *
 * @author dzo
 */
public class ElasticsearchGroupSearch extends AbstractElasticsearchSearch<Group, GroupQuery, DefaultSearchIndexSyncState, Object> implements GroupSearch {

	/**
	 * default constructor
	 *
	 * @param manager
	 * @param converter
	 */
	public ElasticsearchGroupSearch(ElasticsearchManager<Group, DefaultSearchIndexSyncState> manager, Converter<Group, Map<String, Object>, Object> converter) {
		super(manager, converter);
	}

	@Override
	public List<Group> getGroups(User loggedinUser, GroupQuery query) {
		return searchEntities(loggedinUser, query);
	}

	@Override
	protected BoolQueryBuilder buildFilterQuery(User loggedinUser, GroupQuery query) {
		final BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
		final Prefix prefix = query.getPrefix();
		if (present(prefix)) {
			filterQuery.must(ElasticsearchIndexSearchUtils.buildPrefixFilter(prefix, GroupFields.REALNAME_LOWERCASE));
		}

		return filterQuery;
	}
}
