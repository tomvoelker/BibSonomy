package org.bibsonomy.search.es.search.project;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.ProjectOrder;
import org.bibsonomy.model.enums.ProjectStatus;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.logic.query.util.BasicQueryUtils;
import org.bibsonomy.search.es.index.converter.project.ProjectConverter;
import org.bibsonomy.search.es.index.converter.project.ProjectFields;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.services.searcher.ProjectSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Date;
import java.util.List;

/**
 * elasticsearch implementation of the {@link ProjectSearch} interface
 *
 * @author dzo
 */
public class ElasticsearchProjectSearch implements ProjectSearch {

	private ElasticsearchManager<Project, SearchIndexSyncState> manager;
	private ProjectConverter converter;

	/**
	 * default constructor with all required fields
	 * @param manager
	 * @param converter
	 */
	public ElasticsearchProjectSearch(final ElasticsearchManager<Project, SearchIndexSyncState> manager, final ProjectConverter converter) {
		this.manager = manager;
		this.converter = converter;
	}

	@Override
	public List<Project> getProjects(final User loggedinUser, ProjectQuery query) {
		final ResultList<Project> postList = ElasticsearchIndexSearchUtils.callSearch(() -> {
			final ResultList<Project> projects = new ResultList<>();
			final QueryBuilder queryBuilder = this.buildQuery(loggedinUser, query);
			if (queryBuilder == null) {
				return projects;
			}

			final SortOrder sortOrderQuery = ElasticsearchIndexSearchUtils.convertSortOrder(query.getSortOrder());
			final Pair<String, SortOrder> sortOrder = query.getOrder() == ProjectOrder.START_DATE ? new Pair<>(ProjectFields.START_DATE, sortOrderQuery) : new Pair<>(ProjectFields.TITLE + "." + ProjectFields.TITLE_SORT, sortOrderQuery);

			final int offset = BasicQueryUtils.calcOffset(query);
			final int limit = BasicQueryUtils.calcLimit(query);
			final SearchHits hits = this.manager.search(queryBuilder, sortOrder, offset, limit, null, null);

			if (hits == null) {
				return projects;
			}

			projects.setTotalCount((int) hits.getTotalHits());

			for (final SearchHit hit : hits) {
				final Project project = this.converter.convert(hit.getSourceAsMap(), Role.ADMIN.equals(loggedinUser.getRole()));
				projects.add(project);
			}

			return projects;
		});
		return postList;
	}

	private QueryBuilder buildQuery(final User loggedinUser, final ProjectQuery query) {
		final BoolQueryBuilder mainQuery = QueryBuilders.boolQuery();
		final BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
		final String search = query.getSearch();
		if (present(search)) {
			final QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(search);
			this.manager.getPublicFields().stream().forEach(queryStringQueryBuilder::field);
			mainQuery.must(queryStringQueryBuilder);
		}

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

		return mainQuery.filter(filterQuery);
	}
}
