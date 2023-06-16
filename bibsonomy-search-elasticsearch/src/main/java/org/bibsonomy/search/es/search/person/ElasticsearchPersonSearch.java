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
package org.bibsonomy.search.es.search.person;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.services.searcher.PersonSearch;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonSortKey;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.query.util.BasicQueryUtils;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.index.converter.person.PersonConverter;
import org.bibsonomy.search.es.index.converter.person.PersonFields;
import org.bibsonomy.search.es.index.converter.person.PersonResourceRelationConverter;
import org.bibsonomy.search.es.management.ElasticsearchOneToManyManager;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.util.Sets;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

/**
 * elasticsearch implementation of the {@link PersonSearch} interface
 *
 * @author dzo
 */
public class ElasticsearchPersonSearch implements PersonSearch {

	private final ElasticsearchOneToManyManager<Person, ResourcePersonRelation> manager;
	private final PersonConverter converter;
	private final PersonResourceRelationConverter personResourceRelationConverter;

	/**
	 * default constructor
	 *
	 * @param manager
	 * @param converter
	 * @param personResourceRelationConverter
	 */
	public ElasticsearchPersonSearch(final ElasticsearchOneToManyManager<Person, ResourcePersonRelation> manager, final PersonConverter converter, final PersonResourceRelationConverter personResourceRelationConverter) {
		this.manager = manager;
		this.converter = converter;
		this.personResourceRelationConverter = personResourceRelationConverter;
	}

	@Override
	public Statistics getStatistics(final User loggedinUser, final PersonQuery query) {
		final Statistics statistics = new Statistics();
		return ElasticsearchIndexSearchUtils.callSearch(() -> {
			final BoolQueryBuilder boolQueryBuilder = this.buildQuery(query);
			final long documentCount = this.manager.getDocumentCount(boolQueryBuilder);
			statistics.setCount((int) documentCount);
			return statistics;
		}, statistics);
	}

	@Override
	public List<Person> getPersons(final PersonQuery query) {
		return ElasticsearchIndexSearchUtils.callSearch(() -> {
			final ResultList<Person> persons = new ResultList<>();
			/*
			 * FIXME: copy paste code, refactor PersonQuery to extend BasicQuery to use the AbstractElasticsearchSearch
			 * class
			 * there is a limit in the es search how many entries we can skip (max result window)
			 * here we check the limit set for the index
			 * we do the following:
			 * 1. we set this information e.g. for the view
			 * 2. if the start already exceeds the limit we return an empty result list
			 * 3. if the end only exceeds the limit we set it to the max result window
			 */
			final Settings indexSettings = this.manager.getIndexSettings();
			final Integer maxResultWindow = indexSettings.getAsInt("index.max_result_window", 10000);
			persons.setPaginationLimit(maxResultWindow);

			if (query.getStart() > maxResultWindow) {
				return persons;
			}

			final BoolQueryBuilder mainQuery = this.buildQuery(query);

			final int offset = BasicQueryUtils.calcOffset(query);
			final int limit = BasicQueryUtils.calcLimit(query, maxResultWindow);

			final List<Pair<String, SortOrder>> sortOrders = this.getSortOrders(query);
			final SearchHits searchHits = this.manager.search(mainQuery, sortOrders, offset, limit, null, null);

			for (final SearchHit searchHit : searchHits.getHits()) {
				final Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
				final Person person = this.converter.convert(sourceAsMap, null);
				final Map<String, SearchHits> innerHits = searchHit.getInnerHits();
				if (present(innerHits)) {
					final List<ResourcePersonRelation> resourcePersonRelations = new LinkedList<>();
					final SearchHits resourcePersonRelationHits = innerHits.get(PersonFields.TYPE_RELATION);
					if (present(resourcePersonRelationHits)) {
						final SearchHit[] hits = resourcePersonRelationHits.getHits();
						for (SearchHit hit : hits) {
							final Map<String, Object> personResourceRelationSource = hit.getSourceAsMap();
							final ResourcePersonRelation resourcePersonRelation = this.personResourceRelationConverter.convert(personResourceRelationSource, null);
							resourcePersonRelations.add(resourcePersonRelation);
						}
					}
					person.setResourceRelations(resourcePersonRelations);
				}
				persons.add(person);
			}
			persons.setTotalCount((int) searchHits.totalHits);
			return persons;
		});
	}

	private BoolQueryBuilder buildQuery(final PersonQuery query) {
		final String personQuery = query.getSearch();

		final BoolQueryBuilder mainQuery = QueryBuilders.boolQuery();
		final BoolQueryBuilder filterQuery = this.buildFilterQuery(query);
		if (present(personQuery)) {
			// Build the main search query
			final BoolQueryBuilder mainSearchQuery = QueryBuilders.boolQuery();

			// If quotes are used for an exact search, the query uses a query string. Otherwise uses multi match for more fuzziness
			if (personQuery.chars().filter(ch -> ch == '"').count() > 0) {
				final QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(personQuery)
						.defaultOperator(Operator.AND);
				// set the type to phrase prefix match
				queryStringQueryBuilder.analyzeWildcard(true).tieBreaker(1f);

				mainSearchQuery.should(queryStringQueryBuilder);
			} else {
				/*
				 * maybe some of tokens of the query contain the title of a publication of the author
				 */
				final MultiMatchQueryBuilder resourceRelationQuery = QueryBuilders.multiMatchQuery(personQuery);
				resourceRelationQuery.type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
						.operator(Operator.AND) // "and" here means every term in the query must be in one of the following fields
						.field(PersonFields.RelationFields.POST + "." + ESConstants.Fields.Resource.TITLE, 2.5f)
						.field(PersonFields.RelationFields.POST + "." + ESConstants.Fields.Publication.SCHOOL, 1.3f)
						.tieBreaker(0.8f)
						.boost(4);

				final HasChildQueryBuilder childSearchQuery = JoinQueryBuilders.hasChildQuery(PersonFields.TYPE_RELATION, resourceRelationQuery, ScoreMode.Max);
				mainSearchQuery.should(childSearchQuery);

				final QueryBuilder nameQuery = this.getNameQuery(query);
				mainSearchQuery.should(nameQuery);
			}

			// Inner hits query for relations
			final HasChildQueryBuilder childQuery = JoinQueryBuilders.hasChildQuery(PersonFields.TYPE_RELATION, QueryBuilders.matchAllQuery(), ScoreMode.None);
			final InnerHitBuilder innerHit = new InnerHitBuilder();
			childQuery.innerHit(innerHit);

			// Add to main query
			mainQuery.must(mainSearchQuery);
			mainQuery.should(childQuery);
		}

		if (filterQuery.hasClauses()) {
			mainQuery.filter(filterQuery);
		}

		return mainQuery;
	}

	private BoolQueryBuilder buildFilterQuery(final PersonQuery query) {
		final BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();

		// if no query is provided, filter the entities to only get person results
		//if (!present(query.getQuery())) {
		filterQuery.must(QueryBuilders.termQuery(PersonFields.JOIN_FIELD, PersonFields.TYPE_PERSON));
		//}

		/*
		 * add filters
		 */
		final String college = query.getCollege();
		if (present(college)) {
			final TermQueryBuilder collegeTermQuery = QueryBuilders.termQuery(PersonFields.COLLEGE, college);
			filterQuery.must(collegeTermQuery);
		}

		final Prefix prefix = query.getPrefix();
		if (present(prefix) && prefix != Prefix.ALL) {
			filterQuery.must(ElasticsearchIndexSearchUtils.buildPrefixFilter(prefix, PersonFields.MAIN_NAME_PREFIX));
		}

		return filterQuery;
	}

	private QueryBuilder getNameQuery(final PersonQuery query) {
		final boolean usePrefixMatch = query.isUsePrefixMatch();
		final boolean phraseMatch = query.isPhraseMatch();
		final String searchQuery = query.getSearch();

		/*
		 * the search terms must match in the order entered and the last is only a prefix match
		 */
		if (usePrefixMatch && phraseMatch) {
			final MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(searchQuery, PersonFields.ALL_NAMES, ESConstants.getNgramField(PersonFields.ALL_NAMES));
			multiMatchQueryBuilder.type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX);
			return multiMatchQueryBuilder;
		}

		/*
		 * the search terms should be match in any order and the last token is used in a prefix match
		 */
		if (usePrefixMatch) {
			return ElasticsearchIndexSearchUtils.buildMultiBoolMatchPrefixQuery(searchQuery, Sets.asSet(PersonFields.ALL_NAMES, ESConstants.getNgramField(PersonFields.ALL_NAMES)));
		}

		// the search terms should match given the order, last term no order
		return QueryBuilders.multiMatchQuery(searchQuery, PersonFields.ALL_NAMES, ESConstants.getNgramField(PersonFields.ALL_NAMES));
	}

	private List<Pair<String, SortOrder>> getSortOrders(final PersonQuery query) {
		final PersonSortKey sortKey = query.getSortKey();
		final SortOrder sortOrder = query.getSortOrder().toString().equalsIgnoreCase("ASC") ? SortOrder.ASC : SortOrder.DESC;
		if (present(sortKey)) {
			switch (sortKey) {
				case RANK:
					return null; // rank is the default order
				case MAIN_NAME_LAST_NAME:
					return Collections.singletonList(new Pair<>(PersonFields.MAIN_NAME, SortOrder.ASC));
			}
		}
		return null;
	}
}
