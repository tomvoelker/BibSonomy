package org.bibsonomy.search.es.search.project;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.ProjectOrder;
import org.bibsonomy.model.enums.ProjectStatus;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.search.es.index.converter.project.ProjectFields;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.search.AbstractElasticsearchSearch;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.services.searcher.ProjectSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * elasticsearch implementation of the {@link ProjectSearch} interface
 *
 * @author dzo
 */
public class ElasticsearchProjectSearch extends AbstractElasticsearchSearch<Project, ProjectQuery, SearchIndexSyncState, Boolean> implements ProjectSearch {

	/**
	 * default constructor
	 *
	 * @param manager
	 * @param converter
	 */
	public ElasticsearchProjectSearch(ElasticsearchManager<Project, SearchIndexSyncState> manager, Converter<Project, Map<String, Object>, Boolean> converter) {
		super(manager, converter);
	}

	@Override
	public List<Project> getProjects(final User loggedinUser, ProjectQuery query) {
		return this.searchEntities(loggedinUser, query);
	}

	@Override
	protected Pair<String, SortOrder> getSortOrder(ProjectQuery query) {
		final SortOrder sortOrderQuery = ElasticsearchIndexSearchUtils.convertSortOrder(query.getSortOrder());
		return query.getOrder() == ProjectOrder.START_DATE ? new Pair<>(ProjectFields.START_DATE, sortOrderQuery) : new Pair<>(ProjectFields.TITLE + "." + ProjectFields.TITLE_SORT, sortOrderQuery);
	}

	@Override
	protected BoolQueryBuilder buildFilterQuery(User loggedinUser, ProjectQuery query) {
		final BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
		final String type = query.getType();
		if (present(type)) {
			final TermQueryBuilder typeQuery = QueryBuilders.termQuery(ProjectFields.TYPE, type);
			filterQuery.must(typeQuery);
		}

		final ProjectStatus projectStatus = query.getProjectStatus();
		if (present(projectStatus)) {
			final Date now = new Date();
			switch (projectStatus) {
				case RUNNING:
					final RangeQueryBuilder endDateGreaterQuery = QueryBuilders.rangeQuery(ProjectFields.END_DATE);
					endDateGreaterQuery.gt(now);
					filterQuery.must(endDateGreaterQuery);
					break;
				case FINISHED:
					final RangeQueryBuilder projectFinishedRange = QueryBuilders.rangeQuery(ProjectFields.END_DATE);
					projectFinishedRange.lte(now);
					filterQuery.must(projectFinishedRange);
					break;
				default:
					throw new IllegalArgumentException("project status " + projectStatus + " not supported");
			}
		}

		final Prefix prefix = query.getPrefix();
		if (present(prefix)) {
			filterQuery.must(ElasticsearchIndexSearchUtils.buildPrefixFilter(prefix, ProjectFields.TITLE + "." + ProjectFields.TITLE_LOWERCASE));
		}
		return filterQuery;
	}
}
