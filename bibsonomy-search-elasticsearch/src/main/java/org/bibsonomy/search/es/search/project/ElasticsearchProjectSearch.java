package org.bibsonomy.search.es.search.project;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.search.join.ScoreMode;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.ProjectOrder;
import org.bibsonomy.model.enums.ProjectStatus;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.search.SearchInfoLogic;
import org.bibsonomy.search.es.index.converter.person.PersonFields;
import org.bibsonomy.search.es.index.converter.project.ProjectFields;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.search.AbstractElasticsearchSearch;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.services.searcher.ProjectSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.search.sort.SortOrder;

/**
 * elasticsearch implementation of the {@link ProjectSearch} interface
 *
 * @author dzo
 */
public class ElasticsearchProjectSearch extends AbstractElasticsearchSearch<Project, ProjectQuery, SearchIndexSyncState, Boolean> implements ProjectSearch {

	private final SearchInfoLogic infoLogic;

	/**
	 * default constructor
	 *  @param manager
	 * @param converter
	 * @param infoLogic
	 */
	public ElasticsearchProjectSearch(final ElasticsearchManager<Project, SearchIndexSyncState> manager, final Converter<Project, Map<String, Object>, Boolean> converter, final SearchInfoLogic infoLogic) {
		super(manager, converter);
		this.infoLogic = infoLogic;
	}

	@Override
	public List<Project> getProjects(final User loggedinUser, final ProjectQuery query) {
		return this.searchEntities(loggedinUser, query);
	}

	@Override
	protected Pair<String, SortOrder> getSortOrder(final ProjectQuery query) {
		final SortOrder sortOrderQuery = ElasticsearchIndexSearchUtils.convertSortOrder(query.getSortOrder());
		return query.getOrder() == ProjectOrder.START_DATE ? new Pair<>(ProjectFields.START_DATE, sortOrderQuery) : new Pair<>(ProjectFields.TITLE + "." + ProjectFields.TITLE_SORT, sortOrderQuery);
	}

	@Override
	protected Boolean getConversionOptions(final User loggedinUser) {
		return Role.ADMIN.equals(loggedinUser.getRole());
	}

	@Override
	protected BoolQueryBuilder buildFilterQuery(User loggedinUser, ProjectQuery query) {
		final BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();

		filterQuery.must(QueryBuilders.termQuery(ProjectFields.JOIN_FIELD, ProjectFields.TYPE_PROJECT));

		/*
		 * type and sponsor filters
		 */
		final String type = query.getType();
		if (present(type)) {
			final TermQueryBuilder typeQuery = QueryBuilders.termQuery(ProjectFields.TYPE, type);
			filterQuery.must(typeQuery);
		}

		final String sponsor = query.getSponsor();
		if (present(sponsor)) {
			final TermQueryBuilder sponsorQuery = QueryBuilders.termQuery(ProjectFields.SPONSOR, sponsor);
			filterQuery.must(sponsorQuery);
		}

		final ProjectStatus projectStatus = query.getProjectStatus();
		if (present(projectStatus)) {
			final Date now = new Date();
			switch (projectStatus) {
				case RUNNING:
					final RangeQueryBuilder endDateGreaterQuery = QueryBuilders.rangeQuery(ProjectFields.END_DATE);
					endDateGreaterQuery.gte(now);
					final RangeQueryBuilder startDateQuery = QueryBuilders.rangeQuery(ProjectFields.START_DATE);
					startDateQuery.lte(now);
					filterQuery.must(endDateGreaterQuery).must(startDateQuery);
					break;
				case FINISHED:
					final RangeQueryBuilder projectFinishedRange = QueryBuilders.rangeQuery(ProjectFields.END_DATE);
					projectFinishedRange.lt(now);
					filterQuery.must(projectFinishedRange);
					break;
				case UPCOMING:
					final RangeQueryBuilder projectUpcomingRange = QueryBuilders.rangeQuery(ProjectFields.START_DATE);
					projectUpcomingRange.gt(now);
					filterQuery.must(projectUpcomingRange);
					break;
				default:
					throw new IllegalArgumentException("project status " + projectStatus + " not supported");
			}
		}

		/*
		 * start date and end date filter
		 */
		final Date startDate = query.getStartDate();
		if (present(startDate)) {
			final RangeQueryBuilder startDateFilter = QueryBuilders.rangeQuery(ProjectFields.START_DATE);
			startDateFilter.gte(startDate);
			filterQuery.must(startDateFilter);
		}

		final Date endDate = query.getEndDate();
		if (present(endDate)) {
			final RangeQueryBuilder endDateFilter = QueryBuilders.rangeQuery(ProjectFields.END_DATE);
			endDateFilter.lte(endDate);
			filterQuery.must(endDateFilter);
		}

		final Prefix prefix = query.getPrefix();
		if (present(prefix)) {
			filterQuery.must(ElasticsearchIndexSearchUtils.buildPrefixFilter(prefix, ProjectFields.TITLE_PREFIX));
		}

		final Group organization = query.getOrganization();
		if (present(organization)) {
			final String name = organization.getName();
			final Set<String> personsOfOrganization = this.infoLogic.getPersonsOfOrganization(name);
			if (!present(personsOfOrganization)) {
				return null;
			}

			final BoolQueryBuilder personIdFilter = QueryBuilders.boolQuery();
			personsOfOrganization.stream().map(ElasticsearchProjectSearch::buildPersonFilter).forEach(personIdFilter::should);

			final HasChildQueryBuilder hasChildQueryBuilder = JoinQueryBuilders.hasChildQuery(PersonFields.TYPE_PERSON, personIdFilter, ScoreMode.None);
			filterQuery.must(hasChildQueryBuilder);
		}

		return filterQuery;
	}

	private static QueryBuilder buildPersonFilter(final String personId) {
		return QueryBuilders.termQuery(PersonFields.PERSON_ID, personId);
	}
}
