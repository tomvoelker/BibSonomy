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
package org.bibsonomy.search.es.search.group;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.GroupSortKey;
import org.bibsonomy.model.logic.query.GroupQuery;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.index.converter.group.GroupFields;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.search.AbstractElasticsearchSearch;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.services.searcher.GroupSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * group search implementation for elasticsearch
 *
 * @author dzo
 */
public class ElasticsearchGroupSearch extends AbstractElasticsearchSearch<Group, GroupQuery, DefaultSearchIndexSyncState, Object> implements GroupSearch {

	/**
	 * default constructor
	 *
	 * @param manager the manager that is responsible for this search
	 * @param converter the converter for converting elasticsearch documents to
	 */
	public ElasticsearchGroupSearch(final ElasticsearchManager<Group, DefaultSearchIndexSyncState> manager, final Converter<Group, Map<String, Object>, Object> converter) {
		super(manager, converter);
	}

	@Override
	public List<Group> getGroups(final User loggedinUser, final GroupQuery query) {
		return searchEntities(loggedinUser, query);
	}



	@Override
	protected List<Pair<String, SortOrder>> getSortCriteria(final GroupQuery query) {
		final SortOrder sortOrder = ElasticsearchIndexSearchUtils.convertSortOrder(query.getSortOrder());
		final GroupSortKey sortKey = query.getGroupSortKey();
		if (present(sortKey)) {
			switch (sortKey) {
				case GROUP_NAME:
					return Collections.singletonList(new Pair<>(GroupFields.NAME, sortOrder));
				case GROUP_REALNAME:
					// here we add the name as a second search order to handle groups without real names
					return Arrays.asList(
						new Pair<>(GroupFields.NAME, sortOrder),
						new Pair<>(ESConstants.getRawField(GroupFields.REALNAME), sortOrder)
					);
				case GROUP_SORTNAME:
					return Collections.singletonList(new Pair<>(GroupFields.SORTNAME, sortOrder));
				case RANK:
					return null; // default sort key is rank
			}
			throw new IllegalArgumentException("Sort key '" + sortKey + "' not supported");
		}

		return super.getSortCriteria(query);
	}

	@Override
	protected BoolQueryBuilder buildFilterQuery(final User loggedinUser, final GroupQuery query) {
		final BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
		final Prefix prefix = query.getPrefix();
		if (present(prefix) && prefix != Prefix.ALL) {
			filterQuery.must(ElasticsearchIndexSearchUtils.buildPrefixFilter(prefix, GroupFields.REALNAME_PREFIX));
		}

		final boolean organization = query.isOrganization();
		filterQuery.must(QueryBuilders.termQuery(GroupFields.ORGANIZATION, organization));

		return filterQuery;
	}

	@Override
	protected BoolQueryBuilder buildMainQuery(User loggedinUser, GroupQuery query) {
		final BoolQueryBuilder mainQueryBuilder = super.buildMainQuery(loggedinUser, query);
		final Set<String> realnameSearch = query.getRealnameSearch();
		if (present(realnameSearch)) {
			final QueryStringQueryBuilder queryStringQueryBuilder = buildStringQueryForGroupRealnames(realnameSearch);
			mainQueryBuilder.must(queryStringQueryBuilder);
		}

		return mainQueryBuilder;
	}

	/**
	 *
	 * @param realnameSearch
	 * @return
	 */
	private static QueryStringQueryBuilder buildStringQueryForGroupRealnames(Set<String> realnameSearch) {
		// TODO use match query
		final String field = GroupFields.REALNAME + "." + ESConstants.RAW_SUFFIX;
		final String fieldQuery = String.format("\"%s\"", String.join("\" OR \"", realnameSearch));
		final QueryStringQueryBuilder builder = QueryBuilders.queryStringQuery(String.format("%s:%s", field, fieldQuery))
				.defaultOperator(Operator.OR);
		return builder;
	}
}
