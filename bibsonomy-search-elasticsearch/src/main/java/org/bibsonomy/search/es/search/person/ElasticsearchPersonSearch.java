package org.bibsonomy.search.es.search.person;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.lucene.search.join.ScoreMode;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.services.searcher.PersonSearch;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonOrder;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.query.util.BasicQueryUtils;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.index.converter.person.PersonConverter;
import org.bibsonomy.search.es.index.converter.person.PersonFields;
import org.bibsonomy.search.es.index.converter.person.PersonResourceRelationConverter;
import org.bibsonomy.search.es.management.ElasticsearchOneToManyManager;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.*;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	public ElasticsearchPersonSearch(ElasticsearchOneToManyManager<Person, ResourcePersonRelation> manager, PersonConverter converter, PersonResourceRelationConverter personResourceRelationConverter) {
		this.manager = manager;
		this.converter = converter;
		this.personResourceRelationConverter = personResourceRelationConverter;
	}

	@Override
	public Statistics getStatistics(User loggedinUser, PersonQuery query) {
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
		final String personQuery = query.getQuery();

		final BoolQueryBuilder mainQuery = QueryBuilders.boolQuery();
		final BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
		if (present(personQuery)) {
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

			final HasChildQueryBuilder childQuery = JoinQueryBuilders.hasChildQuery(PersonFields.TYPE_RELATION, QueryBuilders.matchAllQuery(), ScoreMode.None);
			final InnerHitBuilder innerHit = new InnerHitBuilder();
			childQuery.innerHit(innerHit);

			final AbstractQueryBuilder<?> nameQuery = query.isUsePrefixMatch() ? QueryBuilders.matchPhrasePrefixQuery(PersonFields.ALL_NAMES, personQuery) : QueryBuilders.matchQuery(PersonFields.ALL_NAMES, personQuery);

			/*
			 * build the search query
			 */
			final BoolQueryBuilder mainSearchQuery = QueryBuilders.boolQuery();
			mainSearchQuery.should(nameQuery);
			mainSearchQuery.should(childSearchQuery);

			mainQuery.must(mainSearchQuery);
			mainQuery.should(childQuery);
		} else {
			filterQuery.must(QueryBuilders.termQuery(PersonFields.JOIN_FIELD, PersonFields.TYPE_PERSON));
		}

		/*
		 * add filters
		 */
		final String college = query.getCollege();
		if (present(college)) {
			final TermQueryBuilder collegeTermQuery = QueryBuilders.termQuery(PersonFields.COLLEGE, college);
			filterQuery.must(collegeTermQuery);
		}

		final Prefix prefix = query.getPrefix();
		if (present(prefix)) {
			filterQuery.must(ElasticsearchIndexSearchUtils.buildPrefixFilter(prefix, PersonFields.MAIN_NAME_PREFIX));
		}

		if (filterQuery.hasClauses()) {
			mainQuery.filter(filterQuery);
		}
		return mainQuery;
	}

	private List<Pair<String, SortOrder>> getSortOrders(PersonQuery query) {
		final PersonOrder order = query.getOrder();
		if (present(order)) {
			switch (order) {
				case RANK:
					return null; // rank is the default order
				case MAIN_NAME_LAST_NAME:
					return Collections.singletonList(new Pair<>(PersonFields.MAIN_NAME, SortOrder.ASC));
			}
		}
		return null;
	}
}
