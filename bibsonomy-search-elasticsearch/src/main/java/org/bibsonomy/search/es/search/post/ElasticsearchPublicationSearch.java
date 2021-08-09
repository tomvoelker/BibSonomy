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
package org.bibsonomy.search.es.search.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.search.join.ScoreMode;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.es.index.converter.project.ProjectFields;
import org.bibsonomy.services.searcher.PostSearchQuery;
import org.bibsonomy.model.SystemTag;
import org.bibsonomy.database.systemstags.SystemTagsExtractor;
import org.bibsonomy.database.systemstags.search.AuthorSystemTag;
import org.bibsonomy.database.systemstags.search.BibTexKeySystemTag;
import org.bibsonomy.database.systemstags.search.EntryTypeSystemTag;
import org.bibsonomy.database.systemstags.search.TitleSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.util.object.FieldDescriptor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;


/**
 * handles publication relevant search
 * 
 * @author dzo
 * @param <P> 
 */
public class ElasticsearchPublicationSearch<P extends BibTex> extends ElasticsearchPostSearch<P> {


	private static final String ENTRYTYPE_AGGREGATION_ID = "distinct_entrytypes";
	private static final String YEAR_AGGREGATION_ID = "year";
	private static final String AUTHOR_FIELD_ID = "author";

	protected static final Map<String, String> FIELD_MAPPER = new HashMap<>();
	static {
		FIELD_MAPPER.put(BibTex.ENTRYTYPE_FIELD_NAME, Fields.Publication.ENTRY_TYPE);
		FIELD_MAPPER.put(BibTex.YEAR_FIELD_NAME, Fields.Publication.YEAR);
		// FIELD_MAPPER.put(BibTex.AUTHOR_FIELD_NAME, Fields.Publication.AUTHORS);
	}

	@Override
	protected void buildResourceSpecificQuery(BoolQueryBuilder mainQueryBuilder, String loggedinUser, PostSearchQuery<?> postQuery) {
		super.buildResourceSpecificQuery(mainQueryBuilder, loggedinUser, postQuery);

		final String authorSearchTerms = postQuery.getAuthorSearchTerms();
		if (present(authorSearchTerms)) {
			final QueryBuilder authorSearchQuery = QueryBuilders.matchQuery(Fields.Publication.AUTHORS + "." + Fields.Publication.PERSON_NAME, authorSearchTerms).operator(Operator.AND);
			final NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(Fields.Publication.AUTHORS, authorSearchQuery, ScoreMode.Total);
			mainQueryBuilder.must(nestedQuery);
		}

		final String bibtexKey = postQuery.getBibtexKey();
		if (present(bibtexKey)) {
			final QueryBuilder bibtexKeyQuery = QueryBuilders.termQuery(Fields.Publication.BIBTEXKEY, bibtexKey);
			mainQueryBuilder.must(bibtexKeyQuery);
		}

		/*
		 * find publications that are not assigned to a person but match one of the person names
		 */
		final List<PersonName> personNames = postQuery.getPersonNames();
		final boolean onlyIncludeAuthorsWithoutPersonId = postQuery.isOnlyIncludeAuthorsWithoutPersonId();
		if (present(personNames)) {
			final BoolQueryBuilder personNameQuery = QueryBuilders.boolQuery();

			for (final PersonName personName : personNames) {
				final QueryBuilder personNameSearchQuery = buildPersonNameQuery(personName.toString(), onlyIncludeAuthorsWithoutPersonId);
				personNameQuery.should(personNameSearchQuery);
			}

			final NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(Fields.Publication.AUTHORS, personNameQuery, ScoreMode.Total);
			mainQueryBuilder.must(nestedQuery);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.search.post.EsResourceSearch#buildResourceSpecificFilters(org.elasticsearch.index.query.BoolFilterBuilder, java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */

	private static QueryBuilder buildPersonNameQuery(String authorName, boolean onlyIncludeAuthorsWithoutPersonId) {
		final MatchQueryBuilder matchQuery = QueryBuilders.matchQuery(Fields.Publication.AUTHORS + "." + Fields.Publication.PERSON_NAME, authorName).operator(Operator.AND);

		if (!onlyIncludeAuthorsWithoutPersonId) {
			return matchQuery;
		}

		final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder.must(matchQuery);
		boolQueryBuilder.mustNot(QueryBuilders.existsQuery(Fields.Publication.AUTHORS + "." + Fields.Publication.PERSON_ID));

		return boolQueryBuilder;
	}

	@Override
	protected List<Pair<String, SortOrder>> buildResourceSpecificSortParameters(final List<SortCriteria> sortCriteria) {
		final List<Pair<String, SortOrder>> sortParameters = new ArrayList<>();
		if (!present(sortCriteria)) {
			return sortParameters;
		}
		for (final SortCriteria sortCrit : sortCriteria) {
			final SortOrder esSortOrder = SortOrder.fromString(sortCrit.getSortOrder().toString());
			final SortKey sortKey = sortCrit.getSortKey();
			switch (sortKey) {
				// ignore these Order type since result of no sort parameters
				case RANK:
				case NONE:
					break;
				// Order type with cleaned up index attribute
				case TITLE:
					sortParameters.add(new Pair<>(Fields.Sort.TITLE, esSortOrder));
					break;
				case BOOKTITLE:
					sortParameters.add(new Pair<>(Fields.Sort.BOOKTITLE, esSortOrder));
					break;
				case JOURNAL:
					sortParameters.add(new Pair<>(Fields.Sort.JOURNAL, esSortOrder));
					break;
				case SERIES:
					sortParameters.add(new Pair<>(Fields.Sort.SERIES, esSortOrder));
					break;
				case PUBLISHER:
					sortParameters.add(new Pair<>(Fields.Sort.PUBLISHER, esSortOrder));
					break;
				case AUTHOR:
					sortParameters.add(new Pair<>(Fields.Sort.AUTHOR, esSortOrder));
					break;
				case EDITOR:
					sortParameters.add(new Pair<>(Fields.Sort.EDITOR, esSortOrder));
					break;
				case SCHOOL:
					sortParameters.add(new Pair<>(Fields.Sort.SCHOOL, esSortOrder));
					break;
				case INSTITUTION:
					sortParameters.add(new Pair<>(Fields.Sort.INSTITUTION, esSortOrder));
					break;
				case ORGANIZATION:
					sortParameters.add(new Pair<>(Fields.Sort.ORGANIZATION, esSortOrder));
					break;
				case YEAR:
					sortParameters.add(new Pair<>(Fields.Publication.YEAR, esSortOrder));
					break;
				case PUBDATE:
					sortParameters.add(new Pair<>(Fields.Publication.YEAR, esSortOrder));
					sortParameters.add(new Pair<>(Fields.Publication.MONTH, esSortOrder));
					sortParameters.add(new Pair<>(Fields.Publication.DAY, esSortOrder));
					break;
				// more complex order types possible here
				default:
					sortParameters.add(new Pair<>(sortKey.toString().toLowerCase(), esSortOrder));
					break;
			}
		}
		return sortParameters;
	}

	@Override
	protected BoolQueryBuilder buildResourceSpecificFilters(BoolQueryBuilder mainFilterBuilder, String loggedinUser, Set<String> allowedGroups, Set<String> usersThatShareDocs, PostSearchQuery<?> postQuery) {
		final BoolQueryBuilder filterBuilder = super.buildResourceSpecificFilters(mainFilterBuilder, loggedinUser, allowedGroups, usersThatShareDocs, postQuery);

		final String year = postQuery.getYear();
		final String lastYear = postQuery.getLastYear();
		final String firstYear = postQuery.getFirstYear();

		if (present(year)) {
			final TermQueryBuilder yearQuery = QueryBuilders.termQuery(Fields.Publication.YEAR, year);
			filterBuilder.must(yearQuery);
		}

		final boolean presentLastYear = present(lastYear);
		final boolean presentFirstYear = present(firstYear);
		if (presentLastYear || presentFirstYear) {
			final RangeQueryBuilder rangeFilter = QueryBuilders.rangeQuery(Fields.Publication.YEAR);
			if (presentFirstYear) {
				rangeFilter.gte(Integer.parseInt(firstYear));
			}

			if (presentLastYear) {
				rangeFilter.lte(Integer.parseInt(lastYear));
			}
			filterBuilder.must(rangeFilter);
		}

		/*
		 * entry type filter
		 */
		final String entryType = postQuery.getEntryType();
		if (present(entryType)) {
			final MatchQueryBuilder entryTypeMatch = QueryBuilders.matchQuery(Fields.Publication.ENTRY_TYPE, entryType);
			filterBuilder.must(entryTypeMatch);
		}

		final Set<Filter> filters = postQuery.getFilters();
		if (present(filters)) {
			/*
			 * only return documents where users attached documents
			 * but only show posts of users that share documents with the loggedin user
			 */
			if (filters.contains(FilterEntity.JUST_PDF)) {
				final QueryBuilder docSearchQuery = QueryBuilders.existsQuery(Fields.Publication.DOCUMENTS);
				final NestedQueryBuilder docFieldExists = QueryBuilders.nestedQuery(Fields.Publication.DOCUMENTS, docSearchQuery, ScoreMode.Total);
				final BoolQueryBuilder docFilter = QueryBuilders.boolQuery();
				docFilter.must(docFieldExists);
				usersThatShareDocs.stream().map(user -> QueryBuilders.matchQuery(Fields.USER_NAME, user)).forEach(docFilter::should);
				filterBuilder.must(docFilter);
			}
		}

		/*
		 * for a cris system we only want publications of persons that are associated with the college
		 */
		final String college = postQuery.getCollege();

		if (present(college)) {
			final BoolQueryBuilder collegeFilter = QueryBuilders.boolQuery();
			final QueryBuilder collegeAuthorFilter = buildCollegeTermFilter(Fields.Publication.AUTHORS, college);
			final QueryBuilder collegeEditorFilter = buildCollegeTermFilter(Fields.Publication.EDITORS, college);
			collegeFilter.should(collegeAuthorFilter).should(collegeEditorFilter);
			filterBuilder.must(collegeFilter);
		}

		/*
		 * filter publications for an organization
		 */
		final GroupingEntity grouping = postQuery.getGrouping();
		if (GroupingEntity.ORGANIZATION.equals(grouping)) {
			final String groupingName = postQuery.getGroupingName();
			// no persons assigned to this organization
			final Set<String> personIds = this.infoLogic.getPersonsOfOrganization(groupingName);
			if (!present(personIds)) {
				return null;
			}

			final BoolQueryBuilder organizationQuery = QueryBuilders.boolQuery();
			personIds.stream().map(ElasticsearchPublicationSearch::buildPersonFilter).forEach(organizationQuery::should);
			filterBuilder.must(organizationQuery);
		}

		if (GroupingEntity.PERSON.equals(grouping)) {
			final String groupingName = postQuery.getGroupingName();

			final QueryBuilder personFilter = buildPersonFilter(groupingName);
			filterBuilder.must(personFilter);
		}

		applySystemTagFilters(mainFilterBuilder, postQuery.getSystemTags());

		return filterBuilder;
	}

	private void applySystemTagFilters(BoolQueryBuilder filterBuilder, List<SystemTag> systemTags ) {
		// add system tags to the query builder
		final List<SystemTag> authorTags = SystemTagsExtractor.extractSystemTags(systemTags, AuthorSystemTag.NAME);
		final List<SystemTag> titleTags = SystemTagsExtractor.extractSystemTags(systemTags, TitleSystemTag.NAME);
		final List<SystemTag> bibtexTags = SystemTagsExtractor.extractSystemTags(systemTags, BibTexKeySystemTag.NAME);
		final List<SystemTag> entrytypeTags = SystemTagsExtractor.extractSystemTags(systemTags, EntryTypeSystemTag.NAME);

		if (present(authorTags)) {
			/* TODO fix me @kch
			final QueryBuilder authorSearchQuery = QueryBuilders.matchQuery(Fields.Publication.AUTHORS + "." + Fields.Publication.PERSON_NAME, authorSearchTerms).operator(Operator.AND);
			final NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(Fields.Publication.AUTHORS, authorSearchQuery, ScoreMode.Total);
			filterBuilder.must(nestedQuery);
			 */
		}

		if (present(titleTags)) {
			final QueryBuilder titleQuery = QueryBuilders.matchQuery(Fields.Resource.TITLE, titleTags.get(0).getArgument());
			filterBuilder.must(titleQuery);
		}

		if (present(bibtexTags)) {
			final QueryBuilder bibtexQuery = QueryBuilders.matchQuery(Fields.Publication.BIBTEXKEY, bibtexTags.get(0).getArgument());
			filterBuilder.must(bibtexQuery);
		}

		if (present(entrytypeTags)) {
			final QueryBuilder entryTypeQuery = QueryBuilders.matchQuery(Fields.Publication.ENTRY_TYPE, entrytypeTags.get(0).getArgument()).operator(Operator.OR);
			filterBuilder.must(entryTypeQuery);
		}
	}

	private static QueryBuilder buildPersonFilter(final String personId) {
		final QueryBuilder authorQuery = buildPersonFilter(Fields.Publication.AUTHORS, personId);
		final QueryBuilder editorQuery = buildPersonFilter(Fields.Publication.EDITORS, personId);
		return QueryBuilders.boolQuery().should(authorQuery).should(editorQuery);
	}

	private static QueryBuilder buildPersonFilter(final String field, final String personId) {
		return buildNestedTermQuer(field, Fields.Publication.PERSON_ID, personId);
	}

	private static QueryBuilder buildCollegeTermFilter(final String field, final String college) {
		return buildNestedTermQuer(field, Fields.Publication.PERSON_COLLEGE, college);
	}

	private static QueryBuilder buildNestedTermQuer(final String field, final String nestedField, final String value) {
		return QueryBuilders.nestedQuery(field, QueryBuilders.termQuery(field + "." + nestedField, value), ScoreMode.None);
	}
}
