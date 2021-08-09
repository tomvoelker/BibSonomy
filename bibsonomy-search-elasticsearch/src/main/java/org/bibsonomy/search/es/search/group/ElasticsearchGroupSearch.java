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

import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.services.searcher.GroupSearch;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.GroupSortKey;
import org.bibsonomy.model.logic.query.GroupQuery;
import org.bibsonomy.search.es.index.converter.group.GroupFields;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.search.AbstractElasticsearchSearch;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.util.Converter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
	protected List<Pair<String, SortOrder>> getSortOrder(final GroupQuery query) {
		final SortOrder sortOrder = ElasticsearchIndexSearchUtils.convertSortOrder(query.getSortOrder());
		final GroupSortKey order = query.getGroupOrder();
		if (present(order)) {
			switch (order) {
				case GROUP_NAME:
					return Collections.singletonList(new Pair<>(GroupFields.NAME, sortOrder));
				case GROUP_REALNAME:
					// here we add the name as a second search order to handle groups without real names
					return Arrays.asList(
							new Pair<>(ESConstants.getRawField(GroupFields.REALNAME), sortOrder),
							new Pair<>(GroupFields.NAME, sortOrder)
							);
				case RANK:
					return null; // default order is rank
			}
			throw new IllegalArgumentException("order '" + order + "' not supported");
		}

		return super.getSortOrder(query);
	}

	@Override
	protected BoolQueryBuilder buildFilterQuery(final User loggedinUser, final GroupQuery query) {
		final BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
		final Prefix prefix = query.getPrefix();
		if (present(prefix)) {
			filterQuery.must(ElasticsearchIndexSearchUtils.buildPrefixFilter(prefix, GroupFields.REALNAME_PREFIX));
		}

		final Boolean organization = query.getOrganization();
		if (present(organization)) {
			filterQuery.must(QueryBuilders.termQuery(GroupFields.ORGANIZATION, organization));
		}

		return filterQuery;
	}
}
