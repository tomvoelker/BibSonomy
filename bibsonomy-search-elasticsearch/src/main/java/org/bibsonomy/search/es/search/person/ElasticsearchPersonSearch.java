package org.bibsonomy.search.es.search.person;

import org.apache.lucene.search.join.ScoreMode;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.logic.query.PersonSuggestionQuery;
import org.bibsonomy.search.es.index.converter.person.PersonConverter;
import org.bibsonomy.search.es.index.converter.person.PersonFields;
import org.bibsonomy.search.es.management.person.ElasticsearchPersonManager;
import org.bibsonomy.services.searcher.PersonSearch;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * elasticsearch implementation of the {@link PersonSearch} interface
 *
 * @author dzo
 */
public class ElasticsearchPersonSearch implements PersonSearch {

	private ElasticsearchPersonManager manager;
	private PersonConverter converter;

	/**
	 * default constructor
	 *
	 * @param manager
	 * @param converter
	 */
	public ElasticsearchPersonSearch(ElasticsearchPersonManager manager, PersonConverter converter) {
		this.manager = manager;
		this.converter = converter;
	}

	@Override
	public List<Person> getPersonSuggestions(final PersonSuggestionQuery query) {
		final BoolQueryBuilder mainQuery = QueryBuilders.boolQuery();
		final MatchQueryBuilder nameQuery = QueryBuilders.matchQuery(PersonFields.NAMES + "." + PersonFields.NAME, query.getQuery());
		nameQuery.operator(Operator.AND);
		final NestedQueryBuilder nestedNamesQuery = QueryBuilders.nestedQuery(PersonFields.NAMES, nameQuery, ScoreMode.Max);

		mainQuery.must(nestedNamesQuery);

		final SearchHits searchHits = this.manager.search(mainQuery, 5, 0);// TODO: set limit
		final LinkedList<Person> persons = new LinkedList<>();
		for (final SearchHit searchHit : searchHits.getHits()) {
			final Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
			final Person person = this.converter.convert(sourceAsMap, null);
			persons.add(person);
		}

		return persons;
	}
}
